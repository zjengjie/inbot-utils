package io.inbot.utils

import org.assertj.core.api.Assertions.assertThat

import io.inbot.utils.AESUtils
import org.assertj.core.api.StrictAssertions

import java.security.SecureRandom
import org.testng.annotations.Test


@Test
class AESUtilsTest {

    @Throws(Exception::class)
    fun shouldConvertToFromHex() {
        val iv = ByteArray(20)
        SecureRandom().nextBytes(iv)
        val hexString = AESUtils.byteArrayToHexString(iv)
        val bytes = AESUtils.hexStringToByteArray(hexString)
        StrictAssertions.assertThat(bytes).isEqualTo(iv)
    }

    fun shouldEncryptAndDecryptUsingSaltAndPassword() {
        val plainText = "secretsecret"
        val encrypted = AESUtils.encrypt("salt", "password", plainText)
        StrictAssertions.assertThat(AESUtils.decrypt("salt", "password", encrypted)).isEqualTo(plainText)
    }

    fun shouldEncryptAndDecryptUsing256BitKey() {
        val key = ByteArray(32)
        SecureRandom().nextBytes(key)
        val plainText = "secretsecret"
        val encrypted = AESUtils.encrypt(key, plainText)
        StrictAssertions.assertThat(AESUtils.decrypt(key, encrypted)).isEqualTo(plainText)
    }

    fun shouldEncryptAndDecryptUsingGenerated256BitBase64Key() {
        val plainText = "secretsecret"
        val key = AESUtils.generateAesKey()
        val encrypted = AESUtils.encrypt(key, plainText)
        StrictAssertions.assertThat(AESUtils.decrypt(key, encrypted)).isEqualTo(plainText)
    }
}
