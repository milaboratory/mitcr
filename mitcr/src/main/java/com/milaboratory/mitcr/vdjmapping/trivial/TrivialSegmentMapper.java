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
package com.milaboratory.mitcr.vdjmapping.trivial;

import com.milaboratory.core.segment.Allele;
import com.milaboratory.core.segment.SegmentGroupContainer;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.mitcr.qualitystrategy.GoodBadNucleotideSequence;
import com.milaboratory.mitcr.vdjmapping.AlignmentDirection;
import com.milaboratory.mitcr.vdjmapping.VJMapperListener;
import com.milaboratory.mitcr.vdjmapping.VJSegmentMappingResult;
import com.milaboratory.mitcr.vdjmapping.tree.CoreVJSegmentMapper;
import com.milaboratory.util.BitArray;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

/**
 * A mapper that searches for segment's reference point by sliding with a short seed region
 */
public final class TrivialSegmentMapper implements CoreVJSegmentMapper {
    private final int mandatoryFrom, mandatoryRegionLength;
    private final int minLength;
    private final int lengthTolerance;
    private final AlignmentDirection direction;
    private final int segmentDirection;
    private final SegmentGroupContainer container;
    private final VJMapperListener listener;
    //Map from nmer to aligners
    private final TIntObjectMap<OneSideAligner> alleles;

    /**
     * Creates uni-direction segment mapper.
     *
     * @param group           container of segments
     * @param mandatoryFrom   position of first nucleotide of the region that has to be aligned, relative to reference
     *                        point (0 = reference point)
     * @param mandatoryTo     position of last nucleotide of the region that has to be aligned, relative to reference
     *                        point (0 = reference point)
     * @param minLength       minimal length of alignment
     * @param lengthTolerance maximal allowed difference from best alignment length for a segment to be included in
     *                        result
     */
    public TrivialSegmentMapper(SegmentGroupContainer group, int mandatoryFrom,
                                int mandatoryTo, int minLength, int lengthTolerance,
                                AlignmentDirection direction) {
        this(group, mandatoryFrom, mandatoryTo, minLength, lengthTolerance, direction, null);
    }

    /**
     * Creates uni-direction segment mapper.
     *
     * @param group           container of segments
     * @param mandatoryFrom   position of first nucleotide of the region that has to be aligned, relative to reference
     *                        point (0 = reference point)
     * @param mandatoryTo     position of last nucleotide of the region that has to be aligned, relative to reference
     *                        point (0 = reference point)
     * @param minLength       minimal length of alignment
     * @param lengthTolerance maximal allowed difference from best alignment length for a segment to be included in
     *                        result
     */
    public TrivialSegmentMapper(SegmentGroupContainer group, int mandatoryFrom,
                                int mandatoryTo, int minLength, int lengthTolerance,
                                AlignmentDirection direction,
                                VJMapperListener listener) {
        if (group == null)
            throw new NullPointerException();

        if (direction != AlignmentDirection.Both && direction != AlignmentDirection.InsideCDR3 && direction != AlignmentDirection.OutsideCDR3)
            throw new IllegalArgumentException();

        if (mandatoryFrom > mandatoryTo)
            throw new IllegalArgumentException();

        if (lengthTolerance < 0)
            throw new IllegalArgumentException("LengthTolerance must be positive.");

        this.container = group;
        this.segmentDirection = group.getGroup().getType().cdr3Site();
        this.direction = direction;
        this.listener = listener;
        //this.scores = new int[group.getAllelesCount()];
        //this.permutation = new int[group.getAllelesCount()];
        this.minLength = minLength;
        this.lengthTolerance = lengthTolerance;
        this.mandatoryRegionLength = mandatoryTo - mandatoryFrom + 1;
        if (this.mandatoryRegionLength <= 3)
            throw new IllegalArgumentException("Too short mandatory region.");
        if (this.mandatoryRegionLength >= 12)
            throw new IllegalArgumentException("Mandatory region is too long.");
        this.mandatoryFrom = mandatoryFrom;
        //Building map
        TIntObjectMap<List<Allele>> preMap = new TIntObjectHashMap<>();
        List<Allele> list;
        NucleotideSequence sequence;
        int position, kMer;
        for (Allele allele : group.getAllelesList()) {
            sequence = allele.getSequence();
            kMer = 0;
            for (position = mandatoryFrom; position <= mandatoryTo; ++position)
                kMer = (kMer << 2) |
                        (0x3 & sequence.codeAt(position + allele.getReferencePointPosition()));

            if ((list = preMap.get(kMer)) == null)
                preMap.put(kMer, list = new ArrayList<>());
            list.add(allele);
        }

        TIntObjectMap<OneSideAligner> map = new TIntObjectHashMap<>();
        for (TIntObjectIterator<List<Allele>> iterator = preMap.iterator();
             iterator.hasNext(); ) {
            iterator.advance();
            map.put(iterator.key(),
                    createAligner(iterator.value().toArray(new Allele[iterator.value().size()])));
            //new OneSideAligner(group,
            //       iterator.value().toArray(new Allele[iterator.value().size()]),
            //        -2 * segmentDirection, lengthTolerance * 2, 2, 1));
        }

        alleles = map;
    }

    public OneSideAligner createAligner(Allele[] alleles) {
        if (direction == AlignmentDirection.Both)
            return new OneSideAligner(container,
                    alleles,
                    -2 * segmentDirection, lengthTolerance * 2, 2, 1); //Alignment offset set to the central nucleotide in Cys/Phe codone
        else {
            return new OneSideAligner(container,
                    alleles,
                    direction.getDirectionFactor() * segmentDirection == 1 ? mandatoryFrom : mandatoryFrom + mandatoryRegionLength, //no -1 !!!
                    lengthTolerance * 2, 2, 1);
        }
    }

    @Override
    public VJSegmentMappingResult map(GoodBadNucleotideSequence sequence) {
        final int cTo = sequence.size();
        VJSegmentMappingResult mappingResult = null;
        //int maxScore = -1;

        final int mask = 0xFFFFFFFF >>> (32 - mandatoryRegionLength * 2);
        int kmer = 0;
        int position;

        //Prepare kMer
        for (position = 0; position < mandatoryRegionLength - 1; ++position)
            kmer = mask & ((kmer << 2) | sequence.codeAt(position));

        BitArray possibleSegments;
        OneSideAlignmentResult result;
        int refPoint;
        //int from, to;
        OneSideAligner candidates;
        for (; position < cTo; ++position) {
            //Calculating kMer for the next position
            kmer = mask & ((kmer << 2) | sequence.codeAt(position));

            if ((candidates = alleles.get(kmer)) != null) {
                refPoint = position - mandatoryRegionLength - mandatoryFrom + 1;
                if (direction == AlignmentDirection.Both)
                    result = candidates.doubleSidedAlignmentResult(sequence, refPoint, -1 * segmentDirection); //Firstly align part that is outside CDR3
                else
                    result = candidates.buildResult(sequence, refPoint, direction.getDirectionFactor() * segmentDirection);

                if (mappingResult == null || mappingResult.getScore() < result.score)
                    mappingResult = new VJSegmentMappingResult(result.possibleSegments, container,
                            refPoint, result.continuousAlignmentFrom, result.continuousAlignmentTo,
                            result.score);
            }
        }

        if (mappingResult != null && mappingResult.getScore() < minLength * 2) {
            if (listener != null)
                listener.mappingDropped(mappingResult, sequence);
            return null;
        }

        if (listener != null)
            if (mappingResult != null)
                listener.mappingFound(mappingResult, sequence);
            else
                listener.noMapping(sequence);

        return mappingResult;
    }
}
