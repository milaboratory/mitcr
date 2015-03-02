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
import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequence.quality.SequenceQualityPhred;

/**
 * Trims bad quality nucleotides from {@link NucleotideSQPair}s
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class NucleotideSQPairTrimmer implements Processor<NucleotideSQPair, NucleotideSQPair> {
    private byte thresholdValue;

    public NucleotideSQPairTrimmer(byte thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    @Override
    public NucleotideSQPair process(NucleotideSQPair input) {
        SequenceQualityPhred quality = input.getQuality();
        int trimTo = quality.size() - 1;
        for (int i = quality.size() - 1; i >= 0; --i)
            if (quality.value(i) <= thresholdValue)
                break;
            else
                trimTo = i;
        return input.getRange(0, trimTo);
    }
}
