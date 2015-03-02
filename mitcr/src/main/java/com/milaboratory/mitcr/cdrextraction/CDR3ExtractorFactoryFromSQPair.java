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
package com.milaboratory.mitcr.cdrextraction;

import cc.redberry.pipe.Processor;
import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.SegmentLibrary;
import com.milaboratory.core.segment.Species;
import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.mitcr.pipeline.AnalysisListener;
import com.milaboratory.mitcr.qualitystrategy.QualityInterpretationStrategy;

/**
 * A factory to be used in {@link cc.redberry.pipe.blocks.ParallelProcessor}. See {@link CDR3ExtractorFromSQPair} for
 * details
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class CDR3ExtractorFactoryFromSQPair extends CDR3ExtractorFactory<NucleotideSQPair> {
    /**
     * Creates a CDR3 extractor factory for a {@link NucleotideSQPair}
     *
     * @param species        species type (for segment choice)
     * @param gene           gene type (for segment choice)
     * @param parameters     parameters
     * @param segmentLibrary library of segments
     * @param qStrategy      quality interpretation strategy
     */
    public CDR3ExtractorFactoryFromSQPair(Species species, Gene gene, CDR3ExtractorParameters parameters,
                                          SegmentLibrary segmentLibrary, QualityInterpretationStrategy qStrategy) {
        super(species, gene, parameters, segmentLibrary, qStrategy, null);
    }


    /**
     * Creates a CDR3 extractor factory for a {@link NucleotideSQPair}
     *
     * @param species        species type (for segment choice)
     * @param gene           gene type (for segment choice)
     * @param parameters     parameters
     * @param segmentLibrary library of segments
     * @param qStrategy      quality interpretation strategy
     */
    public CDR3ExtractorFactoryFromSQPair(Species species, Gene gene, CDR3ExtractorParameters parameters,
                                          SegmentLibrary segmentLibrary, QualityInterpretationStrategy qStrategy,
                                          AnalysisListener listener) {
        super(species, gene, parameters, segmentLibrary, qStrategy, listener);
    }

    @Override
    public Processor<NucleotideSQPair, CDR3ExtractionResult<NucleotideSQPair>> create() {
        return new CDR3ExtractorFromSQPair(species, gene, parameters,
                segmentLibrary, qStrategy, listener);
    }
}
