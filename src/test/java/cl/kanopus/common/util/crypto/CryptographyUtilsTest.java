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

class CryptographyUtilsTest {

    @Test
    void encryptDecrypt_withAlgorithmAESGCM_roundtrip() {
        CryptographyUtils.setEncryptKey("my-secret-passphrase", CryptographyUtils.CryptoAlgorithm.AES_GCM);
        String plain = "Data to encrypt";
        String encoded = CryptographyUtils.encrypt(plain);
        Assertions.assertNotNull(encoded);
        String decoded = CryptographyUtils.decrypt(encoded);
        Assertions.assertEquals(plain, decoded);
    }

    @Test
    void setEncryptKey_withBlank_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> CryptographyUtils.setEncryptKey("  ", CryptographyUtils.CryptoAlgorithm.AES_GCM));
    }

    @Test
    void encrypt_whenAlgorithmNotSet_throwsIllegalStateException() {
        // ensure algorithm not set: set null via reflection is risky; instead call setEncryptKey with unsupported algorithm by simulating via enum hack
        // Since only AES_GCM and AES exist, we simulate unsupported by setting null via a separate call: use valid key but change algorithm variable is not accessible.
        // So instead we test behavior when algorithm is null by clearing via reflection.
        try {
            java.lang.reflect.Field alg = CryptographyUtils.class.getDeclaredField("algorithm");
            alg.setAccessible(true);
            alg.set(null, null);

            java.lang.reflect.Field key = CryptographyUtils.class.getDeclaredField("encryptKey");
            key.setAccessible(true);
            key.set(null, "x".toCharArray());

            Assertions.assertThrows(IllegalStateException.class, () -> CryptographyUtils.encrypt("ok"));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

