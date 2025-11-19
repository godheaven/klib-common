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
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

class CryptoAesGcm {

    // ===== Cryptographic parameters =====
    private static final SecureRandom RNG = new SecureRandom();
    private static final String KDF_ALG = "PBKDF2WithHmacSHA256";
    private static final int KDF_ITERATIONS = 210_000;         // adjust according to SLO
    private static final int KDF_KEY_LEN_BITS = 256;

    private static final String AES_TRANSFORM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LEN_BITS = 128;            // 16 bytes
    private static final int SALT_LEN_BYTES = 16;               // per-message salt
    private static final int IV_LEN_BYTES = 12;                 // recommended for GCM

    private CryptoAesGcm() {
    }

    // ========= Encryption / Decryption (AES-GCM) =========

    /**
     * Encrypts plaintext and returns an encoded string with metadata.
     *
     * <p>Format: v1:pbkdf2:iter:saltB64:ivB64:cipherB64
     *
     * @param plaintext the UTF-8 plaintext to encrypt; must not be null
     * @return a compact encoded ciphertext string containing KDF parameters, salt, iv and ciphertext
     * @throws IllegalStateException if the encryption passphrase has not been set
     * @throws RuntimeException      for internal encryption failures
     */
    public static String encrypt(char[] encryptKey, String plaintext) {
        Objects.requireNonNull(plaintext, "plaintext");
        Objects.requireNonNull(encryptKey, "encrypt key is required in CryptoAesGcm.encryptKey");

        byte[] salt = new byte[SALT_LEN_BYTES];
        byte[] iv = new byte[IV_LEN_BYTES];
        RNG.nextBytes(salt);
        RNG.nextBytes(iv);

        SecretKey sk = deriveAesKey(encryptKey, salt, KDF_ITERATIONS);
        byte[] cipher = gcmEncrypt(sk, iv, plaintext.getBytes(StandardCharsets.UTF_8));

        String saltB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(salt);
        String ivB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(iv);
        String cB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(cipher);

        return "v1:pbkdf2:" + KDF_ITERATIONS + ":" + saltB64 + ":" + ivB64 + ":" + cB64;
    }


    /**
     * Decrypts a string previously produced by {@link #encrypt(char[], String)}.
     *
     * @param encoded the encoded ciphertext string produced by {@link #encrypt(char[], String)}; must not be null
     * @return the decrypted plaintext as a UTF-8 string
     * @throws IllegalArgumentException if the input format is unsupported or malformed
     * @throws IllegalStateException    if the encryption passphrase has not been set
     * @throws RuntimeException         if decryption fails (possible tampering or wrong key)
     */
    public static String decrypt(char[] encryptKey, String encoded) {
        Objects.requireNonNull(encoded, "encoded");
        Objects.requireNonNull(encryptKey, "encrypt key is required in CryptoAesGcm.decrypt");

        String[] parts = encoded.split(":");
        if (parts.length != 6 || !parts[0].equals("v1") || !parts[1].equals("pbkdf2")) {
            throw new IllegalArgumentException("Unsupported ciphertext format");
        }

        int iterations = Integer.parseInt(parts[2]);
        byte[] salt = Base64.getUrlDecoder().decode(parts[3]);
        byte[] iv = Base64.getUrlDecoder().decode(parts[4]);
        byte[] cipherBytes = Base64.getUrlDecoder().decode(parts[5]);

        SecretKey sk = deriveAesKey(encryptKey, salt, iterations);
        byte[] plain = gcmDecrypt(sk, iv, cipherBytes);
        return new String(plain, StandardCharsets.UTF_8);
    }


    private static SecretKey deriveAesKey(char[] pass, byte[] salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(pass, salt, iterations, KDF_KEY_LEN_BITS);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(KDF_ALG);
            byte[] key = skf.generateSecret(spec).getEncoded();
            return new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            throw new CryptoException("KDF failed", e);
        }
    }

    private static byte[] gcmEncrypt(SecretKey key, byte[] iv, byte[] plaintext) {
        try {
            Cipher cipher = Cipher.getInstance(AES_TRANSFORM);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LEN_BITS, iv));
            return cipher.doFinal(plaintext);
        } catch (Exception e) {
            throw new CryptoException("AES-GCM encrypt failed", e);
        }
    }


    private static byte[] gcmDecrypt(SecretKey key, byte[] iv, byte[] ciphertext) {
        try {
            Cipher cipher = Cipher.getInstance(AES_TRANSFORM);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LEN_BITS, iv));
            return cipher.doFinal(ciphertext);
        } catch (Exception e) {
            throw new CryptoException("AES-GCM decrypt failed (tampered or wrong key)", e);
        }
    }

    // ========= Hash/Verify (for passwords/tokens), NOT reversible =========

    /**
     * PBKDF2 hash (one-way). Format: v1:pbkdf2:iter:saltB64:dkB64
     *
     * @param input the input string to hash (e.g. a password); must not be null
     * @return a string containing KDF parameters, salt and derived key in Base64
     * @throws RuntimeException if hashing fails
     */
    public static String hash(String input) {
        Objects.requireNonNull(input, "input");
        byte[] salt = new byte[SALT_LEN_BYTES];
        RNG.nextBytes(salt);

        try {
            PBEKeySpec spec = new PBEKeySpec(input.toCharArray(), salt, KDF_ITERATIONS, 256);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(KDF_ALG);
            byte[] dk = skf.generateSecret(spec).getEncoded();

            String saltB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(salt);
            String dkB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(dk);
            return "v1:pbkdf2:" + KDF_ITERATIONS + ":" + saltB64 + ":" + dkB64;
        } catch (Exception e) {
            throw new CryptoException("Hash failed", e);
        }
    }

    /**
     * Verifies a PBKDF2 hash in constant time to prevent timing attacks.
     *
     * @param raw    the raw input to verify (e.g. password)
     * @param stored the stored hash produced by {@link #hash(String)}
     * @return true if the raw input corresponds to the stored hash; false otherwise
     */
    public static boolean verifyHash(String raw, String stored) {
        Objects.requireNonNull(raw, "raw");
        Objects.requireNonNull(stored, "stored");

        String[] parts = stored.split(":");
        if (parts.length != 5 || !parts[0].equals("v1") || !parts[1].equals("pbkdf2")) return false;

        int iterations = Integer.parseInt(parts[2]);
        byte[] salt = Base64.getUrlDecoder().decode(parts[3]);
        byte[] dkStored = Base64.getUrlDecoder().decode(parts[4]);

        try {
            PBEKeySpec spec = new PBEKeySpec(raw.toCharArray(), salt, iterations, dkStored.length * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(KDF_ALG);
            byte[] dk = skf.generateSecret(spec).getEncoded();
            return constantTimeEquals(dk, dkStored);
        } catch (Exception e) {
            return false;
        }
    }

    // ========= Compatibility: old matches() =========

    /**
     * NOTE: Previously this library compared by encrypting (bad practice).
     * Now we treat `encryptedOrHashed` as a PBKDF2 hash.
     *
     * @param raw               the raw input to verify
     * @param encryptedOrHashed the stored PBKDF2 hash
     * @return true if verification succeeds, false otherwise
     */
    public static boolean matches(String raw, String encryptedOrHashed) {
        return verifyHash(raw, encryptedOrHashed);
    }

    // ========= Utilities =========
    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == null || b == null) return false;
        if (a.length != b.length) return false;
        int r = 0;
        for (int i = 0; i < a.length; i++) r |= a[i] ^ b[i];
        return r == 0;
    }


}
