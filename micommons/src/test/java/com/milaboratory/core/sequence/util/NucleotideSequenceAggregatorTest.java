package com.milaboratory.core.sequence.util;

import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import org.junit.Test;

public class NucleotideSequenceAggregatorTest {
    @Test
    public void test1() throws Exception {
        NucleotideSequenceAggregator aggr = new NucleotideSequenceAggregator(8, 2);
        aggr.putSequence(new NucleotideSequence("attacaca"));
        aggr.putSequence(new NucleotideSequence("tattacac"));
        aggr.putSequence(new NucleotideSequence("tattacaa"));

        System.out.println(aggr.getSequence(.5));
    }
}
