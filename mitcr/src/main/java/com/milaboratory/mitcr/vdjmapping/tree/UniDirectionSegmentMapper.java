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

import com.milaboratory.core.segment.Allele;
import com.milaboratory.core.segment.SegmentGroupContainer;
import com.milaboratory.core.sequence.motif.NucleotideMotif;
import com.milaboratory.core.sequence.motif.NucleotideMotifBuilder;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.mitcr.qualitystrategy.GBNSNucleotideInfoProvider;
import com.milaboratory.mitcr.qualitystrategy.GoodBadNucleotideSequence;
import com.milaboratory.mitcr.vdjmapping.SearchDirection;
import com.milaboratory.mitcr.vdjmapping.VJSegmentMappingResult;
import com.milaboratory.mitcr.vdjmapping.ntree.NTreeNodeGenerator;
import com.milaboratory.util.BitArray;

import static com.milaboratory.util.Math.sort;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public final class UniDirectionSegmentMapper implements CoreVJSegmentMapper {
    private final STree treeForward, treeReverse;
    private final STreeResultHolder resultForward, resultReverse;
    //private final SequenceWrapperNInfoProvider infoProvider;
    private final int minLength, lengthTolerance, mandatoryRegionLength;
    private final int leftDelta, rightDelta;
    private final SearchDirection searchDirection;
    private final int mandatoryFrom, mandatoryTo;
    private final int[] scores, permutation;
    private final NucleotideMotif motif;
    private final int reference;
    private final boolean improved = false;
    //private AtomicInteger counter = null;

    /**
     * Creates uni-direction segment mapper.
     *
     * @param generator       tells the tree-based algorithm how to treat bad points
     * @param group           container of segments
     * @param mandatoryFrom   position of first nucleotide of the region that has to be aligned, relative to reference
     *                        point (0 = reference point)
     * @param mandatoryTo     position of last nucleotide of the region that has to be aligned, relative to reference
     *                        point (0 = reference point)
     * @param minLength       minimal length of alignment
     * @param lengthTolerance maximal allowed difference from best alignment length for a segment to be included in
     *                        result
     * @param searchDirection 5' to 3' or 3' to 5'
     */
    public UniDirectionSegmentMapper(NTreeNodeGenerator generator, SegmentGroupContainer group, int mandatoryFrom,
                                     int mandatoryTo, int minLength, int lengthTolerance,
                                     SearchDirection searchDirection) {
        if (mandatoryFrom > mandatoryTo)
            throw new IllegalArgumentException();
        this.scores = new int[group.getAllelesCount()];
        this.permutation = new int[group.getAllelesCount()];
        this.minLength = minLength;
        this.lengthTolerance = lengthTolerance;
        this.searchDirection = searchDirection;
        this.mandatoryRegionLength = mandatoryTo - mandatoryFrom + 1;
        if (this.mandatoryRegionLength <= 3)
            throw new IllegalArgumentException("Too short mandatory region.");
        this.mandatoryFrom = mandatoryFrom;
        this.mandatoryTo = mandatoryTo;
        this.treeForward = new STree(generator, group, mandatoryFrom + 1, +1);
        this.treeReverse = new STree(generator, group, mandatoryFrom, -1);
        this.resultForward = new STreeResultHolder(treeForward);
        this.resultReverse = new STreeResultHolder(treeReverse);
        //this.infoProvider = new SequenceWrapperNInfoProvider();
        //this.leftDelta = lengthTolerance / 2;
        //this.rightDelta = lengthTolerance - this.leftDelta;
        if (lengthTolerance > 0) {
            this.rightDelta = lengthTolerance;
            this.leftDelta = 0;
        } else {
            this.leftDelta = -lengthTolerance;
            this.rightDelta = 0;
        }
        this.reference = -mandatoryFrom - 1;

        //Building motif
        final NucleotideMotifBuilder builder = new NucleotideMotifBuilder(mandatoryRegionLength);
        NucleotideSequence sequence;
        int position;
        for (Allele allele : group.getAllelesList()) {
            position = allele.getReferencePointPosition() + mandatoryFrom;
            sequence = allele.getSequence();
            for (int i = 0; i < mandatoryRegionLength; ++i)
                builder.set(i, sequence.codeAt(position++));
        }
        this.motif = builder.build();
    }

    @Override
    public VJSegmentMappingResult map(GoodBadNucleotideSequence sequence) {
        int cTo = sequence.size() - mandatoryRegionLength;
        VJSegmentMappingResult mappingResult = null;
        int maxScore = -1;
        int currentMinScore = minLength;
        int max, firstGoodIndex;
        //infoProvider.assignSequenceWrapper(sequence);
        final GBNSNucleotideInfoProvider forwardInfoProvider = new GBNSNucleotideInfoProvider(sequence, +1),
                reverseInfoProvider = new GBNSNucleotideInfoProvider(sequence, -1);
        BitArray barcode = new BitArray(treeForward.getGroup().getAllelesCount());

        int mm;
        OUTER:
        for (int position = 0; position < cTo; ++position) {
            //Early termination
            mm = 0;
            for (int i = 0; i < motif.size(); ++i)
                if (!motif.get(i, sequence.codeAt(position + i)))
                    if (++mm == 2)
                        continue OUTER;

            reverseInfoProvider.resetPosition(position);
            treeReverse.performSearch(reverseInfoProvider, resultReverse);
            //Early termination (mandatory region not alligned)
            if (resultReverse.size == 0)
                continue;
            forwardInfoProvider.resetPosition(position + 1);
            treeForward.performSearch(forwardInfoProvider, resultForward);
            //Early termination (mandatory region not alligned)
            if (resultForward.size < mandatoryRegionLength - 1)
                continue;
            max = 0;
            for (int i = 0; i < scores.length; ++i)
                if (max < (scores[i] = resultReverse.alignmentsScore[i] + resultForward.alignmentsScore[i]))
                    max = scores[i];
            //Early terminate this coord to prevent needless sorting.
            if (!better(currentMinScore, max))
                continue;
            sort(scores, permutation);
            firstGoodIndex = -1;
            int i;
            for (i = scores.length - 1; i >= 0 && better(currentMinScore, scores[i]); --i)
                if (resultReverse.alignmentsLengths[permutation[i]] > 0
                        && resultForward.alignmentsLengths[permutation[i]] >= mandatoryRegionLength - 1) {
                    firstGoodIndex = permutation[i];
                    break;
                }
            if (firstGoodIndex == -1)
                continue;
            barcode.loadValueFrom(resultForward.barcodeEvolution[resultForward.alignmentsLengths[firstGoodIndex] - rightDelta - 1]);
            int leftCoord = resultReverse.alignmentsLengths[firstGoodIndex] - leftDelta - 1;
            if (leftCoord < 0)
                leftCoord = 0;
            barcode.and(resultReverse.barcodeEvolution[leftCoord]);
            assert (barcode.bitCount() > 0);
            currentMinScore = maxScore = scores[i]; //Not safe. Using "i"
            //Calculating reference point coord
            int refPoint;
            if (reference >= 0) {
                refPoint = resultForward.tryMapTreeCorrd(reference, barcode);
                if (refPoint == -1) //ambiguous reference point coord
                    continue;
                refPoint++;
            } else {
                refPoint = resultReverse.tryMapTreeCorrd(-1 - reference, barcode);
                if (refPoint == -1) //ambiguous reference point coord
                    continue;
                refPoint = -refPoint;
            }
            refPoint += position;
            mappingResult = new VJSegmentMappingResult(barcode.clone(), treeForward.getGroup(), refPoint,
                    position - resultReverse.alignmentsLengths[firstGoodIndex] + 1,
                    position + resultForward.alignmentsLengths[firstGoodIndex],
                    maxScore);
        }
        //if (counter != null && mappingResult != null)
        //    counter.incrementAndGet();
        return mappingResult;
    }

    private boolean better(int max, int current) {
        if (searchDirection == SearchDirection.BeginToEnd)
            return max < current;
        return max <= current;
    }

    /*@Override
    public SegmentGroupContainer getContainer() {
        return treeForward.getContainer();
    }*/

    /*@Override
    public void assignCounter(AtomicInteger counter) {
        this.counter = counter;
    }*/
}
