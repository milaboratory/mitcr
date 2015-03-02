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
 * Amino acid alphabet.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class AminoAcidAlphabet implements Alphabet {
    protected static char[] aa = {'*', 'A', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'K', 'L',
            'M', 'N', 'P', 'Q', 'R',
            'S', 'T', 'V', 'W', 'Y'};

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

    public static final AminoAcidAlphabet INSTANCE = new AminoAcidAlphabet();

    private AminoAcidAlphabet() {
    }

    public static AminoAcidAlphabet get() {
        return INSTANCE;
    }

    public char symbolFromCode(byte code) {
        return aa[code];
    }

    public byte codeFromSymbol(char symbol) {
        symbol = Character.toUpperCase(symbol);
        for (int i = 0; i < aa.length; ++i)
            if (aa[i] == symbol)
                return (byte) i;
        throw new RuntimeException("Unknown symbol.");
    }

    @Override
    public byte codesCount() {
        return (byte) aa.length;
    }

    public int alphabetCode() {
        return 1;
    }

    final SequenceBuilderFactory<AminoAcidSequence> FACTORY = new SequenceBuilderFactory<AminoAcidSequence>() {
        @Override
        public SequenceBuilder<AminoAcidSequence> create(int size) {
            return new AbstractAASequenceBuilder<AminoAcidSequence>(size) {
                @Override
                public AminoAcidSequence create() {
                    byte[] data = this.data;
                    this.data = null;
                    return AminoAcidSequenceImpl.createFromData(data);
                }
            };
        }
    };

    @Override
    public SequenceBuilderFactory<AminoAcidSequence> getBuilderFactory() {
        return FACTORY;
    }
}
