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
package com.milaboratory.core.sequence.util;

import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.core.sequence.Sequence;
import com.milaboratory.core.sequence.SequenceBuilder;
import com.milaboratory.core.sequence.quality.SequenceQualityPhred;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class SequencesUtils {
    public static int mismatchCount(Sequence seq0, Sequence seq1) {
        if (seq0.getAlphabet() != seq1.getAlphabet())
            throw new IllegalArgumentException("Different sequene alphabets");
        if (seq0.size() != seq1.size())
            return -1;
        int mm = 0;
        for (int i = 0; i < seq0.size(); ++i)
            if (seq0.codeAt(i) != seq1.codeAt(i))
                ++mm;
        return mm;
    }

    public static int mismatchCount(Sequence seq0, Sequence seq1, int maxMismatches) {
        if (seq0.getAlphabet() != seq1.getAlphabet())
            throw new IllegalArgumentException("Different sequene alphabets");
        if (seq0.size() != seq1.size())
            return -1;
        int mm = 0;
        for (int i = 0; i < seq0.size(); ++i)
            if (seq0.codeAt(i) != seq1.codeAt(i))
                if (++mm > maxMismatches)
                    return -1;
        return mm;
    }

    public static String highlightedMismatches(Sequence sequence, Sequence standard) {
        final Alphabet alphabet = standard.getAlphabet();
        if (alphabet != sequence.getAlphabet())
            throw new IllegalArgumentException();
        if (standard.size() != sequence.size())
            throw new IllegalArgumentException();
        char[] chars = new char[sequence.size()];
        byte code;
        for (int i = 0; i < standard.size(); ++i)
            if (standard.codeAt(i) != (code = sequence.codeAt(i)))
                chars[i] = Character.toUpperCase(alphabet.symbolFromCode(code));
            else
                chars[i] = Character.toLowerCase(alphabet.symbolFromCode(code));
        return new String(chars);
    }

    public static List<SequenceMismatch> mismatches(Sequence seq0, Sequence seq1) {
        if (seq0.getAlphabet() != seq1.getAlphabet())
            throw new IllegalArgumentException("Different sequene alphabets");
        if (seq0.size() != seq1.size())
            return Collections.EMPTY_LIST;
        List<SequenceMismatch> result = new ArrayList<>();
        for (int i = 0; i < seq0.size(); ++i)
            if (seq0.codeAt(i) != seq1.codeAt(i))
                result.add(new SequenceMismatch(i, seq0.codeAt(i), seq1.codeAt(i)));
        return result;
    }

    public static byte[] extractRawQualityValues(SequenceQualityPhred quality) {
        if (quality instanceof SequenceQualityPhred)
            return SequenceQualityPhred.getContent(quality);
        //TODO <----
        byte[] values = new byte[quality.size()];
        for (int i = 0; i < values.length; ++i)
            values[i] = quality.value(i);
        return values;
    }

    /*
     * A factory method of NucleotideSubSequence class. If sub-sequence coords lies out of target sequence it returns
     * null.
     *
     * @param sequence target sequence
     * @param from     from coordinate
     * @param length   length of subsequence
     * @return null or NucleotideSubSequence.
     */
    //public static NucleotideSubSequence getSubSequence(NucleotideSequence sequence, int from, int length) {
    //    if (from < 0 || from + length > sequence.size())
    //        return null;
    //    return new NucleotideSubSequence(sequence, from, length);
    //}


    /*
     * A factory method of NucleotideSubSequence class. If sub-sequence coords lies out of target sequence it returns
     * null.
     *
     * @param sequence target sequence
     * @param from     from coordinate
     * @return null or NucleotideSubSequence.
     */
    //public static NucleotideSubSequence getSubSequence(NucleotideSequence sequence, int from) {
    //    if (from < 0 || from > sequence.size() - 1)
    //        return null;
    //    return new NucleotideSubSequence(sequence, from, sequence.size() - from);
    //}

    public static <S extends Sequence> S cat(S... sequences) {
        if (sequences.length == 0)
            throw new IllegalArgumentException("Zero arguments");

        if (sequences.length == 1)
            return sequences[0];

        int size = 0;
        for (S s : sequences)
            size += s.size();

        SequenceBuilder<S> builder = sequences[0].getAlphabet().getBuilderFactory().create(size);
        int pointer = 0;
        for (S s : sequences) {
            builder.copyFrom(s, 0, pointer, s.size());
            pointer += s.size();
        }
        return builder.create();
    }
}
