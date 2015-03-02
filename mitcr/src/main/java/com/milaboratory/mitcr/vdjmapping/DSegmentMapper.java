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

import com.milaboratory.core.segment.Allele;
import com.milaboratory.core.segment.SegmentGroupContainer;
import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.util.BitArray;

import java.util.ArrayList;
import java.util.List;

public final class DSegmentMapper implements SegmentMapper {
    private final SegmentGroupContainer group;
    private final SegmentSequenceWrapper[] wrappers;
    private final int minLength;
    /**
     * If true also search for reverse-complement sequence of D segments
     */
    private final boolean searchRC;

    public DSegmentMapper(SegmentGroupContainer group, int minLength, boolean searchRC) {
        if (group == null)
            throw new NullPointerException();
        this.searchRC = searchRC;
        this.group = group;
        this.minLength = minLength;

        List<SegmentSequenceWrapper> wrappers = new ArrayList<>();
        for (Allele al : group.getAllelesList()) {
            wrappers.add(new SegmentSequenceWrapper(al.getSequence(), al));
            if (searchRC)
                wrappers.add(new SegmentSequenceWrapper(
                        al.getSequence().getReverseComplement(), al));
        }
        this.wrappers = wrappers.toArray(new SegmentSequenceWrapper[wrappers.size()]);
    }

    public SegmentMappingResult map(NucleotideSQPair sequence) {
        return map(sequence.getSequence());
    }

    public SegmentMappingResult map(NucleotideSequence sequence) {
        SimplestAlignment bestAlignment = null, alignment;
        BitArray barcode = new BitArray(group.getAllelesCount());
        for (SegmentSequenceWrapper wrapper : wrappers)
            if ((alignment = SimplestAlignment.build(sequence, wrapper.sequence, minLength))
                    != null) {
                if (bestAlignment == null || alignment.length > bestAlignment.length) {
                    barcode.clearAll();
                    barcode.set(wrapper.allele.getIndex());
                    bestAlignment = alignment;
                } else if (alignment.length == bestAlignment.length)
                    barcode.set(wrapper.allele.getIndex());
            }
        if (bestAlignment == null)
            return null;
        else
            return new SegmentMappingResult(barcode, group, bestAlignment.targetFrom, bestAlignment.targetFrom + bestAlignment.length - 1, bestAlignment.length);
    }

    public int getMinLength() {
        return minLength;
    }

    public static final class SegmentSequenceWrapper {
        final NucleotideSequence sequence;
        final Allele allele;

        public SegmentSequenceWrapper(NucleotideSequence sequence, Allele allele) {
            this.sequence = sequence;
            this.allele = allele;
        }
    }
}
