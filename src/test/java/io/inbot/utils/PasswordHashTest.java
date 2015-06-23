package io.inbot.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.testng.annotations.Test;


public class PasswordHashTest {

    @Test
    public void shouldNotGenerateSamePasswordHash() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "secret";
        String hash = PasswordHash.createHash(password);
        String secondHash = PasswordHash.createHash(password);
        assertThat(secondHash).isNotEqualTo(hash);
    }
}
