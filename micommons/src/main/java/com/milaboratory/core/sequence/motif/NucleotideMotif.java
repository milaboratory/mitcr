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
import com.milaboratory.util.BitArray;

/**
 * Implementation of simplest nucleotide sequence motif.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class NucleotideMotif {
    private final BitArray store;
    private final int size;

    /**
     * Inner private constructor for reverseComplement motif
     */
    NucleotideMotif(BitArray store) {
        this.store = store;
        this.size = store.size() >> 2;
    }

    /**
     * Creates motif from it's String representation.
     */
    public NucleotideMotif(String motif) {
        this.store = new BitArray(motif.length() << 2);
        for (int i = 0; i < motif.length(); ++i) {
            byte[] codes = NucleotideWildcards.getCodes(motif.charAt(i));
            if (codes == null)
                throw new RuntimeException("Illegal nucleotide/wildcard: \"" + motif.charAt(i) + "\".");
            for (byte b : codes)
                store.set(b + (i << 2));
        }
        this.size = motif.length();
    }

    /**
     * Size of motif.
     */
    public final int size() {
        return size;
    }

    /**
     * Return true if motif matches sub-sequence of nucleotide sequence from (from) inclusive to (from + size())
     * exclusive.
     *
     * @param sequence target sequence
     * @param from     offset to start matching
     */
    public final boolean matches(NucleotideSequence sequence, int from) {
        if (from < 0 || from >= sequence.size())
            throw new IllegalArgumentException();
        if (sequence.size() < from + size())
            return false;
        for (int i = 0; i < size; ++i)
            if (!store.get(sequence.codeAt(from + i) + (i << 2)))
                return false;
        return true;
    }

    /**
     * Return number of mismatches between motif and sub-sequence of nucleotide
     * sequence from (from) inclusive to (from + size()) exclusive.
     *
     * @param sequence target sequence
     * @param from offset to start matching
     * @return
     */
    /*public final int mismatchCount(NucleotideSequence sequence, int from) {
        if (from < 0 || from >= sequence.size())
            throw new IllegalArgumentException();
        if (sequence.size() < from + size())
            return -1;
        for (int i = 0; i < size; ++i)
            if (!store.get(sequence.codeAt(from + i) + (i << 2)))
                return false;
        return true;
    }*/


    /**
     * Return reverse-complement motif.
     */
    public final NucleotideMotif reverseComplement() {
        BitArray newStore = new BitArray(store.size());
        for (int i = 0; i < store.size(); ++i)
            if (store.get(store.size() - 1 - i))
                newStore.set(i);
        return new NucleotideMotif(newStore);
    }

    /**
     * Try to find this motif in sub-sequence.
     *
     * @param sequence target sequence
     * @param from     left subsequence border; inclusive
     * @param to       right subsequence border; exclusive
     * @return coordinate of first match (coordinate of first nucleotide in the match) or -1 if no matches found
     */
    public final int findMatch(NucleotideSequence sequence, int from, int to) {
        if (from < 0 || from >= sequence.size() || to < 0
                || to > sequence.size()
                || from > to)
            throw new IllegalArgumentException();
        to -= size;
        for (int i = from; i < to; ++i)
            if (matches(sequence, i))
                return i;
        return -1;
    }

    /**
     * Try to find this motif in sequence.
     *
     * @param sequence target sequence
     * @return coordinate of first match (coordinate of first nucleotide in the match) or -1 if no matches found
     */
    public final int findMatch(NucleotideSequence sequence) {
        return findMatch(sequence, 0, sequence.size());
    }

    /**
     * Direct motif querying method. Returns true if nucleotide with specified code is allowed on the specified
     * position.
     *
     * @param position position in the motif
     * @param code     nucleotide code (1..3)
     */
    public final boolean get(int position, byte code) {
        return store.get(code + (position << 2));
    }

    public String toString() {
        char[] chars = new char[size];
        int offset;
        for (int i = 0; i < size; ++i) {
            offset = i << 2;
            chars[i] = NucleotideWildcards.getSymbol(store.get(offset), store.get(offset + 1),
                    store.get(offset + 2), store.get(offset + 3));
        }
        return new String(chars);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NucleotideMotif that = (NucleotideMotif) o;

        if (!store.equals(that.store)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return store.hashCode();
    }
}
