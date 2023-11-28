package cl.kanopus.common.util;

import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Pablo Diaz Saavedra
 * @email pabloandres.diazsaavedra@gmail.com
 *
 * Utility class that allows information to be encrypted and decrypted based on
 * the use of seeds.
 */
public class CryptographyUtils {

    private static String encryptKey;

    private CryptographyUtils() {
    }

    public static void setEncryptKey(String encryptKey) {
        CryptographyUtils.encryptKey = encryptKey;
    }

    public static String encrypt(String text) {
        checkKey();
        try {
            Key aesKey = new SecretKeySpec(encryptKey.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);

            byte[] encrypted = cipher.doFinal(text.getBytes());

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            throw new RuntimeException("Error trying to encrypt the text:" + text, ex);
        }
    }

    public static String decrypt(String encrypted) {
        checkKey();
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encrypted.replace("\n", ""));

            Key aesKey = new SecretKeySpec(encryptKey.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);

            String decrypted = new String(cipher.doFinal(encryptedBytes));

            return decrypted;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String generateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static void checkKey() {
        if (CryptographyUtils.encryptKey == null) {
            throw new RuntimeException("You must specify an encryption key cl.kanopus.common.util.CryptographyUtils.ENCRYPT_KEY");
        }
    }
}
