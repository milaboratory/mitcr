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

import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.Species;
import com.milaboratory.mitcr.clsexport.io.AbstractBinaryContainerIO;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SCloneSetMetadataIO extends AbstractBinaryContainerIO {
    public static final SCloneSetMetadataIO INSTANCE = new SCloneSetMetadataIO();

    private SCloneSetMetadataIO() {
    }

    @Override
    protected Class getEntityClass() {
        return SCloneSetMetadata.class;
    }

    @Override
    public Object read(DataInput input) throws IOException {
        AbstractCloneSetMetadata aMetadata = AbstractCloneSetMetadataIO.read(input);
        Species species = Species.getSpeciesByIndex(input.readInt()); // on species supporting add (10.02.2012)
        Gene gene = Gene.get(input.readInt());
        int segments = input.readByte();
        byte[][] md5s = new byte[segments][16];
        for (int i = 0; i < segments; i++)
            input.readFully(md5s[i]);
        return new SCloneSetMetadata(aMetadata, species, gene, md5s, 1);
    }

    @Override
    public int typeId() {
        return 0xB3AAD; //+1 on species supporting add (10.02.2012)
    }

    @Override
    public void write(DataOutput output, Object object) throws IOException {
        SCloneSetMetadata metadata = (SCloneSetMetadata) object;
        AbstractCloneSetMetadataIO.write(output, metadata);
        output.writeInt(metadata.getSpecies().index); // on species supporting add (10.02.2012)
        output.writeInt(metadata.getGene().id());
        int segments = metadata.getSegmentGroupsMD5().length;
        output.writeByte((byte) segments);
        for (int i = 0; i < segments; ++i) {
            if (metadata.getSegmentGroupsMD5()[i].length != 16)
                throw new RuntimeException("Wrong MD5 code length");
            output.write(metadata.getSegmentGroupsMD5()[i]);
        }
    }
}
