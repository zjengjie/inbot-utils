package io.inbot.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 * Helper methods to assist with encryption/decryption using AES.
 *
 * The plaintext is encrypted with a secure random salt so that every encrypted value is unique.
 *
 * You can choose between using your own 256 bit key or using a salt + password from which a 256 bit key is constructed.
 *
 * The encrypted value includes a md5 content hash that is used by the decrypt to verify the value is correct. This
 * guarantees the decrypt fails with an UnauthorizedException if the key/password is incorrect and prevents the
 * algorithm from returning random garbage instead.
 */
public class AESUtils {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    static {
        // Change security policy to unlimited strength programmatically
        try {
            Class<?> securityClass = Class.forName("javax.crypto.JceSecurity");
            Field restrictedField = securityClass.getDeclaredField("isRestricted");
            restrictedField.setAccessible(true);
            // restrictedField.set nil, false
            restrictedField.setBoolean(null, false);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            // if this doesn't work; we are on an incompatible JVM
            throw new IllegalStateException("cannot override java crypto restrictions; use a compatible jvm");
        }
    }

    public static String generateAesKey() {
        byte[] newKey = new byte[32];
        SECURE_RANDOM.nextBytes(newKey);
        return Base64.encodeBase64String(newKey);
    }

    /**
     * Decrypt text that was encrypted with the provided encrypt method in this class.
     *
     * @param salt
     *            salt is used together with the password to construct a 256 bit SecretKey
     * @param password
     *            the password
     * @param input
     *            the iv as a hex string followed by '$' followed by the encrypted text
     * @return plain text.
     */
    public static String decrypt(String salt, String password, String input) {
        SecretKey secret = getKey(salt, password);

        return decrypt(secret, input);
    }

    /**
     * Decrypt text that was encrypted with the provided encrypt method in this class.
     *
     * @param key
     *            256 bit key
     * @param encrypted
     *            encrypted text
     * @return plain text
     */
    public static String decrypt(byte[] key, String encrypted) {
        SecretKey secret = new SecretKeySpec(key, "AES");
        return decrypt(secret, encrypted);
    }

    /**
     * Decrypt text that was encrypted with the provided encrypt method in this class.
     *
     * @param key
     *            256 bit key base64 encoded
     * @param encrypted
     *            encrypted text
     * @return plain text
     */
    public static String decrypt(String key, String encrypted) {
        SecretKey secret = new SecretKeySpec(Base64.decodeBase64(key.getBytes(StandardCharsets.UTF_8)), "AES");
        return decrypt(secret, encrypted);
    }

    private static String decrypt(SecretKey secret, String input) {
        try {
            // Convert url-safe base64 to normal base64
            input = input.replaceAll("-", "+").replaceAll("_", "/").replaceAll("\r", "").replaceAll("\n", "");
            String[] splitedInput = input.split("\\$");
            byte[] iv = hexStringToByteArray(splitedInput[0]);
            byte[] hash = Base64.decodeBase64(splitedInput[1]);

            String plaintext;

            // Internally PKCS7 is used
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            // Needs external dependency like 'bouncy castle'
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
            plaintext = new String(cipher.doFinal(hash), "UTF-8");
            String md5 = plaintext.substring(0, 22);
            String plainTextWithoutHash = plaintext.substring(22);
            if (md5.equals(HashUtils.md5(plainTextWithoutHash))) {
                return plainTextWithoutHash;
            } else {
                throw new IllegalArgumentException("wrong aes key - incorrect content hash");
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException
                | UnsupportedEncodingException e) {
            throw new IllegalStateException("cannot decrypt: " + e.getMessage(), e);
        } catch (InvalidKeyException | BadPaddingException e) {
            throw new IllegalArgumentException("wrong aes key");
        }
    }

    /**
     * Encrypt the plain text.
     *
     * @param key
     *            256 bit key
     * @param plainText
     *            text that needs to be encrypted
     * @return the iv as a hex string followed by '$' followed by the encrypted text.
     */
    public static String encrypt(byte[] key, String plainText) {
        SecretKey secret = new SecretKeySpec(key, "AES");
        return encrypt(secret, plainText);
    }

    /**
     * Encrypt the plain text.
     *
     * @param key
     *            256 bit key base64 encoded
     * @param plainText
     *            text that needs to be encrypted
     * @return the iv as a hex string followed by '$' followed by the encrypted text.
     */
    public static String encrypt(String key, String plainText) {
        SecretKey secret = new SecretKeySpec(Base64.decodeBase64(key.getBytes()), "AES");
        return encrypt(secret, plainText);
    }

    /**
     * Encrypt the plain text.
     *
     * @param salt
     *            salt is used together with the password to construct a 256 bit SecretKey
     * @param password
     *            the secret key
     * @param plainText
     *            unencrypted text
     * @return the iv as a hex string followed by '$' followed by the encrypted text.
     */
    public static String encrypt(String salt, String password, String plainText) {
        SecretKey secret = getKey(salt, password);
        return encrypt(secret, plainText);
    }

    private static String encrypt(SecretKey secret, String plainText) {
        try {
            String md5 = HashUtils.md5(plainText);
            plainText = md5 + plainText;
            byte[] iv = new byte[16];
            SECURE_RANDOM.nextBytes(iv);

            // Internally PKCS7 is used
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            // Needs external dependency like 'bouncy castle'
            cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return byteArrayToHexString(iv) + "$" + new String(Base64.encodeBase64URLSafe(encrypted), "UTF-8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
                | UnsupportedEncodingException e) {
            throw new IllegalStateException("cannot encrypt: " + e.getMessage(), e);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("wrong aes key");
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for(int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private static SecretKey getKey(String salt, String password) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 1000, 256);
            SecretKey tmp = factory.generateSecret(spec);
            return new SecretKeySpec(tmp.getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("cannot create key: " + e.getMessage(), e);
        }
    }
}
