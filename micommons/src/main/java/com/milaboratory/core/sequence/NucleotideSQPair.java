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
package com.milaboratory.core.sequence;

import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.core.sequence.quality.SequenceQualityPhred;
import com.milaboratory.core.sequence.quality.SequenceQualityUtils;

import java.io.Serializable;

/**
 * Main container of information about nucleotide sequence.
 *
 * <p>Contains sequence and quality values for all nucleotides in it.</p>
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
//TODO docs
public final class NucleotideSQPair implements Serializable {
    private static final long serialVersionUID = 1L;

    private final NucleotideSequence sequence;
    private final SequenceQualityPhred quality;

    public NucleotideSQPair(String sequence) {
        this(new NucleotideSequence(sequence));
    }

    public NucleotideSQPair(NucleotideSequence sequence) {
        this.sequence = sequence;
        this.quality = SequenceQualityUtils.createGoodQualityObject(sequence.size());
    }

    public NucleotideSQPair(String sequence, String quality) {
        this(new NucleotideSequence(sequence),
                new SequenceQualityPhred(quality));
    }

    public NucleotideSQPair(NucleotideSequence sequence, SequenceQualityPhred quality) {
        if (sequence.size() != quality.size())
            throw new IllegalArgumentException();
        this.sequence = sequence;
        this.quality = quality;
    }

    /**
     * Generates a new instance of NucleotideSQPair containing sub sequence. If to &lt; from then reverse complement
     * will be returned.
     *
     * @param from inclusive
     * @param to   exclusive
     */
    //TODO test required
    public NucleotideSQPair getRange(int from, int to) {
        if (from >= size() || from < 0 || to > size() || to < 0)
            throw new IndexOutOfBoundsException("\"from\" or \"to\" are out of range.");
        NucleotideSequence s;
        SequenceQualityPhred q;
        if (to < from) {
            s = sequence.getRange(to + 1, from + 1).getReverseComplement(); //TODO possible optimization point
            //new NucleotideRCSequence(new NucleotideSubSequence(sequence, to + 1, from - to));
            q = quality.getRange(to + 1, from + 1).reverse(); //TODO possible optimization point
            //new SubSequenceQuality(quality, to + 1, from - to).reverse();
        } else {
            s = sequence.getRange(from, to);
            //new NucleotideSubSequence(sequence, from, to - from);
            q = quality.getRange(from, to);
            //new SubSequenceQuality(quality, from, to - from);
        }
        return new NucleotideSQPair(s, q);
    }

    public NucleotideSQPair getRC() {
        return new NucleotideSQPair(sequence.getReverseComplement(), quality.reverse());
    }

    public int size() {
        return sequence.size();
    }

    public NucleotideSequence getSequence() {
        return sequence;
    }

    public SequenceQualityPhred getQuality() {
        return quality;
    }

    public static String toPrettyString(NucleotideSQPair pair) {
        if (pair == null)
            return "null";
        String seq = pair.sequence.toString();
        String qual = pair.quality.toString();
        return seq + '\n' + qual;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NucleotideSQPair that = (NucleotideSQPair) o;

        if (!quality.equals(that.quality)) return false;
        if (!sequence.equals(that.sequence)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = sequence.hashCode();
        result = 31 * result + quality.hashCode();
        return result;
    }
}
