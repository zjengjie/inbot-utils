package io.inbot.utils;

import java.math.BigInteger;
import org.apache.commons.lang3.Validate;

public class Math {

    /**
     * Java Math.abs has an issue with Long and Integer MIN_VALUE where it returns MIN_VALUE.
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
     * Java Math.abs has an issue with Long and Integer MIN_VALUE where it returns MIN_VALUE.
     *
     * @param number a number
     * @return the absolute value
     */
    public static int safeAbs(int number) {
        // workaround for common issue where Math.abs returns a negative number for Long.MIN_VALUE
        if(Integer.MIN_VALUE == number) {
            return Integer.MAX_VALUE;
        } else {
            return java.lang.Math.abs(number);
        }
    }

    /**
     * Java.lang.pow only works on doubles; this fixes that.
     * Safe version of pow that relies on BigInteger that can raise a long to an int exponential. Uses BigInteger
     * internally to ensure this is done
     * correctly and does a check on the value to ensure it can safely be converted into a long
     *
     * @param l
     *            a long
     * @param exp
     *            exponent
     * @return l to the power of exp
     * @throws IllegalArgumentException
     *             if the resulting value exceeds Long.MAX_VALUE
     */
    public static long pow(long l, int exp) {
        BigInteger bi = BigInteger.valueOf(l).pow(exp);
        if(bi.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0 || bi.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0) {
            throw new IllegalArgumentException("pow(" + l + "," + exp + ") exceeds Long.MAX_VALUE");
        }
        return bi.longValue();
    }

    /**
     * Useful for normalizing integer or double values to a number between 0 and 1.
     * This uses a simple logistic function: https://en.wikipedia.org/wiki/Logistic_function
     * with some small adaptations:
     * <pre>
     * (1 / (1 + java.lang.Math.exp(-1 * (factor * i))) - 0.5) * 2
     * </pre>
     *
     * @param i
     *            any positive long
     * @param factor
     *            allows you to control how quickly things converge on 1. For values that range between 0 and the low
     *            hundreds, something like 0.05 is a good starting point.
     * @return a double between 0 and 1.
     */
    public static double normalize(double i, double factor) {
        Validate.isTrue(i >= 0, "should be positive value");
        return (1 / (1 + java.lang.Math.exp(-1 * (factor * i))) - 0.5) * 2;
    }

    public static double round(double number, int decimals) {
        Validate.isTrue(number != 0, "should not be 0");
        Validate.isTrue(decimals != 0, "should not be 0");
        int temp = (int) (number * Math.pow(10, decimals));
        return (double) temp / Math.pow(10, decimals);
    }
}
