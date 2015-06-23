package io.inbot.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;

public class Md5Appender {
    MessageDigest md;

    private Md5Appender() {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("oh boy, no md5?!?", e);
        }
    }

    public static Md5Appender appender() {
        return new Md5Appender();
    }

    public void append(Object o) {
        if(o!=null) {
            md.update(o.toString().getBytes(StandardCharsets.UTF_8));
        } else {
            md.update((byte)0);
        }
    }

    @Override
    public String toString() {
        return Base64.encodeBase64URLSafeString(md.digest());
    }
}