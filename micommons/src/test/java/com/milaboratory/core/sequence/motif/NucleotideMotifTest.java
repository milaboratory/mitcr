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
package com.milaboratory.core.sequence.motif;

import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Bolotin Dmitriy <bolotin.dmitriy@gmail.com>
 */
public class NucleotideMotifTest {
    @Test
    public void testMatches01() {
        NucleotideMotif nm = new NucleotideMotif("ATCG");
        assertTrue(nm.matches(new NucleotideSequence("ATCG"), 0));
        assertFalse(nm.matches(new NucleotideSequence("ACCG"), 0));
    }

    @Test
    public void testMatches02() {
        NucleotideMotif nm = new NucleotideMotif("GAGGAGACGGTGACCRKGGT");
        assertTrue(nm.matches(new NucleotideSequence("GAGGAGACGGTGACCGGGGT"), 0));
        assertTrue(nm.matches(new NucleotideSequence("GAGGAGACGGTGACCGTGGT"), 0));
        assertFalse(nm.matches(new NucleotideSequence("GAGGAGACGGTGACCGCGGT"), 0));
    }

    @Test
    public void testMatches03() {
        NucleotideMotif nm = new NucleotideMotif("GAGGAGACGGTGACCRKGGT");
        assertTrue(nm.matches(new NucleotideSequence("GACAGAGGAGACGGTGACCGGGGTAA"), 4));
        assertTrue(nm.matches(new NucleotideSequence("CAGATGAGGAGACGGTGACCGTGGTGG"), 5));
        assertFalse(nm.matches(new NucleotideSequence("CCAGAGGAGACGGTGACCGCGGTG"), 3));
    }

    @Test
    public void testToString01() {
        NucleotideMotif nm = new NucleotideMotif("GAGGAGACGGTGACCRKGGT");
        assertEquals(nm.toString(), "GAGGAGACGGTGACCRKGGT");
    }

    @Test
    public void rcTest() {
        NucleotideMotif nm = new NucleotideMotif("ATGC");
        System.out.println(nm.reverseComplement());
        assertTrue(nm.reverseComplement().equals(new NucleotideMotif("GCAT")));
    }

    @Test
    public void testFindMatch01() {
        NucleotideMotif nm = new NucleotideMotif("GAGGAGACGGTGACCRKGGT");
        assertEquals(nm.findMatch(new NucleotideSequence("GACAGAGGAGACGGTGACCGGGGTAA")), 4);
        assertEquals(nm.findMatch(new NucleotideSequence("CAGATGAGGAGACGGTGACCGTGGTGG")), 5);
        assertEquals(nm.findMatch(new NucleotideSequence("CCAGAGGAGACGGTGACCGCGGTG")), -1);
    }
}
