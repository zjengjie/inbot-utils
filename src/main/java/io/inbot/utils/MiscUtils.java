package io.inbot.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    public static Stream<MatchResult> streamMatches(Matcher matcher) {
        // thank you SO: https://stackoverflow.com/questions/28148483/how-do-i-create-a-stream-of-regex-matches

        Spliterator<MatchResult> spliterator = new Spliterators.AbstractSpliterator<MatchResult>(Long.MAX_VALUE, Spliterator.ORDERED|Spliterator.NONNULL) {

            @Override
            public boolean tryAdvance(Consumer<? super MatchResult> action) {
                if(!matcher.find()) {
                    return false;
                } else {
                    action.accept(matcher.toMatchResult());
                    return true;
                }
            }};

        return StreamSupport.stream(spliterator, false);
    }
}
