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

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Objects;

class CryptoAes {

    // ===== Cryptographic parameters =====
    private static final String AES_TRANSFORM = "AES";

    private CryptoAes() {
    }

    public static String encrypt(char[] encryptKey, String plaintext) {
        Objects.requireNonNull(plaintext, "plaintext");
        Objects.requireNonNull(encryptKey, "encrypt key is required in CryptoAes.encrypt");
        try {

            CharBuffer charBuffer = CharBuffer.wrap(encryptKey);
            ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
            byte[] keyBytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(keyBytes);
            Key aesKey = new SecretKeySpec(keyBytes, AES_TRANSFORM);

            Cipher cipher = Cipher.getInstance(AES_TRANSFORM);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);

            byte[] encrypted = cipher.doFinal(plaintext.getBytes());

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            throw new CryptoException("AES encrypt failed:" + plaintext, ex);
        }
    }

    public static String decrypt(char[] encryptKey, String encoded) {
        Objects.requireNonNull(encoded, "encoded");
        Objects.requireNonNull(encryptKey, "encrypt key is required in CryptoAes.decrypt");
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encoded.replace("\n", ""));

            CharBuffer charBuffer = CharBuffer.wrap(encryptKey);
            ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
            byte[] keyBytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(keyBytes);
            Key aesKey = new SecretKeySpec(keyBytes, AES_TRANSFORM);

            Cipher cipher = Cipher.getInstance(AES_TRANSFORM);
            cipher.init(Cipher.DECRYPT_MODE, aesKey);

            return new String(cipher.doFinal(encryptedBytes));
        } catch (Exception ex) {
            throw new CryptoException("AES decrypt failed", ex);
        }
    }


}
