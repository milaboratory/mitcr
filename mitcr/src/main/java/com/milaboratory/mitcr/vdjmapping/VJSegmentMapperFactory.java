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

import com.milaboratory.core.segment.SegmentGroupContainer;
import com.milaboratory.core.segment.SegmentGroupType;
import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequencing.read.SSequencingRead;
import com.milaboratory.mitcr.qualitystrategy.QualityInterpretationStrategy;
import com.milaboratory.mitcr.vdjmapping.ntree.NTreeNodeGenerator;
import com.milaboratory.mitcr.vdjmapping.tree.CoreVJSegmentMapper;
import com.milaboratory.mitcr.vdjmapping.trivial.TrivialSegmentMapper;

/**
 * A factory object that creates V and J segment mappers
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class VJSegmentMapperFactory {
    /**
     * Used internally
     */
    private static CoreVJSegmentMapper createCoreMapper(SegmentGroupContainer group, NTreeNodeGenerator generator,
                                                        VJSegmentMapperParameters parameters,
                                                        VJMapperListener listener) {
        switch (parameters.getAlignmentDirection()) {
            case Both:
            case InsideCDR3:
            case OutsideCDR3:
                return new TrivialSegmentMapper(group, parameters.getSeedFrom(), parameters.getSeedTo(), parameters.getMinAlignmentMatches(),
                        parameters.getLengthTolerance(), parameters.getAlignmentDirection(), listener);
            default:
                throw new IllegalArgumentException(parameters.getAlignmentDirection() + " is not supported.");
        }
        /*if (parameters.getAlignmentDirection() == AlignmentDirection.Both)
            return new UniDirectionSegmentMapper(generator, group, parameters.getSeedFrom(),
                    parameters.getSeedTo(),
                    parameters.getMinAlignmentMatches(), parameters.getLengthTolerance(),
                    selectSearchDirection(group.getGroup().getType()));
        if (parameters.getAlignmentDirection() == AlignmentDirection.TrivialAlignment)
            return new TrivialSegmentMapper(group, parameters.getSeedFrom(),
                    parameters.getSeedTo(), parameters.getMinAlignmentMatches(),
                    parameters.getLengthTolerance(), listener);
        if (parameters.getAlignmentDirection() == AlignmentDirection.Smart)
            return new SmartAlignmentSegmentMapper(generator, group, parameters.getSeedFrom(),
                    parameters.getSeedTo(),
                    parameters.getMinAlignmentMatches(), parameters.getLengthTolerance(),
                    selectSearchDirection(group.getGroup().getType()));
        if (parameters.getAlignmentDirection() == AlignmentDirection.SmartV2)
            return new SmartAlignmentSegmentMapperV2(generator, group, parameters.getSeedFrom(),
                    parameters.getSeedTo(),
                    parameters.getMinAlignmentMatches(), parameters.getLengthTolerance(),
                    selectSearchDirection(group.getGroup().getType()));
        return new OneDirectionSegmentMapper(generator, group, parameters.getSeedFrom(),
                parameters.getAlignmentDirection(),
                parameters.getMinAlignmentMatches(), parameters.getLengthTolerance(),
                selectSearchDirection(group.getGroup().getType()));*/
    }

    private static SearchDirection selectSearchDirection(SegmentGroupType type) {
        switch (type) {
            case Variable:
                return SearchDirection.BeginToEnd;
            case Joining:
                return SearchDirection.EndToBegin;
            default:
                throw new RuntimeException("not supported");
        }
    }

    /**
     * Creates a {@link VJSegmentMapper} for {@link SSequencingRead}s
     *
     * @param group      container of segments that will be mapped
     * @param parameters mapping parameters
     * @param strategy   sequence quality interpretation strategy
     * @return a {@link VJSegmentMapper} for {@link SSequencingRead}s with defined parameters
     */
    public static VJSegmentMapper<SSequencingRead> createMapperForSReads(SegmentGroupContainer group, VJSegmentMapperParameters parameters,
                                                                         QualityInterpretationStrategy strategy) {
        return createMapperForSReads(group, parameters, strategy, null);
    }


    /**
     * Creates a {@link VJSegmentMapper} for {@link SSequencingRead}s
     *
     * @param group      container of segments that will be mapped
     * @param parameters mapping parameters
     * @param strategy   sequence quality interpretation strategy
     * @return a {@link VJSegmentMapper} for {@link SSequencingRead}s with defined parameters
     */
    public static VJSegmentMapper<SSequencingRead> createMapperForSReads(SegmentGroupContainer group, VJSegmentMapperParameters parameters,
                                                                         QualityInterpretationStrategy strategy, VJMapperListener listener) {
        return new VJSegmentsMapperAdapter<SSequencingRead>(strategy.getProviderForSRead(),
                createCoreMapper(group, strategy.getGenerator(), parameters, listener));
    }

    /**
     * Creates a {@link VJSegmentMapper} for {@link NucleotideSQPair}s
     *
     * @param group      container of segments that will be mapped
     * @param parameters mapping parameters
     * @param strategy   sequence quality interpretation strategy
     * @return a {@link VJSegmentMapper} for {@link NucleotideSQPair}s with defined parameters
     */
    public static VJSegmentMapper<NucleotideSQPair> createMapperForNucleotideSQPair(SegmentGroupContainer group, VJSegmentMapperParameters parameters,
                                                                                    QualityInterpretationStrategy strategy, VJMapperListener listener) {
        return new VJSegmentsMapperAdapter<NucleotideSQPair>(strategy.getProviderForNucleotideSQPair(),
                createCoreMapper(group, strategy.getGenerator(), parameters, listener));
    }
}
