package io.inbot.utils;

import static org.assertj.core.api.Assertions.assertThat;

import io.inbot.utils.AESUtils;

import java.security.SecureRandom;
import org.testng.annotations.Test;


@Test
public class AESUtilsTest {

    public void shouldConvertToFromHex() throws Exception {
        byte[] iv = new byte[20];
        new SecureRandom().nextBytes(iv);
        String hexString = AESUtils.byteArrayToHexString(iv);
        byte[] bytes = AESUtils.hexStringToByteArray(hexString);
        assertThat(bytes).isEqualTo(iv); 
    }

    public void shouldEncryptAndDecryptUsingSaltAndPassword() {
        String plainText = "secretsecret";
        String encrypted = AESUtils.encrypt("salt", "password", plainText);
        assertThat(AESUtils.decrypt("salt", "password", encrypted)).isEqualTo(plainText); 
    }

    public void shouldEncryptAndDecryptUsing256BitKey() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        String plainText = "secretsecret";
        String encrypted = AESUtils.encrypt(key, plainText);
        assertThat(AESUtils.decrypt(key, encrypted)).isEqualTo(plainText); 
    }

    public void shouldEncryptAndDecryptUsingGenerated256BitBase64Key() {
        String plainText = "secretsecret";
        String key = AESUtils.generateAesKey();
        String encrypted = AESUtils.encrypt(key, plainText);
        assertThat(AESUtils.decrypt(key, encrypted)).isEqualTo(plainText); 
    }
}
