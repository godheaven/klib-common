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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

class CryptographyUtilsConfigTest {

    @Test
    void nullAlgorithmIsRejected() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> CryptographyUtils.setEncryptKey("secret", null));
        assertTrue(ex.getMessage().toLowerCase().contains("algorithm"));
    }

    @Test
    void configurationFieldsAreVolatile() throws Exception {
        for (String name : new String[]{"encryptKey", "algorithm"}) {
            Field f = CryptographyUtils.class.getDeclaredField(name);
            assertTrue(Modifier.isVolatile(f.getModifiers()), name);
        }
    }

    @Test
    void roundTripAndMalformedInput() {
        CryptographyUtils.setEncryptKey("secret-passphrase", CryptographyUtils.CryptoAlgorithm.AES_GCM);
        String encoded = CryptographyUtils.encrypt("hola");
        assertEquals("hola", CryptographyUtils.decrypt(encoded));
        assertThrows(CryptoException.class, () -> CryptographyUtils.decrypt("v1:pbkdf2:999999999:a:b:c"));
    }

    @Test
    void missingKeyGivesIllegalState() throws Exception {
        Field key = CryptographyUtils.class.getDeclaredField("encryptKey");
        key.setAccessible(true);
        Object previous = key.get(null);
        CryptographyUtils.setEncryptKey("secret-passphrase", CryptographyUtils.CryptoAlgorithm.AES_GCM);
        try {
            key.set(null, null);
            assertThrows(IllegalStateException.class, () -> CryptographyUtils.encrypt("x"));
            assertThrows(IllegalStateException.class, () -> CryptographyUtils.decrypt("x"));
        } finally {
            key.set(null, previous != null ? previous : "secret-passphrase".toCharArray());
        }
    }
}
