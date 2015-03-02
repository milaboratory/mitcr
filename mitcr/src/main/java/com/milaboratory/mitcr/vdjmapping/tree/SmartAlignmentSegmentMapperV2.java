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
import com.milaboratory.mitcr.vdjmapping.SearchDirection;
import com.milaboratory.mitcr.vdjmapping.VJSegmentMappingResult;
import com.milaboratory.mitcr.vdjmapping.ntree.NTreeNodeGenerator;
import com.milaboratory.mitcr.vdjmapping.trivial.OneSideAligner;
import com.milaboratory.util.BitArray;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public final class SmartAlignmentSegmentMapperV2 implements CoreVJSegmentMapper {
    private final UniDirectionSegmentMapper innerMapper;
    private final OneSideAligner oneSideAlignerInside, oneSideAlignerOutside;
    private final int minLength;
    private final int[] cache;
    private AtomicInteger counter = null;

    public SmartAlignmentSegmentMapperV2(NTreeNodeGenerator generator, SegmentGroupContainer group, int mandatoryFrom, int mandatoryTo, int minLength, int lengthTolerance, SearchDirection searchDirection) {
        this.cache = new int[group.getAllelesCount()];
        this.minLength = minLength;
        innerMapper = new UniDirectionSegmentMapper(generator, group, mandatoryFrom, mandatoryTo, minLength, lengthTolerance, searchDirection);
        int offset;
        if (group.getGroup().getType() == SegmentGroupType.Joining)
            offset = mandatoryTo;
        else if (group.getGroup().getType() == SegmentGroupType.Variable)
            offset = mandatoryFrom;
        else
            throw new RuntimeException();
        offset -= group.getGroup().getType().cdr3Site();
        oneSideAlignerOutside = null; //new OneSideAligner(group, -group.getGroup().getType().cdr3Site(), offset, 2);
        oneSideAlignerInside = null; //new OneSideAligner(group, group.getGroup().getType().cdr3Site(), offset, 0);
    }

    @Override
    public VJSegmentMappingResult map(GoodBadNucleotideSequence sequence) {
        VJSegmentMappingResult result = innerMapper.map(sequence);
        if (result == null)
            return null;
        int[] scores = cache;
        Arrays.fill(scores, OneSideAligner.INITIAL_SCORE);
        //oneSideAlignerOutside.buildScores(scores, sequence, result.getRefPoint());
        BitArray barcode = null; //oneSideAlignerInside.build(scores, sequence, result.getRefPoint());
        result.getBarcode().loadValueFrom(barcode);
        return result;
    }

    /*@Override
    public void assignCounter(AtomicInteger counter) {
        this.counter = counter;
    }*/

    /*@Override
    public SegmentGroupContainer getContainer() {
        return innerMapper.getContainer();
    }*/
}
