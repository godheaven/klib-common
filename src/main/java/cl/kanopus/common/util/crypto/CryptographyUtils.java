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

/**
 * Utility class that allows information to be encrypted and decrypted based on the use of seeds.
 *
 * <p>
 * Provides AES-GCM symmetric encryption derived from a passphrase using PBKDF2, along with PBKDF2-based hashing (one-way) suitable for passwords/tokens.
 */
public class CryptographyUtils {

    // State: passphrase (avoid String when possible; use char[])
    private static volatile char[] encryptKey;
    private static volatile CryptoAlgorithm algorithm;

    private CryptographyUtils() {
    }

    // ========= Configuration =========

    /**
     * Set the passphrase used to derive encryption keys.
     *
     * <p>
     * The passphrase should ideally be provided via a secure secret manager. The method copies the value into a char[] for reduced exposure in memory.
     *
     * @param passphrase
     *            the passphrase used to derive AES keys; must be non-empty
     * @param algorithm
     *            the encryption algorithm; must not be null
     * @throws IllegalArgumentException
     *             if passphrase is null or blank, or algorithm is null
     */
    public static void setEncryptKey(String passphrase, CryptoAlgorithm algorithm) {
        if (passphrase == null || passphrase.isBlank()) {
            throw new IllegalArgumentException("Encrypt key must be non-empty.");
        }
        if (algorithm == null) {
            throw new IllegalArgumentException("Encryption algorithm must not be null.");
        }
        // Defensive copy to char[]
        CryptographyUtils.encryptKey = passphrase.toCharArray();
        CryptographyUtils.algorithm = algorithm;
    }

    // ========= Encryption / Decryption (AES-GCM) =========

    /**
     * Encrypts plaintext and returns an encoded string with metadata.
     *
     * <p>
     * Format: v1:pbkdf2:iter:saltB64:ivB64:cipherB64
     *
     * @param plaintext
     *            the UTF-8 plaintext to encrypt; must not be null
     * @return a compact encoded ciphertext string containing KDF parameters, salt, iv and ciphertext
     * @throws IllegalStateException
     *             if the encryption passphrase has not been set
     * @throws RuntimeException
     *             for internal encryption failures
     */
    public static String encrypt(String plaintext) {
        CryptoAlgorithm alg = algorithm;
        if (alg == CryptoAlgorithm.AES_GCM) {
            return CryptoAesGcm.encrypt(requireKey(), plaintext);
        } else {
            throw new IllegalStateException("Unsupported or not configured encryption algorithm: " + alg);
        }
    }

    /**
     * Decrypts a string previously produced by {@link #encrypt(String)}.
     *
     * @param encoded
     *            the encoded ciphertext string produced by {@link #encrypt(String)}; must not be null
     * @return the decrypted plaintext as a UTF-8 string
     * @throws IllegalStateException
     *             if the encryption passphrase or algorithm has not been set
     * @throws CryptoException
     *             if the input format is unsupported or malformed, or decryption fails (possible tampering or wrong key)
     */
    public static String decrypt(String encoded) {
        CryptoAlgorithm alg = algorithm;
        if (alg == CryptoAlgorithm.AES_GCM) {
            return CryptoAesGcm.decrypt(requireKey(), encoded);
        } else {
            throw new IllegalStateException("Unsupported or not configured encryption algorithm: " + alg);
        }
    }

    private static char[] requireKey() {
        char[] key = encryptKey;
        if (key == null) {
            throw new IllegalStateException("Encrypt key has not been configured, call CryptographyUtils.setEncryptKey first.");
        }
        return key;
    }

    public enum CryptoAlgorithm {
        AES_GCM
    }
}
