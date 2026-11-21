/*-
 * !--
 * For support and inquiries regarding this library, please contact:
 *   soporte@kanopus.cl
 *
 * Project website:
 *   https://www.kanopus.cl
 * %%
 * Copyright (C) 2025 Pablo Díaz Saavedra
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * --!
 */
package cl.kanopus.common.util.crypto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CryptoAesGcmSecurityTest {

    private static final char[] KEY = "a-strong-passphrase".toCharArray();

    private static String withIterations(String encoded, String iterations) {
        String[] parts = encoded.split(":");
        parts[2] = iterations;
        return String.join(":", parts);
    }

    @Test
    void rejectsHugeIterationCount() {
        String encoded = CryptoAesGcm.encrypt(KEY, "data");
        long start = System.nanoTime();
        CryptoException ex = assertThrows(CryptoException.class, () -> CryptoAesGcm.decrypt(KEY, withIterations(encoded, String.valueOf(Integer.MAX_VALUE))));
        assertTrue(ex.getMessage().contains("iteration"));
        // must fail fast, without running PBKDF2
        assertTrue(System.nanoTime() - start < 2_000_000_000L);
    }

    @Test
    void rejectsTooLowOrInvalidIterationCount() {
        String encoded = CryptoAesGcm.encrypt(KEY, "data");
        assertThrows(CryptoException.class, () -> CryptoAesGcm.decrypt(KEY, withIterations(encoded, "1")));
        assertThrows(CryptoException.class, () -> CryptoAesGcm.decrypt(KEY, withIterations(encoded, "-5")));
        CryptoException ex = assertThrows(CryptoException.class, () -> CryptoAesGcm.decrypt(KEY, withIterations(encoded, "abc")));
        assertInstanceOf(NumberFormatException.class, ex.getCause());
    }

    @Test
    void wrapsInvalidBase64AndLengths() {
        String encoded = CryptoAesGcm.encrypt(KEY, "data");
        String[] parts = encoded.split(":");
        String badSalt = String.join(":", parts[0], parts[1], parts[2], "!!!", parts[4], parts[5]);
        CryptoException ex = assertThrows(CryptoException.class, () -> CryptoAesGcm.decrypt(KEY, badSalt));
        assertInstanceOf(IllegalArgumentException.class, ex.getCause());

        String shortIv = String.join(":", parts[0], parts[1], parts[2], parts[3], "AAAA", parts[5]);
        assertThrows(CryptoException.class, () -> CryptoAesGcm.decrypt(KEY, shortIv));
    }

    @Test
    void tamperedCiphertextFails() {
        String encoded = CryptoAesGcm.encrypt(KEY, "data");
        String tampered = encoded.substring(0, encoded.length() - 2) + (encoded.endsWith("AA") ? "BB" : "AA");
        assertThrows(CryptoException.class, () -> CryptoAesGcm.decrypt(KEY, tampered));
        assertThrows(CryptoException.class, () -> CryptoAesGcm.decrypt("other".toCharArray(), encoded));
    }

    @Test
    void verifyHashRejectsMalformedInputWithoutThrowing() {
        String stored = CryptoAesGcm.hash("pwd");
        assertTrue(CryptoAesGcm.verifyHash("pwd", stored));
        assertTrue(CryptoAesGcm.matches("pwd", stored));
        assertFalse(CryptoAesGcm.verifyHash("other", stored));
        String[] parts = stored.split(":");
        assertFalse(CryptoAesGcm.verifyHash("pwd", String.join(":", parts[0], parts[1], "abc", parts[3], parts[4])));
        assertFalse(CryptoAesGcm.verifyHash("pwd", String.join(":", parts[0], parts[1], "2000000000", parts[3], parts[4])));
        assertFalse(CryptoAesGcm.verifyHash("pwd", String.join(":", parts[0], parts[1], parts[2], "%%", parts[4])));
        assertFalse(CryptoAesGcm.verifyHash("pwd", "v1:pbkdf2:x"));
    }
}
