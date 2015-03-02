package com.milaboratory.mitcr.vdjmapping;

import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SimplestAlignmentTest {
    @Test
    public void test1() throws Exception {
        NucleotideSequence target = new NucleotideSequence("ATTAGACA"),
                query = new NucleotideSequence("ATTAGACA");
        SimplestAlignment alignment = SimplestAlignment.build(target, query, 3);
        assertEquals(target.size(), alignment.length);
        assertEquals(0, alignment.targetFrom);
        assertEquals(0, alignment.queryFrom);
    }

    @Test
    public void test2() throws Exception {
        NucleotideSequence target = new NucleotideSequence("ATTAGACA"),
                query = new NucleotideSequence("ATTGGACA");
        SimplestAlignment alignment = SimplestAlignment.build(target, query, 3);
        assertEquals(4, alignment.length);
        assertEquals(4, alignment.targetFrom);
        assertEquals(4, alignment.queryFrom);
    }

    @Test
    public void test3() throws Exception {
        NucleotideSequence target = new NucleotideSequence("ACA"),
                query = new NucleotideSequence("ATTGGACA");
        SimplestAlignment alignment = SimplestAlignment.build(target, query, 3);
        assertEquals(3, alignment.length);
        assertEquals(0, alignment.targetFrom);
        assertEquals(5, alignment.queryFrom);
    }

    @Test
    public void test4() throws Exception {
        NucleotideSequence target = new NucleotideSequence("ACA"),
                query = new NucleotideSequence("ATTGGACA");
        SimplestAlignment alignment = SimplestAlignment.build(target, query, 4);
        assertNull(alignment);
    }

    @Test
    public void test5() throws Exception {
        NucleotideSequence target = new NucleotideSequence("ATTAGACA"),
                query = new NucleotideSequence("AGTGGAGA");
        SimplestAlignment alignment = SimplestAlignment.build(target, query, 3);
        assertEquals(3, alignment.length);
        assertEquals(3, alignment.targetFrom);
        assertEquals(5, alignment.queryFrom);
    }

    @Test
    public void test6() throws Exception {
        NucleotideSequence target = new NucleotideSequence("ATTAGACA"),
                query = new NucleotideSequence("AGAC");
        SimplestAlignment alignment = SimplestAlignment.build(target, query, 3);
        assertEquals(4, alignment.length);
        assertEquals(3, alignment.targetFrom);
        assertEquals(0, alignment.queryFrom);
    }
}
