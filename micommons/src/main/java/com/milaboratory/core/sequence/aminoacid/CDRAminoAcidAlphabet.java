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
package com.milaboratory.core.sequence.aminoacid;

import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.core.sequence.SequenceBuilder;
import com.milaboratory.core.sequence.SequenceBuilderFactory;

/**
 * AA alphabet with additional symbols.<br/> "~" - non full codon. 2 or 1 nucleotides.<br/> "-" - no nucleotides
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class CDRAminoAcidAlphabet implements Alphabet {
    public static final CDRAminoAcidAlphabet INSTANCE = new CDRAminoAcidAlphabet();
    protected char[] aa = getChars();

    public static final byte Stop = 0;
    public static final byte A = 1;
    public static final byte C = 2;
    public static final byte D = 3;
    public static final byte E = 4;
    public static final byte F = 5;
    public static final byte G = 6;
    public static final byte H = 7;
    public static final byte I = 8;
    public static final byte K = 9;
    public static final byte L = 10;
    public static final byte M = 11;
    public static final byte N = 12;
    public static final byte P = 13;
    public static final byte Q = 14;
    public static final byte R = 15;
    public static final byte S = 16;
    public static final byte T = 17;
    public static final byte V = 18;
    public static final byte W = 19;
    public static final byte Y = 20;
    public static final byte NonFullCodon = 21;
    public static final byte NoNucleotides = 22;

    private CDRAminoAcidAlphabet() {
    }

    private static char[] getChars() {
        char[] chars = new char[AminoAcidAlphabet.aa.length + 2];
        for (int i = 0; i < AminoAcidAlphabet.aa.length; ++i)
            chars[i] = AminoAcidAlphabet.aa[i];
        chars[chars.length - 2] = '~';
        chars[chars.length - 1] = '-';
        return chars;
    }

    public char symbolFromCode(byte code) {
        return aa[code];
    }

    public byte codeFromSymbol(char symbol) {
        char s = Character.toUpperCase(symbol);
        for (int i = 0; i < aa.length; ++i)
            if (aa[i] == s)
                return (byte) i;
        throw new RuntimeException("Unknown symbol.");
    }

    @Override
    public byte codesCount() {
        return (byte) aa.length;
    }

    public int alphabetCode() {
        return 3;
    }


    final SequenceBuilderFactory<CDRAminoAcidSequence> FACTORY = new SequenceBuilderFactory<CDRAminoAcidSequence>() {
        @Override
        public SequenceBuilder<CDRAminoAcidSequence> create(int size) {
            return new AbstractAASequenceBuilder<CDRAminoAcidSequence>(size) {
                @Override
                public CDRAminoAcidSequence create() {
                    byte[] data = this.data;
                    this.data = null;
                    return CDRAminoAcidSequence.createFromData(data);
                }
            };
        }
    };

    @Override
    public SequenceBuilderFactory getBuilderFactory() {
        return FACTORY;
    }
}
