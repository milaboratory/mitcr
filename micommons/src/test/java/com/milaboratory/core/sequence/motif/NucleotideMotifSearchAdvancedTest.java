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

/*
 */
package com.milaboratory.core.sequence.motif;

import com.milaboratory.core.sequence.SequencingErrorType;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Bolotin Dmitriy <bolotin.dmitriy@gmail.com>
 */
public class NucleotideMotifSearchAdvancedTest {
    private static final NucleotideMotif nm = new NucleotideMotif("GAGGAGACGGTGACCRKGGT");
    private static final NucleotideMotifSearchAdvanced search =
            new NucleotideMotifSearchAdvanced(nm, 1, 2, 3, SequencingErrorType.Mismatch);
    //                                |0        |10       |20       |30       |40       |50       |60       |70
    //                                   --------------------   -----M--------------      ---M----------------
    private static final String s0 = "AATGAGGAGACGGTGACCGGGGTGTCGAGGATACGGTGACCGGGGTGTAATGGAGCAGACGGTGACCGGGGTTGC";
    private static final String q0 = "------------------.-----------..----------------------------.--------------";

    @Test
    public void mmExactTest01() {
        int result = search.search(new NucleotideSequence(s0),
                LowQualityIndicator.Utils.wrap(q0));
        assertEquals(result, 3);
    }

    @Test
    public void mmComparisonTest01() {
        int result = search.search(new NucleotideSequence(s0),
                LowQualityIndicator.Utils.wrap(q0),
                23, s0.length());
        assertEquals(result, 26);
    }

    //
    //                                |0        |10       |20       |30       |40       |50       |60       |70
    //                                   --------M-----------   ----------------M---      ---M----------------
    private static final String s1 = "AATGAGGAGACTGTGACCGGGGTGTCGAGGAGACGGTGACCGAGGTGTAATGGAGCAGACGGTGACCGGGGTTGC";
    private static final String q1 = "-----.------------.-----------..-------.--------------------.--------------";

    @Test
    public void mmComparisonTest02() {
        int result = search.search(new NucleotideSequence(s1),
                LowQualityIndicator.Utils.wrap(q1));
        assertEquals(result, 52);
    }

    //
    //                                |0        |10       |20       |30       |40       |50       |60       |70
    //                                   --------M-----------   ----------------M---      ---M----------M-----
    private static final String s2 = "AATGAGGAGACTGTGACCGGGGTGTCGAGGAGACGGTGACCGAGGTGTAATGGAGCAGACGGTGACTGGGGTTGC";
    private static final String q2 = "-----.------------.-----------..-------.--------------------.--------------";

    @Test
    public void mmComparisonTest03() {
        int result = search.search(new NucleotideSequence(s2),
                LowQualityIndicator.Utils.wrap(q2));
        assertEquals(result, 3);
    }

    //
    //                                |0        |10       |20       |30       |40       |50       |60       |70
    //                                    --------------------   -----M----M----M----      ---M----------M-----
    private static final String sf0 = "AATGAGGAGACGGTGACCGGGGTGTCGAGGACACGGGGACCGAGGTGTAATGGAGCAGACGGTGACTGGGGTTGC";
    private static final String qf0 = "-----.-.-------.--..-----------.---------.------------------.--------------";

    @Test
    public void mmFalseTest01() {
        //Focused on exact match rejection
        int result = search.search(new NucleotideSequence(sf0),
                LowQualityIndicator.Utils.wrap(qf0),
                0, 25);
        assertEquals(result, -1);
    }

    @Test
    public void mmFalseTest02() {
        int result = search.search(new NucleotideSequence(sf0),
                LowQualityIndicator.Utils.wrap(qf0));
        assertEquals(result, -1);
    }
}
