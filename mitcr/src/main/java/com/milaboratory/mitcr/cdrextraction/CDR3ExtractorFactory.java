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

import cc.redberry.pipe.ProcessorFactory;
import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.SegmentLibrary;
import com.milaboratory.core.segment.Species;
import com.milaboratory.mitcr.pipeline.AnalysisListener;
import com.milaboratory.mitcr.qualitystrategy.QualityInterpretationStrategy;

/**
 * Non-public class.
 *
 * @param <I> input type
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)*
 */
abstract class CDR3ExtractorFactory<I> implements ProcessorFactory<I, CDR3ExtractionResult<I>> {
    public final Species species;
    public final Gene gene;
    public final CDR3ExtractorParameters parameters;
    public final SegmentLibrary segmentLibrary;
    public final QualityInterpretationStrategy qStrategy;
    public final AnalysisListener listener;

    public CDR3ExtractorFactory(Species species, Gene gene, CDR3ExtractorParameters parameters,
                                SegmentLibrary segmentLibrary, QualityInterpretationStrategy qStrategy,
                                AnalysisListener listener) {
        this.species = species;
        this.gene = gene;
        this.parameters = parameters;
        this.segmentLibrary = segmentLibrary;
        this.qStrategy = qStrategy;
        this.listener = listener;
    }
}
