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

import com.milaboratory.core.clone.Clone;
import com.milaboratory.mitcr.clsexport.io.BinaryContainerWriter;
import com.milaboratory.mitcr.clsexport.io.serializers.BitArrayIO;
import com.milaboratory.mitcr.clsexport.io.serializers.NucleotideSQPairIO;

import java.io.DataOutput;
import java.io.IOException;

public class CloneIO implements BinaryContainerWriter {
    public static final CloneIO INSTANCE = new CloneIO();
    /*public static final SCloneImplFactory DEFAULT_FACTORY = new SCloneImplFactory() {
        @Override
        public SCloneImpl create(NucleotideSQPair data, BitArray[] barcodes, int[] segmentCoords, ErrorFlags errorFlags, int ssLinkId, int count) {
            return new SCloneImpl(data, barcodes, segmentCoords, errorFlags, ssLinkId, count);
        }
    };
    public static final CloneIO S_INSTANCE = new CloneIO(DEFAULT_FACTORY);
    public static final CloneIO D_INSTANCE = new CloneIO(SCloneDImpl.FACTORY);*/
    /*private final SCloneImplFactory cloneFactory;

    private CloneIO(SCloneImplFactory cloneFactory) {
        this.cloneFactory = cloneFactory;
    }*/

    private CloneIO() {
    }

    /*@Override
    protected Class getEntityClass() {
        return CloneImpl.class;
    }*/

    @Override
    public boolean canWrite(Object object) {
        return object instanceof Clone;
    }

    /*@Override
    public Object read(DataInput input) throws IOException {
        int count = input.readInt();
        NucleotideSQPair pair = (NucleotideSQPair) NucleotideSQPairIO.INSTANCE.read(input);
        ErrorFlags ef = new ErrorFlags(input.readInt());
        int bcount = input.readByte();
        BitArray[] barcodes = new BitArray[bcount];
        for (int i = 0; i < bcount; ++i)
            barcodes[i] = BitArrayIO.read(input);
        int ccount = input.readByte();
        int[] coords = new int[ccount];
        for (int i = 0; i < ccount; ++i)
            coords[i] = input.readInt();
        int link = input.readInt();
        return cloneFactory.create(pair, barcodes, coords, ef, link, count);
    }*/

    @Override
    public int typeId() {
        return 0xC0BAB1;
    }

    @Override
    public void write(DataOutput output, Object object) throws IOException {
        Clone clone = (Clone) object;
        output.writeInt((int) clone.getCount());
        NucleotideSQPairIO.INSTANCE.write(output, clone.getCDR3());
        output.writeInt(0);
        //int size = clone.getParentCloneSet().getSegmentGroupContainer(SegmentGroupType.Diversity) == null ? 2 : 3;
        output.writeByte((byte) 3);
        //for (int i = 0; i < clone.allelesBarcodes.length; ++i)
        BitArrayIO.write(output, clone.getVAlleles().getBitArrayCopy());
        BitArrayIO.write(output, clone.getJAlleles().getBitArrayCopy());
        BitArrayIO.write(output, clone.getDAlleles() == null ? null : clone.getDAlleles().getBitArrayCopy());
        output.writeByte(clone.isDSegmentDetermined() ? (byte) 4 : (byte) 2);
        output.writeInt(clone.getVEnd());
        if (clone.isDSegmentDetermined()) {
            output.writeInt(clone.getDStart());
            output.writeInt(clone.getDEnd());
        }
        output.writeInt(clone.getJStart());
        output.writeInt(-1);
    }

    /*public interface SCloneImplFactory {
        SCloneImpl create(NucleotideSQPair data, BitArray[] barcodes, int[] segmentCoords, ErrorFlags errorFlags, int ssLinkId, int count);
    }*/
}
