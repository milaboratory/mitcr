/*
 * MiTCR <http://milaboratory.com>
 *
 * Copyright (c) 2010-2013:
 *     Bolotin Dmitriy     <bolotin.dmitriy@gmail.com>
 *     Chudakov Dmitriy    <chudakovdm@mail.ru>
 *
 * MiTCR is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.milaboratory.util;

import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.random.Well19937c;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.commons.math.util.FastMath.log;
import static org.junit.Assert.assertTrue;

public class MathTest {
    @Test
    public void testSort() throws Exception {
        long[] array = {2L, 0L, 5L, 11L, -6L, 210L, 68L, 3L};
        int[] permutation = Math.sort(array);
        Assert.assertArrayEquals(new int[]{4, 1, 0, 7, 2, 3, 6, 5}, permutation);
    }

    @Test
    public void testBinomialLog() throws Exception {
        RandomData rdg = new RandomDataImpl(new Well19937c());
        long n, k;
        double func, exact, avrg;
        for (int i = 0; i < 100000; ++i) {
            n = rdg.nextLong(1, 100);
            k = rdg.nextLong(0, n);
            func = Math.binomialCoefficientLog(n, k);
            exact = exactBinomialCoefficientLog(n, k);
            avrg = (func + exact) / 2;
            assertTrue(func == exact || (java.lang.Math.abs(func - exact) / avrg) <= .001);
        }
    }

    @Test
    public void testBinomialLogBig() throws Exception {
        RandomData rdg = new RandomDataImpl(new Well19937c());
        long n, k;
        double func, exact, avrg;
        for (int i = 0; i < 5000; ++i) {
            n = rdg.nextLong(1, 10000);
            k = rdg.nextLong(0, n);
            func = Math.binomialCoefficientLog(n, k);
            exact = exactBinomialCoefficientLog(n, k);
            avrg = (func + exact) / 2;
            assertTrue(func == exact || (java.lang.Math.abs(func - exact) / avrg) <= .001);
        }
    }


    private static double exactBinomialCoefficientLog(long n, long k) {
        if (k > (n / 2))
            return exactBinomialCoefficientLog(n, n - k);

        /*
         * Sum logs for values that could overflow
         */
        double logSum = 0;

        // n!/(n-k)!
        for (long i = n - k + 1; i <= n; i++) {
            logSum += log(i);
        }

        // divide by k!
        for (long i = 2; i <= k; i++) {
            logSum -= log(i);
        }

        return logSum;
    }

}
