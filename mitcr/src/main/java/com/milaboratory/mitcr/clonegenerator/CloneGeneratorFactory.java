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

import com.milaboratory.mitcr.qualitystrategy.DummyQualityInterpretationStrategy;
import com.milaboratory.mitcr.qualitystrategy.QualityInterpretationStrategy;

import java.util.concurrent.ExecutorService;

/**
 * A class used to create {@link CloneGenerator}s from a {@link CloneGeneratorParameters}
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
// TODO: comment
public class CloneGeneratorFactory {
    public static CloneGenerator create(CloneGeneratorParameters parameters, QualityInterpretationStrategy qualityInterpretationStrategy,
                                        boolean saveBackwardLinks) {
        return create(parameters, qualityInterpretationStrategy, saveBackwardLinks, null, null);
    }

    public static CloneGenerator create(CloneGeneratorParameters parameters, QualityInterpretationStrategy qualityInterpretationStrategy,
                                        boolean saveBackwardLinks, ExecutorService executorService, CloneGeneratorListener listener) {
        if (qualityInterpretationStrategy instanceof DummyQualityInterpretationStrategy)
            return new BasicCloneGenerator(AccumulatorType.getFactory(((BasicCloneGeneratorParameters) parameters).getAccumulatorType()),
                    ((BasicCloneGeneratorParameters) parameters).getSegmentInformationAggregationFactor(), saveBackwardLinks,
                    listener);

        if (parameters.getClass() == BasicCloneGeneratorParameters.class)
            return new BasicCloneGenerator(AccumulatorType.getFactory(((BasicCloneGeneratorParameters) parameters).getAccumulatorType()),
                    ((BasicCloneGeneratorParameters) parameters).getSegmentInformationAggregationFactor(), saveBackwardLinks,
                    listener);

        if (parameters.getClass() == LQFilteringOffCloneGeneratorParameters.class) {
            if (qualityInterpretationStrategy == null)
                throw new NullPointerException("Quality interpretation strategy is needed for LQMappingCloneGenerator.");
            return new LQFilteringOffCloneGenerator(AccumulatorType.getFactory(((LQFilteringOffCloneGeneratorParameters) parameters).getAccumulatorType()),
                    ((LQFilteringOffCloneGeneratorParameters) parameters).getSegmentInformationAggregationFactor(),
                    saveBackwardLinks, qualityInterpretationStrategy, listener);
        }
        if (parameters.getClass() == LQMappingCloneGeneratorParameters.class) {
            if (qualityInterpretationStrategy == null)
                throw new NullPointerException("Quality interpretation strategy is needed for LQMappingCloneGenerator.");
            return new LQMappingCloneGenerator(AccumulatorType.getFactory(((LQMappingCloneGeneratorParameters) parameters).getAccumulatorType()),
                    ((LQMappingCloneGeneratorParameters) parameters).getSegmentInformationAggregationFactor(), saveBackwardLinks,
                    qualityInterpretationStrategy, ((LQMappingCloneGeneratorParameters) parameters).getMaxErrorsInBadPoints(),
                    ((LQMappingCloneGeneratorParameters) parameters).isProportionalMapping(), executorService, listener);
        }
        return null;
    }
}
