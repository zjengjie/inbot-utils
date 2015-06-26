package io.inbot.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MiscUtils {

    public static boolean equalsAny(Object left, Object...list) {
        for(Object right: list) {
            if(left.equals(right)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Url encode a string and don't throw a checked exception. Uses UTF-8 as per RFC 3986.
     * @param s a string
     * @return url encoded string
     */
    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("get a jdk that actually supports utf-8", e);
        }
    }

    /**
     * Url decode a string and don't throw a checked exception. Uses UTF-8 as per RFC 3986.
     * @param s a string
     * @return decoded string
     */
    public static String urlDecode(String s) {
        try {
            return URLDecoder.decode(s, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("get a jdk that actually supports utf-8", e);
        }
    }
}
