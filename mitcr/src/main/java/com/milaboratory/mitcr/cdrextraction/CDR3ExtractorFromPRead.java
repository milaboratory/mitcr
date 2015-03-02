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

import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.SegmentLibrary;
import com.milaboratory.core.segment.Species;
import com.milaboratory.core.sequencing.read.PSequencingRead;
import com.milaboratory.mitcr.pipeline.AnalysisListener;
import com.milaboratory.mitcr.qualitystrategy.QualityInterpretationStrategy;

public final class CDR3ExtractorFromPRead extends CDR3Extractor<PSequencingRead> {
    private final TargetRead targetRead;

    /**
     * Creates a CDR3 extractor for a {@link com.milaboratory.core.sequencing.read.SSequencingRead}
     *
     * @param species        species type (for segment choice)
     * @param gene           gene type (for segment choice)
     * @param parameters     parameters
     * @param segmentLibrary library of segments
     * @param qStrategy      quality interpretation strategy
     */
    public CDR3ExtractorFromPRead(Species species, Gene gene, CDR3ExtractorParameters parameters,
                                  SegmentLibrary segmentLibrary, QualityInterpretationStrategy qStrategy,
                                  TargetRead read) {
        this(species, gene, parameters, segmentLibrary, qStrategy, read,
                null);
    }

    /**
     * Creates a CDR3 extractor for a {@link com.milaboratory.core.sequencing.read.SSequencingRead}
     *
     * @param species        species type (for segment choice)
     * @param gene           gene type (for segment choice)
     * @param parameters     parameters
     * @param segmentLibrary library of segments
     * @param qStrategy      quality interpretation strategy
     */
    public CDR3ExtractorFromPRead(Species species, Gene gene, CDR3ExtractorParameters parameters,
                                  SegmentLibrary segmentLibrary, QualityInterpretationStrategy qStrategy,
                                  TargetRead read, AnalysisListener listener) {
        super(species, gene, parameters, segmentLibrary, qStrategy,
                listener);
        this.targetRead = read;
    }

    @Override
    public CDR3ExtractionResult<PSequencingRead> process(PSequencingRead input) {
        switch (targetRead) {
            case Read1:
                return _process(input.getData(0), input, (byte) 0);
            case Read2:
                return _process(input.getData(1), input, (byte) 1);
            case Both:
                CDR3ExtractionResult<PSequencingRead> result0 = _process(input.getData(0), input, (byte) 0),
                        result1 = _process(input.getData(1), input, (byte) 1);
                if (result0.getAlignmentsScore() > result1.getAlignmentsScore())
                    return result0;
                else
                    return result1;

        }
        return null;
    }
}
