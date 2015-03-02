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
package com.milaboratory.core.sequencing.io.fastq;

import cc.redberry.pipe.Processor;
import com.milaboratory.core.sequence.quality.QualityFormat;
import com.milaboratory.core.sequencing.read.PSequencingRead;
import com.milaboratory.core.sequencing.read.PSequencingReadImpl;
import com.milaboratory.core.sequencing.read.SSequencingRead;

/**
 * Trims bad quality nucleotides from both ends of {@link PSequencingRead}s
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class PSequencingReadsTrimmer implements Processor<PSequencingRead, PSequencingRead> {
    private final NucleotideSQPairTrimmer trimmer;

    /**
     * Creates bad quality trimmer
     *
     * @param format sequencing quality format
     */
    public PSequencingReadsTrimmer(QualityFormat format) {
        this.trimmer = new NucleotideSQPairTrimmer((byte) 2);
    }

    @Override
    public PSequencingRead process(PSequencingRead input) {
        SSequencingRead read0 = input.getSingleRead(0);
        SSequencingRead read1 = input.getSingleRead(1);
        return new PSequencingReadImpl(input.id(),
                read0.getDescription(), read1.getDescription(),
                trimmer.process(read0.getData()), trimmer.process(read1.getData()));
    }
}
