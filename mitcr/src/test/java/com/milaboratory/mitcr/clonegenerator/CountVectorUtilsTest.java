package com.milaboratory.mitcr.clonegenerator;

import org.apache.commons.math.random.RandomGenerator;
import org.apache.commons.math.random.Well19937a;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CountVectorUtilsTest {
    @Test
    public void testRandomAccumulator() throws Exception {
        RandomGenerator rg = new Well19937a();

        int generationResolution = 100;

        SummaryStatistics ss = new SummaryStatistics();

        for (int z = 0; z < 100; ++z) {
            int[] realCounts = new int[100];

            int[] frequencies = new int[realCounts.length];
            for (int i = 0; i < frequencies.length; ++i)
                frequencies[i] = rg.nextInt(generationResolution);

            byte[] vector = new byte[realCounts.length + 1];

            for (int i = 0; i < 1000; ++i) {
                for (int k = 0; k < realCounts.length; ++k) {
                    if (rg.nextInt(generationResolution) < frequencies[k]) {
                        realCounts[k]++;
                        CountVectorUtils.addOneRandom(vector, k, rg);
                    }
                }
            }

            int[] calculated = CountVectorUtils.getResult(vector);

            //int delta = 0;
            //int sum = 0;
            for (int i = 0; i < realCounts.length; ++i)
                if (realCounts[i] != 0)
                    ss.addValue(1.0 * (calculated[i] - realCounts[i]) / realCounts[i]);
        }

        //System.out.println(ss.getMean());
        //System.out.println(ss.getStandardDeviation());

        assertEquals(0.0, ss.getMean(), 0.01);
        assertEquals(0.10, ss.getStandardDeviation(), 0.04);
    }

    @Test
    public void testDeterministicAccumulator() throws Exception {
        RandomGenerator rg = new Well19937a();

        int generationResolution = 100;

        SummaryStatistics ss = new SummaryStatistics();

        for (int z = 0; z < 100; ++z) {
            int[] realCounts = new int[100];

            int[] frequencies = new int[realCounts.length];
            for (int i = 0; i < frequencies.length; ++i)
                frequencies[i] = rg.nextInt(generationResolution);

            byte[] vector = new byte[realCounts.length + 5];

            for (int i = 0; i < 1000; ++i) {
                for (int k = 0; k < realCounts.length; ++k) {
                    if (rg.nextInt(generationResolution) < frequencies[k]) {
                        realCounts[k]++;
                        CountVectorUtils.addOneDeterministic(vector, k);
                    }
                }
            }

            int[] calculated = CountVectorUtils.getResultDeterministic(vector);

            //int delta = 0;
            //int sum = 0;
            for (int i = 0; i < realCounts.length; ++i)
                if (realCounts[i] != 0)
                    ss.addValue(1.0 * (calculated[i] - realCounts[i]) / realCounts[i]);
        }

        //System.out.println(ss.getMean());
        //System.out.println(ss.getStandardDeviation());
        assertEquals(0.0, ss.getMean(), 0.01);
        assertEquals(0.10, ss.getStandardDeviation(), 0.04);
    }


    @Test
    public void testEventGenerator() throws Exception {
        byte[] vector = CountVectorUtils.initAccumulatorVectorForDeterministic(1);
        for (int i = 0; i < 100000; ++i)
            if (CountVectorUtils.accumulatorEvent((byte) 10, vector)) {
                assertEquals(1023, i);
                return;
            }

        assertTrue(false);
    }
}
