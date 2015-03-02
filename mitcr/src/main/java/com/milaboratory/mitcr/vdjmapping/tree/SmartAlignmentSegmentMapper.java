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

package com.milaboratory.mitcr.vdjmapping.tree;

import com.milaboratory.core.segment.SegmentGroupContainer;
import com.milaboratory.core.segment.SegmentGroupType;
import com.milaboratory.mitcr.qualitystrategy.GoodBadNucleotideSequence;
import com.milaboratory.mitcr.vdjmapping.AlignmentDirection;
import com.milaboratory.mitcr.vdjmapping.SearchDirection;
import com.milaboratory.mitcr.vdjmapping.VJSegmentMappingResult;
import com.milaboratory.mitcr.vdjmapping.ntree.NTreeNodeGenerator;
import com.milaboratory.mitcr.vdjmapping.trivial.OneSideAligner;
import com.milaboratory.util.BitArray;

import java.util.concurrent.atomic.AtomicInteger;

public final class SmartAlignmentSegmentMapper implements CoreVJSegmentMapper {
    private final OneDirectionSegmentMapper innerMapper;
    private final OneSideAligner oneSideAligner;
    private final int minLength;
    private AtomicInteger counter = null;

    public SmartAlignmentSegmentMapper(NTreeNodeGenerator generator, SegmentGroupContainer group, int mandatoryFrom, int mandatoryTo, int minLength, int lengthTolerance, SearchDirection searchDirection) {
        this.minLength = minLength;
        int offset;
        if (group.getGroup().getType() == SegmentGroupType.Joining)
            innerMapper = new OneDirectionSegmentMapper(generator, group, offset = mandatoryTo, AlignmentDirection.InsideCDR3,
                    mandatoryTo - mandatoryFrom + 1, Math.abs(lengthTolerance), searchDirection);
        else if (group.getGroup().getType() == SegmentGroupType.Variable)
            innerMapper = new OneDirectionSegmentMapper(generator, group, offset = mandatoryFrom, AlignmentDirection.InsideCDR3,
                    mandatoryTo - mandatoryFrom + 1, Math.abs(lengthTolerance), searchDirection);
        else
            throw new RuntimeException();
        offset -= group.getGroup().getType().cdr3Site();
        oneSideAligner = null; //new OneSideAligner(group, -group.getGroup().getType().cdr3Site(), offset, 0);
    }

    @Override
    public VJSegmentMappingResult map(GoodBadNucleotideSequence sequence) {
        VJSegmentMappingResult result = innerMapper.map(sequence);
        if (result == null)
            return null;
        BitArray oneSideBa = null; //oneSideAligner.build(sequence, result.getRefPoint());
        if (result.getScore() + oneSideAligner.getLastScore() < minLength)
            return null;

        if (!result.getBarcode().intersects(oneSideBa))
            oneSideBa = null; // oneSideAligner.build(result.getBarcode(), sequence, result.getRefPoint());

        if (result.getScore() + oneSideAligner.getLastScore() < minLength)
            return null;

        result.getBarcode().and(oneSideBa);
        return result;
    }

    /*@Override public void assignCounter(AtomicInteger counter) {
    this.counter = counter;
    }*/

    /*@Override
    public SegmentGroupContainer getContainer() {
        return innerMapper.getContainer();
    }*/
}