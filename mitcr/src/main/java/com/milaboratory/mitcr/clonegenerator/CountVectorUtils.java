package com.milaboratory.mitcr.clonegenerator;

import com.milaboratory.util.BitArray;
import org.apache.commons.math.random.RandomGenerator;

/**
 * Utils for count vector used in gene segments counting.
 */
public class CountVectorUtils {
    public static final int RANDOM_ACCUMULATOR_HEADER_SIZE = 1;
    public static final int DETERMINISTIC_ACCUMULATOR_HEADER_SIZE = 5;

    static void addOneRandomNaive(byte[] vector, int point, RandomGenerator randomGenerator) {
        final byte exp = vector[0];
        if (exp == 0 || (((0xFFFFFFFF >>> (32 - exp)) & randomGenerator.nextInt()) == 0)) //random event with probability = 2^-exp
            if ((++vector[point + 1]) == -1) //pre-overflow condition in one cell
            {
                for (int i = 1; i < vector.length; ++i)
                    // random tie-breaking rule for rounding of odd numbers
                    if ((vector[i] & 1) == 1 && randomGenerator.nextBoolean())
                        // if odd and random event returned true
                        vector[i] = (byte) (((0xFF & vector[i]) >> 1) + 1);
                    else
                        // if number is even or odd but random event returns false
                        vector[i] = (byte) ((0xFF & vector[i]) >> 1);

                ++vector[0]; //increasing the exponent
            }
    }

    public static void addOneRandom(byte[] vector, int point, RandomGenerator randomGenerator) {
        final byte exp = vector[0];
        if (exp == 0 || (((0xFFFFFFFF >>> (32 - exp)) & randomGenerator.nextInt()) == 0)) //random event with probability = 2^-exp
            if ((++vector[point + 1]) == -1) //pre-overflow condition in one cell
            {
                for (int i = 1; i < vector.length; ++i)
                    // random tie-breaking rule for rounding of odd numbers
                    if ((vector[i] & 1) == 1 && ((exp + i) & 1) == 1)
                        // if odd and random event returned true
                        vector[i] = (byte) (((0xFF & vector[i]) >> 1) + 1);
                    else
                        // if number is even or odd but random event returns false
                        vector[i] = (byte) ((0xFF & vector[i]) >> 1);

                ++vector[0]; //increasing the exponent
            }
    }

    public static void addOneDeterministic(byte[] vector, BitArray array) {
        for (int i = array.size() - 1; i >= 0; --i)
            if (array.get(i))
                addOneDeterministic(vector, i);
    }

    public static void addOneDeterministic(byte[] vector, int point) {
        final byte exp = vector[0];
        if (exp == 0 || accumulatorEvent(exp, vector)) //random event with probability = 2^-exp
            if ((++vector[point + 5]) == -1) //pre-overflow condition in one cell
            {
                for (int i = 5; i < vector.length; ++i)
                    // random tie-breaking rule for rounding of odd numbers
                    if ((vector[i] & 1) == 1 && ((exp + i) & 1) == 1)
                        // if odd and random event returned true
                        vector[i] = (byte) (((0xFF & vector[i]) >> 1) + 1);
                    else
                        // if number is even or odd but random event returns false
                        vector[i] = (byte) ((0xFF & vector[i]) >> 1);

                ++vector[0]; //increasing the exponent
            }
    }

    public static byte[] initAccumulatorVectorForDeterministic(int length) {
        //final byte[] result = new byte[length + 5];
        //result[1] = 1;
        return new byte[length + 5];
    }

    /**
     * Returns true for each 2^exp invocation for a given vector. Used as a substitution of random event with
     * probability 1/2^exp .
     */
    static boolean accumulatorEvent(final byte exp, byte[] vector) {
        if (++vector[1] == 0)
            if (++vector[2] == 0)
                if (++vector[3] == 0)
                    ++vector[4];
        if ((vector[1 + (exp >>> 3)] & (1 << (exp & 7))) != 0) {
            vector[1] = 0;
            vector[2] = 0;
            vector[3] = 0;
            vector[4] = 0;
            return true;
        }
        return false;
    }

    public static int[] getResult(byte[] vector) {
        final byte exp = vector[0];
        int[] result = new int[vector.length - 1];
        for (int i = 0; i < result.length; ++i)
            result[i] = ((0xFF & vector[i + 1]) << exp);
        return result;
    }

    public static int[] getResultDeterministic(byte[] vector) {
        final byte exp = vector[0];
        int[] result = new int[vector.length - 5];
        for (int i = 0; i < result.length; ++i)
            result[i] = ((0xFF & vector[i + 5]) << exp);
        return result;
    }
}
