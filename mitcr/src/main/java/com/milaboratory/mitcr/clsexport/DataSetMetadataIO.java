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

import com.milaboratory.mitcr.clsexport.io.AbstractBinaryContainerIO;
import com.milaboratory.mitcr.clsexport.io.util.PrimitiveMapIO;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class DataSetMetadataIO extends AbstractBinaryContainerIO {
    private static final DataSetMetadataIO instance = new DataSetMetadataIO();

    public static DataSetMetadataIO get() {
        return instance;
    }

    @Override
    protected Class getEntityClass() {
        return DataSetMetadata.class;
    }

    @Override
    public Object read(DataInput input) throws IOException {
        long msb = input.readLong();
        long lsb = input.readLong();
        int offset = input.readInt();
        int count = input.readInt();
        Map<String, Object> props = PrimitiveMapIO.read(input);
        DataSetMetadata dsm = new DataSetMetadata(new UUID(msb, lsb), offset, count, props);
        return dsm;
    }

    @Override
    public int typeId() {
        return 0xEBABABA2;
    }

    @Override
    public void write(DataOutput output, Object object) throws IOException {
        DataSetMetadata metadata = (DataSetMetadata) object;
        output.writeLong(metadata.getUuid().getMostSignificantBits());
        output.writeLong(metadata.getUuid().getLeastSignificantBits());
        output.writeInt(metadata.getRecordsOffset());
        output.writeInt(metadata.getRecordsCount());
        PrimitiveMapIO.write(output, metadata.getProperties());
    }
}
