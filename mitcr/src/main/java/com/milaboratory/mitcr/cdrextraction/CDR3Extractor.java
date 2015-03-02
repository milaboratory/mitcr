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
import com.milaboratory.mitcr.vdjmapping.VDJSegmentsMapper;
import com.milaboratory.mitcr.vdjmapping.VDJSegmentsMapperFactory;
import com.milaboratory.mitcr.vdjmapping.VDJSegmentsMappingResult;
import com.milaboratory.mitcr.vdjmapping.VJSegmentMappingResult;

/**
 * Non-public class.
 *
 * @param <I> input type
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
abstract class CDR3Extractor<I> implements Processor<I, CDR3ExtractionResult<I>> {
    private final VDJSegmentsMapper vdjMapper;
    private final Strand strand;
    private final CoreCDR3Extractor extractor;
    private final CDR3ExtractorListener listener;

    public CDR3Extractor(Species species, Gene gene,
                         CDR3ExtractorParameters parameters,
                         SegmentLibrary segmentLibrary,
                         QualityInterpretationStrategy qStrategy,
                         AnalysisListener listener) {
        this.strand = parameters.getStrand();
        this.extractor = new CoreCDR3Extractor(parameters.getLowerCDR3LengthThreshold(), parameters.getUpperCDR3LengthThreshold(), parameters.getIncludeCysPhe());
        this.vdjMapper = VDJSegmentsMapperFactory.createVDJMapper(species, gene, parameters, segmentLibrary, qStrategy, listener);
        this.listener = listener == null ? null : listener.getCDR3ExtractorListener();
    }

    protected CDR3ExtractionResult<I> _process(final NucleotideSQPair data, I input) {
        return _process(data, input, (byte) 0);
    }

    protected CDR3ExtractionResult<I> _process(final NucleotideSQPair data, I input, byte readIndex) {


        if (data.size() < 10) {
            CDR3ExtractionResult result = new CDR3ExtractionResult<I>(input, new VJSegmentMappingResult[0], null, null, false, readIndex);
            if (listener != null)
                listener.cdr3NotExtracted(result, input);
            return result;
        }

        NucleotideSQPair rcData = null;
        final VDJSegmentsMappingResult[] mappingResults = new VDJSegmentsMappingResult[2];
        if (strand.isForward())
            mappingResults[0] = vdjMapper.map(data);
        if (strand.isReverse())
            mappingResults[1] = vdjMapper.map(rcData = data.getRC());

        VDJSegmentsMappingResult bestResult = mappingResults[0];
        if (bestResult == null || (mappingResults[1] != null && mappingResults[1].score() > bestResult.score()))
            bestResult = mappingResults[1];

        boolean isRC = (bestResult == mappingResults[1]);
        NucleotideSQPair cdr = null;
        if (bestResult.isGood())
            cdr = extractor.extract(bestResult, isRC ? rcData : data);

        final CDR3ExtractionResult result = new CDR3ExtractionResult<I>(input, bestResult.getVJResultsArray(), bestResult.getDResult(), cdr, isRC, readIndex);

        if (listener != null)
            if (cdr == null)
                listener.cdr3NotExtracted(result, input);
            else
                listener.cdr3Extracted(result, input);

        return result;
    }
}
