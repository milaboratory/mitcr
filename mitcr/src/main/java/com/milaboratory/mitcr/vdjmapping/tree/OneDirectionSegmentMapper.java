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
import com.milaboratory.mitcr.qualitystrategy.GBNSNucleotideInfoProvider;
import com.milaboratory.mitcr.qualitystrategy.GoodBadNucleotideSequence;
import com.milaboratory.mitcr.vdjmapping.AlignmentDirection;
import com.milaboratory.mitcr.vdjmapping.SearchDirection;
import com.milaboratory.mitcr.vdjmapping.VJSegmentMappingResult;
import com.milaboratory.mitcr.vdjmapping.ntree.NTreeNodeGenerator;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public final class OneDirectionSegmentMapper implements CoreVJSegmentMapper {
    private final STree tree;
    private final STreeResultHolder result;
    //private final GBNSNucleotideInfoProvider infoProvider;
    private final int minLength, delta;
    private final int from, alignmentDirection;
    private final SearchDirection searchDirection;
    //private AtomicInteger counter = null;

    /**
     * Creates one direction segment mapper.
     *
     * @param generator       tells the tree-based algorithm how to treat bad points
     * @param group           container of segments
     * @param from            position of first nucleotide that shoud be aligned, relative to reference point (0 =
     *                        reference point)
     * @param direction       direction of alignment
     * @param minLength       minimal good alignment length
     * @param lengthTolerance maximal allowed difference from best alignment length for a segment to be included in
     *                        result
     * @param searchDirection 5' to 3' or 3' to 5'
     */
    public OneDirectionSegmentMapper(NTreeNodeGenerator generator, SegmentGroupContainer group,
                                     int from, AlignmentDirection direction, int minLength, int lengthTolerance, SearchDirection searchDirection) {
        this.minLength = minLength;
        this.delta = lengthTolerance;
        this.from = from;
        this.alignmentDirection = direction.getDirection(group.getGroup().getType());
        this.searchDirection = searchDirection;
        this.tree = new STree(generator, group, from, alignmentDirection);
        this.result = new STreeResultHolder(tree);
        //this.infoProvider = new GBNSNucleotideInfoProvider()
        //this.infoProvider = new SequenceWrapperNInfoProvider();
        //infoProvider.setDirection(alignmentDirection);
    }

    @Override
    public VJSegmentMappingResult map(GoodBadNucleotideSequence sequence) {
        int cFrom = 0;
        int cTo = sequence.size();
        if (alignmentDirection == 1)
            cTo -= minLength;
        else
            cFrom += minLength;
        VJSegmentMappingResult mappingResult = null;
        int maxScore = -1;
        //infoProvider.assignSequenceWrapper(sequence);
        final GBNSNucleotideInfoProvider infoProvider = new GBNSNucleotideInfoProvider(sequence, alignmentDirection);
        for (int position = cFrom; position < cTo; ++position) {
            infoProvider.resetPosition(position);
            tree.performSearch(infoProvider, result);
            if (result.maxScore >= minLength
                    && (maxScore == -1 || better(maxScore, result.maxScore))) {
                mappingResult = createResult(position);
                maxScore = result.maxScore;
            }
        }
        //if (counter != null && mappingResult != null)
        //    counter.incrementAndGet();
        return mappingResult;
    }

    private VJSegmentMappingResult createResult(int coord) {
        int sFrom = coord;
        int sTo = coord + alignmentDirection * (result.size - 1);
        int tmp;
        if (sFrom > sTo) {
            tmp = sFrom;
            sFrom = sTo;
            sTo = tmp;
        }
        return new VJSegmentMappingResult(result.barcodeEvolution[result.size - 1 - delta],
                tree.getGroup(), coord - from, sFrom, sTo, result.maxScore);
    }

    private boolean better(int max, int current) {
        if (searchDirection == SearchDirection.BeginToEnd)
            return max < current;
        return max <= current;
    }

    /*@Override
    public SegmentGroupContainer getContainer() {
        return tree.getContainer();
    }*/

    /**@Override public void assignCounter(AtomicInteger counter) {
    this.counter = counter;
    }*/
}
