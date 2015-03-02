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

/**
 * Main implementation of amino acid sequence.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class AminoAcidSequenceImpl extends AminoAcidSequence {
    final byte[] data;

    private AminoAcidSequenceImpl(byte[] data) {
        this.data = data;
    }

    @Override
    public byte codeAt(int position) {
        return data[position];
    }

    @Override
    public int size() {
        return data.length;
    }

    public static AminoAcidSequenceImpl createFromData(byte[] data) {
        return new AminoAcidSequenceImpl((byte[]) data.clone());
    }

    public static AminoAcidSequenceImpl createFromSequence(String sequence) {
        return createFromSequence(sequence.toCharArray());
    }

    public static AminoAcidSequenceImpl createFromSequence(char[] chars) {
        byte[] data = new byte[chars.length];
        for (int i = 0; i < chars.length; ++i)
            data[i] = AminoAcidAlphabet.INSTANCE.codeFromSymbol(chars[i]);
        return new AminoAcidSequenceImpl(data);
    }
}
