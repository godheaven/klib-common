/*-
 * !--
 * For support and inquiries regarding this library, please contact:
 *   soporte@kanopus.cl
 * 
 * Project website:
 *   https://www.kanopus.cl
 * %%
 * Copyright (C) 2025 - 2026 Pablo Díaz Saavedra
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CryptoAesGcmTest {

    @Test
    void encryptDecrypt_roundtrip_withSameKey() {
        char[] key = "0123456789012345".toCharArray();
        String plain = "Hello, Kanopus!";

        String encoded = CryptoAesGcm.encrypt(key, plain);
        Assertions.assertNotNull(encoded);
        Assertions.assertFalse(encoded.isEmpty());

        String decrypted = CryptoAesGcm.decrypt(key, encoded);
        Assertions.assertEquals(plain, decrypted);
    }

    @Test
    void decrypt_badFormat_throwsIllegalArgumentException() {
        char[] key = "0123456789012345".toCharArray();
        Assertions.assertThrows(IllegalArgumentException.class, () -> CryptoAesGcm.decrypt(key, "bad-format"));
    }

    @Test
    void hashAndVerifyHash_behaviour() {
        String password = "s3cr3t-pw";
        String stored = CryptoAesGcm.hash(password);
        Assertions.assertNotNull(stored);
        Assertions.assertTrue(CryptoAesGcm.verifyHash(password, stored));
        Assertions.assertFalse(CryptoAesGcm.verifyHash("wrong", stored));
    }

    @Test
    void matches_isAliasForVerifyHash() {
        String pw = "pw-xyz";
        String stored = CryptoAesGcm.hash(pw);
        Assertions.assertTrue(CryptoAesGcm.matches(pw, stored));
        Assertions.assertFalse(CryptoAesGcm.matches("no", stored));
    }
}

