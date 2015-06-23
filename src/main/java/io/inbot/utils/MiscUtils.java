package io.inbot.utils;

public class MiscUtils {

    public static boolean equalsAny(Object left, Object...list) {
        for(Object right: list) {
            if(left.equals(right)) {
                return true;
            }
        }
        return false;
    }
}
