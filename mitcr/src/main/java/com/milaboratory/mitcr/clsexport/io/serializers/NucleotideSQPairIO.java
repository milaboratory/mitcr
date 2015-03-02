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

import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.core.sequence.quality.SequenceQualityPhred;
import com.milaboratory.mitcr.clsexport.io.BinaryContainerReader;
import com.milaboratory.mitcr.clsexport.io.BinaryContainerWriter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * IO for {@link NucleotideSQPair}.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class NucleotideSQPairIO implements BinaryContainerReader, BinaryContainerWriter {
    public static final int TYPE_ID = 0xBEFCF;
    public static final NucleotideSQPairIO INSTANCE = new NucleotideSQPairIO();

    private NucleotideSQPairIO() {
    }

    @Override
    public boolean canRead(int typeId) {
        return typeId == TYPE_ID;
    }

    @Override
    public Object read(DataInput input) throws IOException {
        NucleotideSequence sequence = (NucleotideSequence) NucleotideSequenceIO.INSTANCE.read(input);
        SequenceQualityPhred quality = (SequenceQualityPhred) SequenceQualityIO.INSTANCE.read(input);
        return new NucleotideSQPair(sequence, quality);
    }

    @Override
    public boolean canWrite(Object object) {
        return object.getClass() == NucleotideSQPair.class;
    }

    @Override
    public int typeId() {
        return TYPE_ID;
    }

    @Override
    public void write(DataOutput output, Object object) throws IOException {
        NucleotideSQPair pair = (NucleotideSQPair) object;
        NucleotideSequenceIO.INSTANCE.write(output, pair.getSequence());
        SequenceQualityIO.INSTANCE.write(output, pair.getQuality());
    }
}
