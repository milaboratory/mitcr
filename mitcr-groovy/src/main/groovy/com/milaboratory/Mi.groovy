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
package com.milaboratory

import cc.redberry.pipe.CUtils
import cc.redberry.pipe.OutputPort
import com.milaboratory.core.segment.DefaultSegmentLibrary
import com.milaboratory.core.segment.Gene
import com.milaboratory.core.segment.Species
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence
import com.milaboratory.core.sequencing.read.SSequencingRead
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractorFromSRead
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractorParameters
import com.milaboratory.mitcr.pipeline.ParameterPresets
import com.milaboratory.mitcr.qualitystrategy.IlluminaQualityInterpretationStrategy

/**
 * Created with IntelliJ IDEA.
 * User: dbolotin
 * Date: 30.01.13
 * Time: 18:09
 * To change this template use File | Settings | File Templates.
 */
class Mi {
    static NucleotideSequence getN(String string) {
        return new NucleotideSequence(string);
    }

    static <T> void each(OutputPort<T> port, Closure<SSequencingRead> action) {
        T object;
        while ((object = port.take()) != null)
            action(object);
    }

    static OutputPort<CDR3ExtractionResult> cdr3(OutputPort<SSequencingRead> reads, Species species = Species.HomoSapiens,
                                                 Gene gene = Gene.TRB, CDR3ExtractorParameters params = null) {
        if (!params)
            params = ParameterPresets.flex.CDR3ExtractorParameters;

        return CUtils.wrap(reads, new CDR3ExtractorFromSRead(species, gene, params, DefaultSegmentLibrary.load(),
                new IlluminaQualityInterpretationStrategy((byte) 25)));
    }
}
