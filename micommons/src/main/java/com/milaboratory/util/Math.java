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

import static java.lang.Math.PI;
import static org.apache.commons.math.util.FastMath.log;

public class Math {
    public static float log10Count(int count, int total) {
        if (count > 0)
            return (float) java.lang.Math.log10((double) count / total);
        return (float) (java.lang.Math.log10(1.0 / total) - 1.0 / java.lang.Math.log(10));
    }

    public static byte min(byte[] array) {
        byte min = Byte.MAX_VALUE;
        for (byte b : array)
            if (b < min)
                min = b;
        return min;
    }

    public static double binomialCoefficientLog(long n, long k) {
        if (n < 0 || k < 0 || k > n)
            throw new IllegalArgumentException();

        if (k == 0 || n == 0)
            return 0.0;

        if (k > (n / 2))
            return binomialCoefficientLog(n, n - k);

        //Error <= 0.1%
        if (k < 12)
            return exactBinomialCoefficientLog(n, k);
        else
            return approxBinomialCoefficientLog(n, k);
    }

    private static double exactBinomialCoefficientLog(long n, long k) {
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

    private static double approxBinomialCoefficientLog(long n, long k) {
        return (n + .5) * log(n) - (k + .5) * log(k) - (n - k + .5) * log(n - k) - .5 * log(2 * PI);
    }

    /**
     * Sorts the specified array of integers into ascending order and returns source permutation which acts on the final
     * sorted array and get source array. So it inverse permutation acting on specified array sort him.
     *
     * @param source source array to sort
     * @return inverse permutation, which sorts source array
     */
    public static int[] sort(int[] source) {
        int[] permutation = new int[source.length];
        for (int i = 0; i < permutation.length; ++i)
            permutation[i] = i;
        sort1(source, 0, source.length, permutation);
        return permutation;
    }

    public static void sort(int[] source, int[] permutation) {
        for (int i = 0; i < permutation.length; ++i)
            permutation[i] = i;
        sort1(source, 0, source.length, permutation);
    }

    /**
     * Sorts the specified sub-array of integers into ascending order.
     */
    private static void sort1(int x[], int off, int len, int[] permutation) {

        // Insertion sort on smallest arrays
        if (len < 7) {
            for (int i = off; i < len + off; i++)
                for (int j = i; j > off && x[j - 1] > x[j]; j--)
                    swap(x, j, j - 1, permutation);
            return;
        }

        // Choose a partition element, v
        int m = off + (len >> 1);       // Small arrays, middle element
        if (len > 7) {
            int l = off;
            int n = off + len - 1;
            if (len > 40) {        // Big arrays, pseudomedian of 9
                int s = len / 8;
                l = med3(x, l, l + s, l + 2 * s);
                m = med3(x, m - s, m, m + s);
                n = med3(x, n - 2 * s, n - s, n);
            }
            m = med3(x, l, m, n); // Mid-size, med of 3
        }
        int v = x[m];

        // Establish Invariant: v* (<v)* (>v)* v*
        int a = off, b = a, c = off + len - 1, d = c;
        while (true) {
            while (b <= c && x[b] <= v) {
                if (x[b] == v)
                    swap(x, a++, b, permutation);
                b++;
            }
            while (c >= b && x[c] >= v) {
                if (x[c] == v)
                    swap(x, c, d--, permutation);
                c--;
            }
            if (b > c)
                break;
            swap(x, b++, c--, permutation);
        }

        // Swap partition elements back to middle
        int s, n = off + len;
        s = java.lang.Math.min(a - off, b - a);
        vecswap(x, off, b - s, s, permutation);
        s = java.lang.Math.min(d - c, n - d - 1);
        vecswap(x, b, n - s, s, permutation);

        // Recursively sort non-partition-elements
        if ((s = b - a) > 1)
            sort1(x, off, s, permutation);
        if ((s = d - c) > 1)
            sort1(x, n - s, s, permutation);

    }

    private static void swap(int x[], int a, int b, int[] permutation) {
        swap(x, a, b);
        swap(permutation, a, b);
    }

    /**
     * Swaps x[a] with x[b].
     */
    private static void swap(int x[], int a, int b) {
        int t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    private static void vecswap(int x[], int a, int b, int n, int[] permutation) {
        for (int i = 0; i < n; i++, a++, b++)
            swap(x, a, b, permutation);
    }

    /**
     * Returns the index of the median of the three indexed integers.
     */
    private static int med3(int x[], int a, int b, int c) {
        return (x[a] < x[b]
                ? (x[b] < x[c] ? b : x[a] < x[c] ? c : a)
                : (x[b] > x[c] ? b : x[a] > x[c] ? c : a));
    }

    // ------------------------------------

    /**
     * Sorts the specified array of integers into ascending order and returns source permutation which acts on the final
     * sorted array and get source array. So it inverse permutation acting on specified array sort him.
     *
     * @param source source array to sort
     * @return inverse permutation, which sorts source array
     */
    public static int[] sort(long[] source) {
        int[] permutation = new int[source.length];
        for (int i = 0; i < permutation.length; ++i)
            permutation[i] = i;
        sort1(source, 0, source.length, permutation);
        return permutation;
    }

    public static void sort(long[] source, int[] permutation) {
        for (int i = 0; i < permutation.length; ++i)
            permutation[i] = i;
        sort1(source, 0, source.length, permutation);
    }

    /**
     * Sorts the specified sub-array of integers into ascending order.
     */
    private static void sort1(long x[], int off, int len, int[] permutation) {

        // Insertion sort on smallest arrays
        if (len < 7) {
            for (int i = off; i < len + off; i++)
                for (int j = i; j > off && x[j - 1] > x[j]; j--)
                    swap(x, j, j - 1, permutation);
            return;
        }

        // Choose a partition element, v
        int m = off + (len >> 1);       // Small arrays, middle element
        if (len > 7) {
            int l = off;
            int n = off + len - 1;
            if (len > 40) {        // Big arrays, pseudomedian of 9
                int s = len / 8;
                l = med3(x, l, l + s, l + 2 * s);
                m = med3(x, m - s, m, m + s);
                n = med3(x, n - 2 * s, n - s, n);
            }
            m = med3(x, l, m, n); // Mid-size, med of 3
        }
        long v = x[m];

        // Establish Invariant: v* (<v)* (>v)* v*
        int a = off, b = a, c = off + len - 1, d = c;
        while (true) {
            while (b <= c && x[b] <= v) {
                if (x[b] == v)
                    swap(x, a++, b, permutation);
                b++;
            }
            while (c >= b && x[c] >= v) {
                if (x[c] == v)
                    swap(x, c, d--, permutation);
                c--;
            }
            if (b > c)
                break;
            swap(x, b++, c--, permutation);
        }

        // Swap partition elements back to middle
        int s, n = off + len;
        s = java.lang.Math.min(a - off, b - a);
        vecswap(x, off, b - s, s, permutation);
        s = java.lang.Math.min(d - c, n - d - 1);
        vecswap(x, b, n - s, s, permutation);

        // Recursively sort non-partition-elements
        if ((s = b - a) > 1)
            sort1(x, off, s, permutation);
        if ((s = d - c) > 1)
            sort1(x, n - s, s, permutation);

    }

    private static void swap(long x[], int a, int b, int[] permutation) {
        swap(x, a, b);
        swap(permutation, a, b);
    }

    /**
     * Swaps x[a] with x[b].
     */
    private static void swap(long x[], int a, int b) {
        long t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    private static void vecswap(long x[], int a, int b, int n, int[] permutation) {
        for (int i = 0; i < n; i++, a++, b++)
            swap(x, a, b, permutation);
    }

    /**
     * Returns the index of the median of the three indexed integers.
     */
    private static int med3(long x[], int a, int b, int c) {
        return (x[a] < x[b]
                ? (x[b] < x[c] ? b : x[a] < x[c] ? c : a)
                : (x[b] > x[c] ? b : x[a] > x[c] ? c : a));
    }

    // ------------------------------------

    /**
     * Sorts the specified array of integers into ascending order and returns source permutation which acts on the final
     * sorted array and get source array. So it inverse permutation acting on specified array sort him.
     *
     * @param source source array to sort
     * @return inverse permutation, which sorts source array
     */
    public static int[] sort(double[] source) {
        int[] permutation = new int[source.length];
        for (int i = 0; i < permutation.length; ++i)
            permutation[i] = i;
        sort1(source, 0, source.length, permutation);
        return permutation;
    }

    public static void sort(double[] source, int[] permutation) {
        for (int i = 0; i < permutation.length; ++i)
            permutation[i] = i;
        sort1(source, 0, source.length, permutation);
    }

    /**
     * Sorts the specified sub-array of integers into ascending order.
     */
    private static void sort1(double x[], int off, int len, int[] permutation) {

        // Insertion sort on smallest arrays
        if (len < 7) {
            for (int i = off; i < len + off; i++)
                for (int j = i; j > off && x[j - 1] > x[j]; j--)
                    swap(x, j, j - 1, permutation);
            return;
        }

        // Choose a partition element, v
        int m = off + (len >> 1);       // Small arrays, middle element
        if (len > 7) {
            int l = off;
            int n = off + len - 1;
            if (len > 40) {        // Big arrays, pseudomedian of 9
                int s = len / 8;
                l = med3(x, l, l + s, l + 2 * s);
                m = med3(x, m - s, m, m + s);
                n = med3(x, n - 2 * s, n - s, n);
            }
            m = med3(x, l, m, n); // Mid-size, med of 3
        }
        double v = x[m];

        // Establish Invariant: v* (<v)* (>v)* v*
        int a = off, b = a, c = off + len - 1, d = c;
        while (true) {
            while (b <= c && x[b] <= v) {
                if (x[b] == v)
                    swap(x, a++, b, permutation);
                b++;
            }
            while (c >= b && x[c] >= v) {
                if (x[c] == v)
                    swap(x, c, d--, permutation);
                c--;
            }
            if (b > c)
                break;
            swap(x, b++, c--, permutation);
        }

        // Swap partition elements back to middle
        int s, n = off + len;
        s = java.lang.Math.min(a - off, b - a);
        vecswap(x, off, b - s, s, permutation);
        s = java.lang.Math.min(d - c, n - d - 1);
        vecswap(x, b, n - s, s, permutation);

        // Recursively sort non-partition-elements
        if ((s = b - a) > 1)
            sort1(x, off, s, permutation);
        if ((s = d - c) > 1)
            sort1(x, n - s, s, permutation);

    }

    private static void swap(double x[], int a, int b, int[] permutation) {
        swap(x, a, b);
        swap(permutation, a, b);
    }

    /**
     * Swaps x[a] with x[b].
     */
    private static void swap(double x[], int a, int b) {
        double t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    private static void vecswap(double x[], int a, int b, int n, int[] permutation) {
        for (int i = 0; i < n; i++, a++, b++)
            swap(x, a, b, permutation);
    }

    /**
     * Returns the index of the median of the three indexed integers.
     */
    private static int med3(double x[], int a, int b, int c) {
        return (x[a] < x[b]
                ? (x[b] < x[c] ? b : x[a] < x[c] ? c : a)
                : (x[b] > x[c] ? b : x[a] > x[c] ? c : a));
    }
}
