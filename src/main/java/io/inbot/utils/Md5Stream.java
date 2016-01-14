package io.inbot.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;

public class Md5Stream extends OutputStream {
    private MessageDigest md;
    public Md5Stream() {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e1) {
            throw new IllegalStateException("md5 not supported", e1);
        }
    }

    @Override
    public void write(int b) throws IOException {
        md.update((byte)b);
    }

    public String md5Hash() {
        return Base64.encodeBase64URLSafeString(md.digest());
    }

    public String etag() {
        return '"'+md5Hash()+'"';
    }
}