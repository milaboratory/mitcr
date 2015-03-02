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
package com.milaboratory.mitcr.clsexport;

import com.milaboratory.mitcr.clsexport.io.util.PrimitiveMapIO;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class AbstractCloneSetMetadataIO {
    public static void write(DataOutput output, AbstractCloneSetMetadata metadata) throws IOException {
        output.writeLong(metadata.getUUID().getMostSignificantBits());
        output.writeLong(metadata.getUUID().getLeastSignificantBits());
        DataSetMetadataIO.get().write(output, metadata.getDataSetMetadata());
        PrimitiveMapIO.write(output, metadata.getProperties());
        output.writeInt(metadata.getClonesOffset());
        output.writeInt(metadata.getClonesCount());
        output.writeInt(metadata.getClustersOffset());
        output.writeInt(metadata.getClustersCount());
    }

    public static AbstractCloneSetMetadata read(DataInput input) throws IOException {
        long msb, lsb;
        msb = input.readLong();
        lsb = input.readLong();
        DataSetMetadata metadata = (DataSetMetadata) DataSetMetadataIO.get().read(input);
        Map<String, Object> map = PrimitiveMapIO.read(input);
        int cloOff = input.readInt();
        int cloCount = input.readInt();
        int cluOff = input.readInt();
        int cluCount = input.readInt();
        return new AbstractCloneSetMetadata(map, metadata, new UUID(msb, lsb), cloOff, cloCount, cluOff, cluCount, -1); //Format == -1
    }
}
