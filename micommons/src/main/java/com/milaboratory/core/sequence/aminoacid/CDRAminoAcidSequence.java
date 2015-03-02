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
import com.milaboratory.core.sequence.Sequence;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.core.sequence.util.Translator;

/**
 * AA sequence with {@link CDRAminoAcidAlphabet} alphabet.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class CDRAminoAcidSequence extends Sequence {
    final byte[] data;
    private final int rLength, endLength, beginLength;
    private final NucleotideSequence nCdr;

    public CDRAminoAcidSequence(String sequence) {
        this.nCdr = null;

        int length = this.rLength = sequence.length();
        this.endLength = rLength / 2;
        this.beginLength = rLength - endLength;
        this.data = new byte[length];
        for (int i = 0; i < length; ++i)
            data[i] = CDRAminoAcidAlphabet.INSTANCE.codeFromSymbol(sequence.charAt(i));
    }

    public CDRAminoAcidSequence(NucleotideSequence cdr) {
        this.nCdr = cdr;

        int length = (cdr.size() + 2) / 3;
        this.rLength = cdr.size() / 3;
        this.endLength = rLength / 2;
        this.beginLength = rLength - endLength;
        this.data = new byte[length];
        for (int i = 0; i < beginLength; ++i)
            data[i] = Translator.getAminoAcid(cdr, i * 3);
        for (int i = 0; i < endLength; ++i)
            data[length - 1 - i] = Translator.getAminoAcid(cdr, cdr.size() - 3 - i * 3);
        if (rLength != length)
            data[beginLength] = 21;
    }

    private CDRAminoAcidSequence(byte[] data) {
        this.nCdr = null;

        this.data = data;
        this.rLength = data.length;
        this.endLength = rLength / 2;
        this.beginLength = rLength - endLength;
    }

    @Override
    public Alphabet getAlphabet() {
        return CDRAminoAcidAlphabet.INSTANCE;
    }

    @Override
    public byte codeAt(int position) {
        return data[position];
    }

    public int getTripletAtCDRPosition(int position) {
        if (position >= 0)
            return Translator.getTriplet(nCdr, position * 3);
        return Translator.getTriplet(nCdr, nCdr.size() + 3 * position);
    }

    public NucleotideSequence getNucleotideSequence() {
        return nCdr;
    }

    public byte getCodeAtCDRPosition(int position) {
        if (position >= 0)
            return data[position];
        return data[position + data.length];
    }

    public int getBeginLength() {
        return beginLength;
    }

    public int getEndLength() {
        return endLength;
    }

    public int getLength() {
        return data.length;
    }

    public int getRLength() {
        return rLength;
    }

    public boolean containStops() {
        for (byte b : data)
            if (b == 0)
                return true;
        return false;
    }

    public boolean isInFrame() {
        return (nCdr.size() % 3) == 0;
    }

    public void toNucleotideSequenceCoord(int position, CDRTriplet triplet) {
        if (position < beginLength) {
            triplet.setStart(position * 3, 3);
            return;
        }
        int fromEndPosition = data.length - 1 - position;
        if (fromEndPosition < endLength) {
            triplet.setStart(nCdr.size() - (fromEndPosition + 1) * 3, 3);
            return;
        }
        triplet.setStart(beginLength * 3, nCdr.size() % 3);
    }

    @Override
    public int size() {
        return data.length;
    }

    public static CDRAminoAcidSequence createFromData(byte[] data) {
        return new CDRAminoAcidSequence(data.clone());
    }
}
