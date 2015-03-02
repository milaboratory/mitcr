package com.milaboratory.mitcr.clonegenerator;

import org.apache.commons.math.random.MersenneTwister;
import org.apache.commons.math.random.RandomGenerator;

public class AverageQualityCalculator {
    /**
     * This method is intended to calculate average value of sequence of numbers between 0 and 63 using only 1 byte of
     * information. The only additional information needed is a number of observations.
     *
     * <p>Performance: resulting value error with 95% probability will be: (1) less than 1 point for small sample sizes
     * and (2) less than 2.2 points for big samples. One iteration takes about 100 cycles on the Core i7 processor.</p>
     *
     * <p>If {@code observationCount == 0} the value of {@code accumulatorValue} is ignored.</p>
     *
     * <p>Use {@link #getValue(byte, org.apache.commons.math.random.RandomGenerator)} to extract value from
     * accumulator.</p>
     *
     * @param accumulatorValue previous value of 1-byte accumulator (storage)
     * @param observationCount number of observations before current event
     * @param observedValue    observed value
     * @param generator        random generator
     */
    public static byte nextValue(byte accumulatorValue, long observationCount, byte observedValue, RandomGenerator generator) {
        if (observationCount == 0)
            return (byte) (observedValue << 2);

        final int observedValueInt = observedValue << 2;
        final int accumulatorValueInt = (0xFF & accumulatorValue);

        if (observedValueInt == accumulatorValueInt)
            return accumulatorValue;

        double nextValueDelta = (observedValueInt - accumulatorValueInt) * 1.0 / (observationCount + 1);

        if (nextValueDelta < 0) {
            nextValueDelta = -nextValueDelta;
            final double fpart;
            accumulatorValue -= (byte) (fpart = Math.floor(nextValueDelta));
            nextValueDelta -= fpart;
            if (generator.nextDouble() < nextValueDelta)
                --accumulatorValue;
        } else {
            final double fpart;
            accumulatorValue += (byte) (fpart = Math.floor(nextValueDelta));
            nextValueDelta -= fpart;
            if (generator.nextDouble() < nextValueDelta)
                ++accumulatorValue;
        }

        return accumulatorValue;
    }

    /**
     * Extracts value from 1-byte accumulator.
     *
     * @param accumulatorValue resulting value of accumulator
     * @param generator        random generator
     */
    public static byte getValue(byte accumulatorValue, RandomGenerator generator) {
        final int accumulatorValueInt = (0xFF & accumulatorValue);
        if ((accumulatorValueInt & 0x3) < 2)
            return (byte) (accumulatorValueInt >> 2);
        else if ((accumulatorValueInt & 0x3) == 2 && generator.nextBoolean())
            return (byte) (accumulatorValueInt >> 2);
        return (byte) ((accumulatorValueInt >> 2) + 1);
    }

    private static ThreadLocal<RandomGenerator> tlRandomGenerator = new ThreadLocal<RandomGenerator>() {
        @Override
        protected RandomGenerator initialValue() {
            return new MersenneTwister(75586L); //Fixed value for calculation reproducibility
        }
    };

    /**
     * Array version of {@link #nextValue(byte, long, byte, org.apache.commons.math.random.RandomGenerator)} using
     * thread local random generator.
     *
     * @param accumulatorValues previous values of accumulators
     * @param observationNumber number of observations before current event
     * @param observedValues    array with observed values
     */
    public static void nextValues(byte[] accumulatorValues, long observationNumber,
                                  byte[] observedValues) {
        nextValues(accumulatorValues, observationNumber, observedValues, tlRandomGenerator.get());
    }

    /**
     * Array version of {@link #getValue(byte, org.apache.commons.math.random.RandomGenerator)} using thread local
     * random generator.
     *
     * @param accumulatorValues resulting values of accumulators
     */
    public static byte[] getValues(byte[] accumulatorValues) {
        return getValues(accumulatorValues, tlRandomGenerator.get());
    }

    /**
     * Array version of {@link #getValue(byte, org.apache.commons.math.random.RandomGenerator)}, rewrites values in
     * array.
     *
     * @param accumulatorValues resulting values of accumulators
     */
    public static void convertValues(byte[] accumulatorValues) {
        for (int i = accumulatorValues.length - 1; i >= 0; --i)
            accumulatorValues[i] = getValue(accumulatorValues[i], tlRandomGenerator.get());
    }

    /**
     * Array version of {@link #nextValue(byte, long, byte, org.apache.commons.math.random.RandomGenerator)}.
     *
     * @param accumulatorValues previous values of accumulators
     * @param observationNumber number of observations before current event
     * @param observedValues    array with observed values
     * @param generator         random generator
     */
    public static void nextValues(byte[] accumulatorValues, long observationNumber,
                                  byte[] observedValues, RandomGenerator generator) {
        for (int i = accumulatorValues.length - 1; i >= 0; --i)
            accumulatorValues[i] = nextValue(accumulatorValues[i], observationNumber, observedValues[i], generator);
    }

    /**
     * Array version of {@link #getValue(byte, org.apache.commons.math.random.RandomGenerator)}.
     *
     * @param accumulatorValues resulting values of accumulators
     */
    public static byte[] getValues(byte[] accumulatorValues, RandomGenerator generator) {
        byte[] result = new byte[accumulatorValues.length];
        for (int i = accumulatorValues.length - 1; i >= 0; --i)
            result[i] = getValue(accumulatorValues[i], generator);
        return result;
    }

    /**
     * Array version of {@link #getValue(byte, org.apache.commons.math.random.RandomGenerator)}, rewrites values in
     * array.
     *
     * @param accumulatorValues resulting values of accumulators
     */
    public static void convertValues(byte[] accumulatorValues, RandomGenerator generator) {
        for (int i = accumulatorValues.length - 1; i >= 0; --i)
            accumulatorValues[i] = getValue(accumulatorValues[i], generator);
    }
}
