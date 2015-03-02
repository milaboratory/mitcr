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

import com.milaboratory.core.sequence.quality.SequenceQualityPhred;
import com.milaboratory.mitcr.clsexport.io.BinaryContainerReader;
import com.milaboratory.mitcr.clsexport.io.BinaryContainerWriter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class SequenceQualityIO implements BinaryContainerReader, BinaryContainerWriter {
    public static final int TYPE_ID = 0xBEFCC;
    public static final SequenceQualityIO INSTANCE = new SequenceQualityIO();

    @Override
    public boolean canRead(int typeId) {
        return typeId == TYPE_ID;
    }

    @Override
    public Object read(DataInput input) throws IOException {
        byte t = input.readByte();
        if (t == 0) {
            int length = input.readInt();
            byte[] data = new byte[length];
            input.readFully(data);
            return new SequenceQualityPhred(data);
        }
        throw new RuntimeException("Sequence quality type not supported or wrong file structure");
    }

    @Override
    public boolean canWrite(Object object) {
        return object.getClass() == SequenceQualityPhred.class;
    }

    @Override
    public int typeId() {
        return TYPE_ID;
    }

    @Override
    public void write(DataOutput output, Object object) throws IOException {
        if (object.getClass() == SequenceQualityPhred.class) {
            SequenceQualityPhred sq = (SequenceQualityPhred) object;
            output.writeByte(0);
            output.writeInt(sq.size());
            output.write(sq.getInnerData());
        }
    }
}
