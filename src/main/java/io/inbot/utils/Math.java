package io.inbot.utils;

import java.math.BigInteger;

public class Math {

    /**
     * Java Math.pow has an issue with Long and Integer MIN_VALUE where it returns MIN_VALUE.
     *
     * @param number a number
     * @return the absolute value
     */
    public static long safeAbs(long number) {
        // workaround for common issue where Math.abs returns a negative number for Long.MIN_VALUE
        if(Long.MIN_VALUE == number) {
            return Long.MAX_VALUE;
        } else {
            return java.lang.Math.abs(number);
        }
    }

    /**
     * @param l a long
     * @param exp exponent
     * @return l to the power of exp
     */
    public static long pow(long l, int exp) {
        BigInteger bi = BigInteger.valueOf(l).pow(exp);
        if(bi.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
            throw new IllegalArgumentException("pow(" + l + "," + exp + ") exceeds Long.MAX_VALUE");
        }
        return bi.longValue();
    }
}
