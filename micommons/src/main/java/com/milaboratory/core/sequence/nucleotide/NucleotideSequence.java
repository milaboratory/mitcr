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
package com.milaboratory.core.sequence.nucleotide;

import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.core.sequence.Sequence;
import com.milaboratory.util.Bit2Array;

import java.io.Serializable;

import static com.milaboratory.core.sequence.nucleotide.NucleotideAlphabet.INSTANCE;

/**
 * Nucleotide sequence.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class NucleotideSequence extends Sequence implements Serializable {
    private static final long serialVersionUID = 1L;
    final Bit2Array data;

    public NucleotideSequence(String sequence) {
        data = new Bit2Array(sequence.length());
        byte code;
        for (int i = 0; i < sequence.length(); ++i)
            data.set(i, INSTANCE.codeFromSymbol(sequence.charAt(i)));
    }

    public NucleotideSequence(char[] sequence) {
        data = new Bit2Array(sequence.length);
        byte code;
        for (int i = 0; i < sequence.length; ++i)
            data.set(i, INSTANCE.codeFromSymbol(sequence[i]));
    }


    public NucleotideSequence(Bit2Array data) {
        this.data = data.clone();
    }

    NucleotideSequence(Bit2Array data, boolean unsafe) {
        this.data = data;
    }

    @Override
    public byte codeAt(int position) {
        return (byte) data.get(position);
    }

    @Override
    public int size() {
        return data.size();
    }

    public NucleotideSequence getRange(int from, int to) {
        return new NucleotideSequence(data.getRange(from, to), true);
    }

    /**
     * Returns reverse complement of this sequence.
     *
     * @return reverse complement sequence
     */
    public NucleotideSequence getReverseComplement() {
        return new NucleotideSequence(transformToRC(data), true);
    }

    public Bit2Array getInnerData() {
        return data.clone();
    }

    @Override
    public Alphabet getAlphabet() {
        return INSTANCE;
    }

    public static NucleotideSequence fromStorage(Bit2Array b2a) {
        return new NucleotideSequence(b2a.clone(), true);
    }

    public static NucleotideSequence fromSequence(byte[] sequence, int offset, int length) {
        Bit2Array storage = new Bit2Array(length);
        for (int i = 0; i < length; ++i)
            storage.set(i, INSTANCE.codeFromSymbol((char) sequence[offset + i]));
        return new NucleotideSequence(storage, true);
    }

    private static Bit2Array transformToRC(Bit2Array data) {
        Bit2Array newData = new Bit2Array(data.size());
        int reverseCoord;
        for (int coord = 0; coord < data.size(); coord++) {
            reverseCoord = data.size() - 1 - coord;
            newData.set(coord, (~data.get(reverseCoord)) & 0x3);
        }
        return newData;
    }
}
