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

class CryptoAesTest {

    @Test
    void encryptDecrypt_roundtrip() {
        char[] key = "abcdefghijklmnop".toCharArray();
        String plain = "PlainText123";

        String enc = CryptoAes.encrypt(key, plain);
        Assertions.assertNotNull(enc);
        Assertions.assertFalse(enc.isEmpty());

        String dec = CryptoAes.decrypt(key, enc);
        Assertions.assertEquals(plain, dec);
    }

    @Test
    void decrypt_withWrongKey_throwsCryptoException() {
        char[] key = "abcdefghijklmnop".toCharArray();
        char[] wrong = "ponmlkjihgfedcba".toCharArray();

        String plain = "WillFail";
        String enc = CryptoAes.encrypt(key, plain);

        Assertions.assertThrows(CryptoException.class, () -> CryptoAes.decrypt(wrong, enc));
    }
}

