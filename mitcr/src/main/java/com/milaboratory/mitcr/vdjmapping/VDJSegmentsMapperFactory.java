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
package com.milaboratory.mitcr.vdjmapping;

import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.SegmentGroupType;
import com.milaboratory.core.segment.SegmentLibrary;
import com.milaboratory.core.segment.Species;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractorParameters;
import com.milaboratory.mitcr.pipeline.AnalysisListener;
import com.milaboratory.mitcr.qualitystrategy.QualityInterpretationStrategy;
import com.milaboratory.mitcr.vdjmapping.ntree.NTreeNodeGenerator;

/**
 * A class to create {@link VDJSegmentsMapper}s from parameters
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class VDJSegmentsMapperFactory {
    private SegmentLibrary library;
    private NTreeNodeGenerator generator;
    private CDR3ExtractorParameters parameters;
    //private AtomicInteger vMapped = new AtomicInteger();
    //private AtomicInteger jMapped = new AtomicInteger();

    /**
     * Creates a factory for {@link VDJSegmentsMapper}s
     *
     * @param library    parent segment library
     * @param generator  generator, specifies tree algorithm to be used in segment alignment
     * @param parameters CDR3 extraction parameters
     */
    public VDJSegmentsMapperFactory(SegmentLibrary library, NTreeNodeGenerator generator, CDR3ExtractorParameters parameters) {
        this.library = library;
        this.generator = generator;
        this.parameters = parameters;
    }

    /*public VDJSegmentsMapper create() {
        VDJSegmentsMapper mapper = createVDJMapper(library, generator, parameters);
        //mapper.getVMapper().assignCounter(vMapped);
        //mapper.getJMapper().assignCounter(jMapped);
        return mapper;
    }*/

    public static VDJSegmentsMapper createVDJMapper(Species species, Gene gene,
                                                    CDR3ExtractorParameters parameters,
                                                    SegmentLibrary library,
                                                    QualityInterpretationStrategy qStrategy) {
        return createVDJMapper(species, gene, parameters, library, qStrategy, null);
    }

    public static VDJSegmentsMapper createVDJMapper(Species species, Gene gene,
                                                    CDR3ExtractorParameters parameters,
                                                    SegmentLibrary library,
                                                    QualityInterpretationStrategy qStrategy,
                                                    AnalysisListener listener) {
        return new VDJSegmentsMapper(
                VJSegmentMapperFactory.createMapperForNucleotideSQPair(
                        library.getGroup(species, gene, SegmentGroupType.Variable), parameters.getVMapperParameters(),
                        qStrategy, listener == null ? null : listener.getVListener()),
                VJSegmentMapperFactory.createMapperForNucleotideSQPair(
                        library.getGroup(species, gene, SegmentGroupType.Joining), parameters.getJMapperParameters(),
                        qStrategy, listener == null ? null : listener.getJListener()),
                DSegmentMapperFactory.createForNucleotideSQPair(
                        library.getGroup(species, gene, SegmentGroupType.Diversity), parameters.getDMapperParameters())
        );
    }
}
