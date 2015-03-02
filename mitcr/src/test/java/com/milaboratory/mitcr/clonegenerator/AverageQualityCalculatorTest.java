package com.milaboratory.mitcr.clonegenerator;

import org.apache.commons.math.random.*;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.junit.Assert;
import org.junit.Test;

public class AverageQualityCalculatorTest {
    @Test
    public void test1() throws Exception {
        RandomGenerator rg = new MersenneTwister(234562);

        SummaryStatistics ss = new SummaryStatistics();
        byte accumulator = 0;

        long before = System.nanoTime();
        for (int i = 0; i < 100000; ++i) {
            for (int j = 0; j < 40; ++j)
                accumulator = AverageQualityCalculator.nextValue(accumulator, j, (byte) j, rg);
            ss.addValue(AverageQualityCalculator.getValue(accumulator, rg));
        }
        System.out.println("One calculation = " + ((System.nanoTime() - before) / 4000000) + " ns");

        Assert.assertEquals(19.5, ss.getMean(), 0.1);
        Assert.assertEquals(0.5, ss.getStandardDeviation(), 0.05);
    }

    @Test
    public void test2() throws Exception {
        RandomGenerator rg = new Well19937a(223441);
        RandomData rdg = new RandomDataImpl(new Well44497a(454213));

        final int sampleSize = 2000;

        SummaryStatistics ss = new SummaryStatistics();
        byte accumulator = 0;
        int v;
        double accActual;

        for (int i = 0; i < 1000; ++i) {
            accActual = 0.0;

            for (int j = 0; j < sampleSize; ++j) {
                accumulator = AverageQualityCalculator.nextValue(accumulator, j, (byte) (v = rdg.nextInt(0, 40)), rg);
                accActual += v;
            }

            ss.addValue(AverageQualityCalculator.getValue(accumulator, rg) - accActual / sampleSize);
        }

        Assert.assertEquals(0, ss.getMean(), 0.1);
        Assert.assertEquals(1.154, ss.getStandardDeviation(), 0.05);
    }
}
