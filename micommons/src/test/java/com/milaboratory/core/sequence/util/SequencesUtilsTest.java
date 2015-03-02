package com.milaboratory.core.sequence.util;

import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import org.junit.Test;

import static com.milaboratory.core.sequence.util.SequencesUtils.cat;
import static org.junit.Assert.assertEquals;

public class SequencesUtilsTest {
    @Test
    public void testCat() throws Exception {
        String seq1s = "gttacagc",
                seq2s = "gctatacgatgc";
        NucleotideSequence seq1 = new NucleotideSequence(seq1s),
                seq2 = new NucleotideSequence(seq2s),
                catAssert = new NucleotideSequence(seq1s + seq2s);

        assertEquals(catAssert, cat(seq1, seq2));
        assertEquals(catAssert.hashCode(), cat(seq1, seq2).hashCode()); //Just in case
    }
}
