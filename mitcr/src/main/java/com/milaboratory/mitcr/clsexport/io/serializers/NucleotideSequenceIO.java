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
package com.milaboratory.mitcr.clsexport.io.serializers;

import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.mitcr.clsexport.io.BinaryContainerReader;
import com.milaboratory.mitcr.clsexport.io.BinaryContainerWriter;
import com.milaboratory.mitcr.clsexport.io.WrongStructureException;
import com.milaboratory.util.Bit2Array;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class NucleotideSequenceIO implements BinaryContainerReader, BinaryContainerWriter {
    public static final int TYPE_ID = 0xBEFCE;
    public static NucleotideSequenceIO INSTANCE = new NucleotideSequenceIO();

    @Override
    public boolean canRead(int typeId) {
        return TYPE_ID == typeId;
    }

    @Override
    public Object read(DataInput input) throws IOException {
        byte s = input.readByte();
        if (s != 'S')
            throw new WrongStructureException();
        Bit2Array storage = Bit2ArrayIO.read(input);
        return new NucleotideSequence(storage);
    }

    @Override
    public boolean canWrite(Object object) {
        return object.getClass() == NucleotideSequence.class;
    }

    @Override
    public int typeId() {
        return TYPE_ID;
    }

    @Override
    public void write(DataOutput output, Object object) throws IOException {
        NucleotideSequence sequence = (NucleotideSequence) object;
        output.writeByte('S');
        Bit2ArrayIO.write(output, sequence.getInnerData());
    }
}
