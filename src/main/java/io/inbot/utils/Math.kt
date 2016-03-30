package io.inbot.utils

import java.math.BigInteger
import org.apache.commons.lang3.Validate

object Math {

    /**
     * Java Math.pow has an issue with Long and Integer MIN_VALUE where it returns MIN_VALUE.

     * @param number a number
     * *
     * @return the absolute value
     */
    fun safeAbs(number: Long): Long {
        // workaround for common issue where Math.abs returns a negative number for Long.MIN_VALUE
        if (java.lang.Long.MIN_VALUE == number) {
            return java.lang.Long.MAX_VALUE
        } else {
            return java.lang.Math.abs(number)
        }
    }

    /**
     * Java Math.pow has an issue with Long and Integer MIN_VALUE where it returns MIN_VALUE.

     * @param number a number
     * *
     * @return the absolute value
     */
    fun safeAbs(number: Int): Int {
        // workaround for common issue where Math.abs returns a negative number for Long.MIN_VALUE
        if (Integer.MIN_VALUE == number) {
            return Integer.MAX_VALUE
        } else {
            return java.lang.Math.abs(number)
        }
    }

    /**
     * @param l a long
     * *
     * @param exp exponent
     * *
     * @return l to the power of exp
     */
    fun pow(l: Long, exp: Int): Long {
        val bi = BigInteger.valueOf(l).pow(exp)
        if (bi.compareTo(BigInteger.valueOf(java.lang.Long.MAX_VALUE)) > 0) {
            throw IllegalArgumentException("pow($l,$exp) exceeds Long.MAX_VALUE")
        }
        return bi.toLong()
    }

    /**
     * Useful for normalizing integer or double values to a number between 0 and 1.
     * This uses a simple logistic function: https://en.wikipedia.org/wiki/Logistic_function
     * with some small adaptations:
     *
     * (1 / (1 + java.lang.Math.exp(-1 * (factor * i))) - 0.5) * 2
     *

     * @param i
     * *            any positive long
     * *
     * @param factor
     * *            allows you to control how quickly things converge on 1. For values that range between 0 and the low
     * *            hundreds, something like 0.05 is a good starting point.
     * *
     * @return a double between 0 and 1.
     */
    fun normalize(i: Double, factor: Double): Double {
        Validate.isTrue(i >= 0, "should be positive value")
        return (1 / (1 + java.lang.Math.exp(-1 * (factor * i))) - 0.5) * 2
    }
}
