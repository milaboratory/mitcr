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

import com.milaboratory.core.sequence.aminoacid.AminoAcidAlphabet;
import com.milaboratory.core.sequence.aminoacid.AminoAcidSequence;
import com.milaboratory.core.sequence.aminoacid.AminoAcidSequenceImpl;
import com.milaboratory.core.sequence.nucleotide.NucleotideAlphabet;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;

public final class Translator {
    private static byte[] code = null;

    private static synchronized void init() {
        if (code != null)
            return;
        char[] Base1 = "ttttttttttttttttccccccccccccccccaaaaaaaaaaaaaaaagggggggggggggggg".toCharArray();
        char[] Base2 = "ttttccccaaaaggggttttccccaaaaggggttttccccaaaaggggttttccccaaaagggg".toCharArray();
        char[] Base3 = "tcagtcagtcagtcagtcagtcagtcagtcagtcagtcagtcagtcagtcagtcagtcagtcag".toCharArray();
        char[] AA = "FFLLSSSSYY**CC*WLLLLPPPPHHQQRRRRIIIMTTTTNNKKSSRRVVVVAAAADDEEGGGG".toCharArray();
        code = new byte[Base1.length];
        int triplet;
        byte b0, b1, b2;
        for (int i = 0; i < Base1.length; ++i) {
            b0 = NucleotideAlphabet.INSTANCE.codeFromSymbol(Base1[i]);
            b1 = NucleotideAlphabet.INSTANCE.codeFromSymbol(Base2[i]);
            b2 = NucleotideAlphabet.INSTANCE.codeFromSymbol(Base3[i]);
            triplet = (b0 << 4) | (b1 << 2) | b2;
            code[triplet] = AminoAcidAlphabet.get().codeFromSymbol(AA[i]);
        }
    }

    public static int getTriplet(NucleotideSequence nSequence, int tripletStart) {
        int triplet = (nSequence.codeAt(tripletStart) << 4) | (nSequence.codeAt(tripletStart + 1) << 2) | nSequence.codeAt(tripletStart + 2);
        return triplet;
    }

    public static byte getAminoAcid(int triplet) {
        init();
        return code[triplet];
    }

    public static byte getAminoAcid(NucleotideSequence nSequence, int tripletStart) {
        init();
        return code[getTriplet(nSequence, tripletStart)];
    }

    public static AminoAcidSequence translate(NucleotideSequence nSequence, byte frame) {
        init();
        if (frame > 3 || frame < -3 || frame == 0)
            throw new IllegalArgumentException();
        if (frame < 0)
            throw new UnsupportedOperationException(); // ))
        frame -= 1;
        int size = (nSequence.size() - frame) / 3;
        byte[] aaData = new byte[size];
        int triplet;
        for (int i = 0; i < size; i++) {
            triplet = (nSequence.codeAt(i * 3 + frame) << 4) | (nSequence.codeAt(i * 3 + frame + 1) << 2) | nSequence.codeAt(i * 3 + frame + 2);
            aaData[i] = code[triplet];
        }
        return AminoAcidSequenceImpl.createFromData(aaData);
    }

    /**
     * Translates nucleotide sequence to amino acid sequence. Returns null in case stop codon is encountered
     *
     * @param nSequence Sequence to translate
     * @param frame     Reading frame; allowed range [1..3]
     * @return Amino acid sequence or null, if stop codon is encountered
     */
    public static AminoAcidSequence translateWithoutStops(NucleotideSequence nSequence, byte frame) {
        init();
        if (frame > 3 || frame < -3 || frame == 0)
            throw new IllegalArgumentException();
        if (frame < 0)
            throw new UnsupportedOperationException(); // ))
        frame -= 1;
        int size = (nSequence.size() - frame) / 3;
        byte[] aaData = new byte[size];
        int triplet;
        for (int i = 0; i < size; i++) {
            triplet = (nSequence.codeAt(i * 3 + frame) << 4) | (nSequence.codeAt(i * 3 + frame + 1) << 2) | nSequence.codeAt(i * 3 + frame + 2);
            aaData[i] = code[triplet];
            if (aaData[i] == (byte) 0)
                return null;
        }
        return AminoAcidSequenceImpl.createFromData(aaData);
    }
}
