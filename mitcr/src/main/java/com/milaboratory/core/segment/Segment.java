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
package com.milaboratory.core.segment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Container with information for a recombination segment. For example: TRBV20-1. Note that detailed data, such as
 * sequences and reference points is contained in child alleles <p/> <p>It also contains information about known
 * alleles.</p>
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class Segment {
    private String segmentName;
    private int index;
    private SegmentGroupContainer parentContainer = null;
    private List<Allele> mAlleles;
    private List<Allele> alleles;

    /**
     * Creates a new recombination segment
     *
     * @param segmentName name of segment
     * @param index       internal index of segment
     * @param alleles     list of child alleles
     */
    public Segment(String segmentName, int index, List<Allele> alleles) {
        this.segmentName = segmentName;
        this.index = index;
        for (Allele allele : alleles)
            allele.assignSegment(this);
        mAlleles = new ArrayList<Allele>(alleles);
        Collections.sort(alleles, new Comparator<Allele>() {
            public int compare(Allele o1, Allele o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });
        this.alleles = Collections.unmodifiableList(mAlleles);
    }

    void assignType(SegmentGroupContainer parentContainer) {
        if (this.parentContainer != null)
            throw new IllegalStateException("Type already assigned.");
        this.parentContainer = parentContainer;
    }

    /**
     * Gets the parent container with all segments
     *
     * @return parent container with all segments
     */
    public SegmentGroupContainer getParentContainer() {
        return parentContainer;
    }

    /**
     * Gets the internal index of segment
     *
     * @return index of segment
     */
    public int getIndex() {
        return index;
    }

    /**
     * Gets the segment name
     *
     * @return segment name
     */
    public String getSegmentName() {
        return segmentName;
    }

    /**
     * Gets the list of child alleles
     *
     * @return list of child alleles
     */
    public List<Allele> getAlleles() {
        return alleles;
    }

    @Override
    public String toString() {
        return segmentName;
    }
}
