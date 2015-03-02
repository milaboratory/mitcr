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

import com.milaboratory.core.segment.*;
import com.milaboratory.mitcr.clsexport.io.AbstractBinaryContainerIO;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class SegmentGroupContainerIO extends AbstractBinaryContainerIO {
    public static final int TYPE_ID = 0xCED; // == SegmentGroupContainerIOOld.TYPE_ID + 1
    public static final SegmentGroupContainerIO INSTANCE = new SegmentGroupContainerIO();

    private SegmentGroupContainerIO() {
    }

    @Override
    protected Class getEntityClass() {
        return SegmentGroupContainer.class;
    }

    @Override
    public Object read(DataInput input) throws IOException {
        byte[] md5 = new byte[16];
        input.readFully(md5);
        int groupIndex = input.readInt();
        int speciesIndex = input.readInt(); //Added on 8.02.2012 (new format)
        int count = input.readInt();
        Segment[] segments = new Segment[count];
        for (int i = 0; i < count; i++)
            segments[i] = readSegment(input);
        return new SegmentGroupContainer(Species.getSpeciesByIndex(speciesIndex),
                SegmentGroup.getSegmentGroupByIndex(groupIndex),
                Arrays.asList(segments), md5);
    }

    @Override
    public int typeId() {
        return TYPE_ID;
    }

    @Override
    public void write(DataOutput output, Object object) throws IOException {
        SegmentGroupContainer container = (SegmentGroupContainer) object;
        output.write(container.getMD5());
        writeWithoutMD5(output, container);
    }

    public void writeWithoutMD5(DataOutput output, SegmentGroupContainer object) throws IOException {
        output.writeInt(object.getGroup().getIndex());
        output.writeInt(object.getSpecies().index); //Added on 8.02.2012 (new format)
        int count = 0;
        for (Segment segment : object.getSegmentsList())
            count++;
        output.writeInt(count);
        for (Segment segment : object.getSegmentsList())
            writeSegment(output, segment);
    }


    private void writeSegment(DataOutput output, Segment object) throws IOException {
        output.writeUTF(object.getSegmentName());
        output.writeInt(object.getIndex());
        output.writeInt(object.getAlleles().size());
        for (Allele a : object.getAlleles())
            writeAllele(output, a);
    }

    private Segment readSegment(DataInput input) throws IOException {
        String name = input.readUTF();
        int index = input.readInt();
        int count = input.readInt();
        Allele[] alleles = new Allele[count];
        for (int i = 0; i < count; ++i)
            alleles[i] = readAllele(input);
        return new Segment(name, index, Arrays.asList(alleles));
    }

    private void writeAllele(DataOutput output, Allele object) throws IOException {
        output.writeUTF(object.getAccessionNumber());
        output.writeUTF(object.getFullName());
        output.writeUTF(object.getFunctionality());
        output.writeInt(object.getReferencePointPosition());
        output.writeInt(object.getIndex());
        output.writeInt(object.getNumber());
        NucleotideSequenceIO.INSTANCE.write(output, object.getSequence());
    }

    private Allele readAllele(DataInput input) throws IOException {
        String accessionNumber = input.readUTF();
        String fullName = input.readUTF();
        String functionality = input.readUTF();
        int refPoint = input.readInt();
        int index = input.readInt();
        int number = input.readInt();
        NucleotideSequence sequence = (NucleotideSequence) NucleotideSequenceIO.INSTANCE.read(input);
        return new Allele(fullName, number, index, functionality, accessionNumber, refPoint, sequence);
    }
}
