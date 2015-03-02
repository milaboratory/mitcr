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

import com.milaboratory.core.sequence.nucleotide.NucleotideAlphabet;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of standard IUPAC notations.<br/><br/> See <a href="http://users.ox.ac.uk/~linc1775/blueprint.htm">http://users.ox.ac.uk/~linc1775/blueprint.htm</a>.
 *
 * @author Bolotin Dmitriy <bolotin.dmitriy@gmail.com>
 */
public class NucleotideWildcards {
    private static final char[] CHARS;
    private static final Wildcard[] WILDCARDS;
    private static final Map<Byte, Wildcard> pCodeToWildcard = new HashMap<>();

    static {
        WILDCARDS = new Wildcard[]{
                //No nucleotide
                new Wildcard('-'),
                //Exact nucleotides
                new Wildcard('A', 'A'),
                new Wildcard('G', 'G'),
                new Wildcard('C', 'C'),
                new Wildcard('T', 'T'),
                //Two-letter wildcard
                new Wildcard('R', 'A', 'G'),
                new Wildcard('Y', 'C', 'T'),
                new Wildcard('S', 'G', 'C'),
                new Wildcard('W', 'A', 'T'),
                new Wildcard('K', 'G', 'T'),
                new Wildcard('M', 'A', 'C'),
                //Three-letter wildcards
                new Wildcard('B', 'C', 'G', 'T'),
                new Wildcard('D', 'A', 'G', 'T'),
                new Wildcard('H', 'A', 'C', 'T'),
                new Wildcard('V', 'A', 'C', 'G'),
                //Any nucleotide
                new Wildcard('N', 'A', 'C', 'G', 'T')
        };

        //Sorting to make binary search possible
        Arrays.sort(WILDCARDS, new Comparator<Wildcard>() {
            @Override
            public int compare(Wildcard o1, Wildcard o2) {
                return Character.compare(o1.character, o2.character);
            }
        });

        //Creating projections
        CHARS = new char[WILDCARDS.length];
        for (int i = 0; i < CHARS.length; ++i) {
            CHARS[i] = WILDCARDS[i].character;
            pCodeToWildcard.put(WILDCARDS[i].pCode, WILDCARDS[i]);
        }
    }

    /**
     * Returns sorted byte array with codes corresponding to specified symbol.<br/><br/> <p/> Allowed symbols: -, A, T,
     * G, C, R, Y, S, W, K, M, B, D, H, V, N<br/><br/> <p/> IUPAC standard notion used. For more information see <a
     * href="http://users.ox.ac.uk/~linc1775/blueprint.htm">http://users.ox.ac.uk/~linc1775/blueprint.htm</a>.
     */
    public static byte[] getCodes(char symbol) {
        char upper = Character.toUpperCase(symbol);
        int position = Arrays.binarySearch(CHARS, upper);
        if (position < 0)
            return null;
        return WILDCARDS[position].to;
    }

    /**
     * Returns wildcard symbol for specified nucleotides.<br/><br/> <p/> IUPAC standard notion used. For more
     * information see <a href="http://users.ox.ac.uk/~linc1775/blueprint.htm">http://users.ox.ac.uk/~linc1775/blueprint.htm</a>.
     *
     * @param a allow A
     * @param g allow G
     * @param c allow C
     * @param t allow T
     */
    public static char getSymbol(boolean a, boolean g, boolean c, boolean t) {
        byte pCode = 0;
        if (a)
            pCode |= 1;
        if (g)
            pCode |= 1 << 1;
        if (c)
            pCode |= 1 << 2;
        if (t)
            pCode |= 1 << 3;
        Wildcard wc = pCodeToWildcard.get(pCode);
        if (wc == null)
            throw new RuntimeException("Impossible exception. (Assertion fail)");
        return wc.character;
    }

    private static class Wildcard {
        public final char character;
        public final byte[] to;
        public final byte pCode;

        public Wildcard(char character, char... to) {
            this.character = character;
            this.to = new byte[to.length];
            byte pCode = 0;
            for (int i = 0; i < to.length; ++i)
                pCode |= 1 << (this.to[i] = NucleotideAlphabet.INSTANCE.codeFromSymbol(to[i]));
            Arrays.sort(this.to);
            this.pCode = pCode;
        }
    }
}
