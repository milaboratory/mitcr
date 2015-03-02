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
package com.milaboratory.mitcr.clonegenerator;

import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult;
import com.milaboratory.mitcr.qualitystrategy.GBNSUtils;
import com.milaboratory.mitcr.qualitystrategy.GoodBadNucleotideSequence;
import com.milaboratory.mitcr.qualitystrategy.GoodBadNucleotideSequenceProvider;
import com.milaboratory.mitcr.qualitystrategy.QualityInterpretationStrategy;

public class LQFilteringOffCloneGenerator extends BasicCloneGenerator {
    private final QualityInterpretationStrategy strategy;
    //For fast access
    private final GoodBadNucleotideSequenceProvider<NucleotideSQPair> goodBadNucleotideSequenceProvider;

    public LQFilteringOffCloneGenerator(AccumulatorCloneFactory cloneFactory, float barcodeAggregationFactor,
                                        boolean saveBackwardLinks, QualityInterpretationStrategy strategy) {
        this(cloneFactory, barcodeAggregationFactor, saveBackwardLinks, strategy, null);
    }

    public LQFilteringOffCloneGenerator(AccumulatorCloneFactory cloneFactory, float barcodeAggregationFactor,
                                        boolean saveBackwardLinks, QualityInterpretationStrategy strategy,
                                        CloneGeneratorListener listener) {
        super(cloneFactory, barcodeAggregationFactor, saveBackwardLinks, listener);
        this.strategy = strategy;
        this.goodBadNucleotideSequenceProvider = strategy.getProviderForNucleotideSQPair();
    }

    @Override
    public void put(CDR3ExtractionResult cdr3ExtractionResult) {
        if (cdr3ExtractionResult == null) {
            //Clone generation is finished
            super.put(null);
            return;
        }

        if (cdr3ExtractionResult.getCDR3() == null) {
            //For statistics aggregation
            super.put(cdr3ExtractionResult);
            return;
        }

        //Wrapping sequence to G/B nucleotide sequence
        GoodBadNucleotideSequence sequence = goodBadNucleotideSequenceProvider.process(cdr3ExtractionResult.getCDR3());

        //Looking for bad points
        if (!GBNSUtils.hasBadNucleotides(sequence)) {
            //To the good
            super.put(cdr3ExtractionResult);
        } else if (listener != null)
            listener.cdr3Dropped(cdr3ExtractionResult);
    }
}
