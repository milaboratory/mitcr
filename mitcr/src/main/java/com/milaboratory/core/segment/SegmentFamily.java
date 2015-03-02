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

import com.milaboratory.util.BitArray;

import java.util.HashSet;
import java.util.Set;

/**
 * Recombination segments family. For example TRBV6 for TRBV6-1, TRBV6-2, etc...
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class SegmentFamily {
    private Set<Segment> segments = new HashSet<Segment>();
    private String name;
    private SegmentGroupContainer group;
    private BitArray barcode;

    /**
     * Creates a family of segments
     *
     * @param name  family name
     * @param group container storing all possible segments
     */
    public SegmentFamily(String name, SegmentGroupContainer group) {
        this.name = name;
        this.group = group;
        this.barcode = new BitArray(group.getSegmentCount());
    }

    /**
     * Creates a family of segments
     *
     * @param segment base segment of the family
     * @param group   container storing all possible segments
     */
    public SegmentFamily(Segment segment, SegmentGroupContainer group) {
        this.name = segment.getSegmentName();
        this.group = group;
        this.barcode = new BitArray(group.getSegmentCount());
        add(segment);
    }

    /**
     * Adds a segment to a family
     *
     * @param segment a segment to add
     */
    public final void add(Segment segment) {
        segments.add(segment);
        barcode.set(segment.getIndex());
    }

    /**
     * Checks if this segment family intersects with a given set of segments specified by barcode
     *
     * @param barcode a segment barcode
     * @return true if there is intersection, otherwise false
     */
    public boolean intersects(BitArray barcode) {
        return this.barcode.intersects(barcode);
    }

    /**
     * Gets parent segment group container with all possible elements
     *
     * @return parent segment group container
     */
    public SegmentGroupContainer getGroup() {
        return group;
    }

    /**
     * Gets the name of family
     *
     * @return name of family
     */
    public String getName() {
        return name;
    }

    /**
     * Gets all segments in the family
     *
     * @return all segments in the family
     */
    public Set<Segment> getSegments() {
        return segments;
    }

    /**
     * Gets a binary barcode for family in respect to container storing all possible segments
     *
     * @return a segment barcode
     */
    public BitArray getBarcode() {
        return barcode;
    }

    @Override
    public String toString() {
        return name + " (" + segments.size() + ")";
    }
}
