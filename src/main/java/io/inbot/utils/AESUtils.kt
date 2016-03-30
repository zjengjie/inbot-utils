package io.inbot.utils

import java.io.UnsupportedEncodingException
import java.lang.reflect.Field
import java.nio.charset.StandardCharsets
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Base64

/**
 * Helper methods to assist with encryption/decryption using AES.

 * The plaintext is encrypted with a secure random salt so that every encrypted value is unique.

 * You can choose between using your own 256 bit key or using a salt + password from which a 256 bit key is constructed.

 * The encrypted value includes a md5 content hash that is used by the decrypt to verify the value is correct. This
 * guarantees the decrypt fails with an UnauthorizedException if the key/password is incorrect and prevents the
 * algorithm from returning random garbage instead.
 */
object AESUtils {

    private val SECURE_RANDOM = SecureRandom()

    init {
        // Change security policy to unlimited strength programmatically
        try {
            val securityClass = Class.forName("javax.crypto.JceSecurity")
            val restrictedField = securityClass.getDeclaredField("isRestricted")
            restrictedField.isAccessible = true
            // restrictedField.set nil, false
            restrictedField.setBoolean(null, false)
        } catch (e: ClassNotFoundException) {
            // if this doesn't work; we are on an incompatible JVM
            throw IllegalStateException("cannot override java crypto restrictions; use a compatible jvm")
        } catch (e: NoSuchFieldException) {
            throw IllegalStateException("cannot override java crypto restrictions; use a compatible jvm")
        } catch (e: IllegalAccessException) {
            throw IllegalStateException("cannot override java crypto restrictions; use a compatible jvm")
        }

    }

    fun generateAesKey(): String {
        val newKey = ByteArray(32)
        SECURE_RANDOM.nextBytes(newKey)
        return Base64.encodeBase64String(newKey)
    }

    /**
     * Decrypt text that was encrypted with the provided encrypt method in this class.

     * @param salt
     * *            salt is used together with the password to construct a 256 bit SecretKey
     * *
     * @param password
     * *            the password
     * *
     * @param input
     * *            the iv as a hex string followed by '$' followed by the encrypted text
     * *
     * @return plain text.
     */
    fun decrypt(salt: String, password: String, input: String): String {
        val secret = getKey(salt, password)

        return decrypt(secret, input)
    }

    /**
     * Decrypt text that was encrypted with the provided encrypt method in this class.

     * @param key
     * *            256 bit key
     * *
     * @param encrypted
     * *            encrypted text
     * *
     * @return plain text
     */
    fun decrypt(key: ByteArray, encrypted: String): String {
        val secret = SecretKeySpec(key, "AES")
        return decrypt(secret, encrypted)
    }

    /**
     * Decrypt text that was encrypted with the provided encrypt method in this class.

     * @param key
     * *            256 bit key base64 encoded
     * *
     * @param encrypted
     * *            encrypted text
     * *
     * @return plain text
     */
    fun decrypt(key: String, encrypted: String): String {
        val secret = SecretKeySpec(Base64.decodeBase64(key.toByteArray(StandardCharsets.UTF_8)), "AES")
        return decrypt(secret, encrypted)
    }

    private fun decrypt(secret: SecretKey, input: String): String {
        var input = input
        try {
            // Convert url-safe base64 to normal base64
            input = input.replace("-".toRegex(), "+").replace("_".toRegex(), "/").replace("\r".toRegex(), "").replace("\n".toRegex(), "")
            val splitedInput = input.split("\\$".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val iv = hexStringToByteArray(splitedInput[0])
            val hash = Base64.decodeBase64(splitedInput[1])

            val plaintext: String

            // Internally PKCS7 is used
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            // Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            // Needs external dependency like 'bouncy castle'
            cipher.init(Cipher.DECRYPT_MODE, secret, IvParameterSpec(iv))
            plaintext = String(cipher.doFinal(hash))
            val md5 = plaintext.substring(0, 22)
            val plainTextWithoutHash = plaintext.substring(22)
            if (md5 == HashUtils.md5(plainTextWithoutHash)) {
                return plainTextWithoutHash
            } else {
                throw IllegalArgumentException("wrong aes key - incorrect content hash")
            }
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalStateException("cannot decrypt: " + e.message, e)
        } catch (e: NoSuchPaddingException) {
            throw IllegalStateException("cannot decrypt: " + e.message, e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw IllegalStateException("cannot decrypt: " + e.message, e)
        } catch (e: IllegalBlockSizeException) {
            throw IllegalStateException("cannot decrypt: " + e.message, e)
        } catch (e: UnsupportedEncodingException) {
            throw IllegalStateException("cannot decrypt: " + e.message, e)
        } catch (e: InvalidKeyException) {
            throw IllegalArgumentException("wrong aes key")
        } catch (e: BadPaddingException) {
            throw IllegalArgumentException("wrong aes key")
        }

    }

    /**
     * Encrypt the plain text.

     * @param key
     * *            256 bit key
     * *
     * @param plainText
     * *            text that needs to be encrypted
     * *
     * @return the iv as a hex string followed by '$' followed by the encrypted text.
     */
    fun encrypt(key: ByteArray, plainText: String): String {
        val secret = SecretKeySpec(key, "AES")
        return encrypt(secret, plainText)
    }

    /**
     * Encrypt the plain text.

     * @param key
     * *            256 bit key base64 encoded
     * *
     * @param plainText
     * *            text that needs to be encrypted
     * *
     * @return the iv as a hex string followed by '$' followed by the encrypted text.
     */
    fun encrypt(key: String, plainText: String): String {
        val secret = SecretKeySpec(Base64.decodeBase64(key.toByteArray()), "AES")
        return encrypt(secret, plainText)
    }

    /**
     * Encrypt the plain text.

     * @param salt
     * *            salt is used together with the password to construct a 256 bit SecretKey
     * *
     * @param password
     * *            the secret key
     * *
     * @param plainText
     * *            unencrypted text
     * *
     * @return the iv as a hex string followed by '$' followed by the encrypted text.
     */
    fun encrypt(salt: String, password: String, plainText: String): String {
        val secret = getKey(salt, password)
        return encrypt(secret, plainText)
    }

    private fun encrypt(secret: SecretKey, plainText: String): String {
        var plainText = plainText
        try {
            val md5 = HashUtils.md5(plainText)
            plainText = md5 + plainText
            val iv = ByteArray(16)
            SECURE_RANDOM.nextBytes(iv)

            // Internally PKCS7 is used
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            // Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            // Needs external dependency like 'bouncy castle'
            cipher.init(Cipher.ENCRYPT_MODE, secret, IvParameterSpec(iv))
            val encrypted = cipher.doFinal(plainText.toByteArray())
            return byteArrayToHexString(iv) + "$" + String(Base64.encodeBase64URLSafe(encrypted))
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalStateException("cannot encrypt: " + e.message, e)
        } catch (e: NoSuchPaddingException) {
            throw IllegalStateException("cannot encrypt: " + e.message, e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw IllegalStateException("cannot encrypt: " + e.message, e)
        } catch (e: IllegalBlockSizeException) {
            throw IllegalStateException("cannot encrypt: " + e.message, e)
        } catch (e: BadPaddingException) {
            throw IllegalStateException("cannot encrypt: " + e.message, e)
        } catch (e: UnsupportedEncodingException) {
            throw IllegalStateException("cannot encrypt: " + e.message, e)
        } catch (e: InvalidKeyException) {
            throw IllegalArgumentException("wrong aes key")
        }

    }

    fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    fun byteArrayToHexString(bytes: ByteArray): String {
        val sb = StringBuilder(bytes.size * 2)
        for (b in bytes) {
            sb.append(String.format("%02X", b))
        }
        return sb.toString()
    }

    private fun getKey(salt: String, password: String): SecretKey {
        try {
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec = PBEKeySpec(password.toCharArray(), salt.toByteArray(), 1000, 256)
            val tmp = factory.generateSecret(spec)
            return SecretKeySpec(tmp.encoded, "AES")
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalStateException("cannot create key: " + e.message, e)
        } catch (e: InvalidKeySpecException) {
            throw IllegalStateException("cannot create key: " + e.message, e)
        }

    }
}
