package com.milaboratory.core.sequence.tree;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PenaltyUtilsTest {
    @Test
    public void test1() throws Exception {
        byte[][] result = PenaltyUtils.getDifferencesCombination(0.5, new double[]{0.2, 0.3, 10.0});
        byte[][] asserted = {{}, {0}, {1}, {0, 0}, {0, 1}, {1, 0}};
        assertEquals(asserted.length, result.length);
        for (int i = result.length - 1; i >= 0; --i) {
            assertTrue(Arrays.equals(asserted[i], result[i]));
        }
    }

    @Test
    public void test2() throws Exception {
        byte[][] result = PenaltyUtils.getDifferencesCombination(0.5, new double[]{0.3, 0.2, 10.0});
        byte[][] asserted = {{}, {1}, {0}, {1, 1}, {1, 0}, {0, 1}};
        assertEquals(asserted.length, result.length);
        for (int i = result.length - 1; i >= 0; --i) {
            assertTrue(Arrays.equals(asserted[i], result[i]));
        }
    }

    @Test
    public void test3() throws Exception {
        byte[][] result = PenaltyUtils.getDifferencesCombination(0.5, new double[]{0.2, 0.2, 10.0});
        byte[][] asserted = {{}, {0}, {1}, {0, 0}, {0, 1}, {1, 0}, {1, 1}};
        assertEquals(asserted.length, result.length);
        for (int i = result.length - 1; i >= 0; --i) {
            assertTrue(Arrays.equals(asserted[i], result[i]));
        }
    }

    @Test
    public void test4() throws Exception {
        byte[][] result = PenaltyUtils.getDifferencesCombination(0.5, new double[]{0.1, 0.1, 10.0}, new int[]{1, 1});
        byte[][] asserted = {{}, {0}, {1}, {0, 1}, {1, 0}};
        assertEquals(asserted.length, result.length);
        for (int i = result.length - 1; i >= 0; --i) {
            assertTrue(Arrays.equals(asserted[i], result[i]));
        }
    }
}
