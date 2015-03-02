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
package com.milaboratory.core.clone;

import com.milaboratory.core.segment.AlleleSet;
import com.milaboratory.core.segment.SegmentGroupType;
import com.milaboratory.core.segment.SegmentSet;
import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequence.aminoacid.CDRAminoAcidSequence;
import com.milaboratory.mitcr.clonegenerator.SequencingReadLink;
import com.milaboratory.util.BitArray;

import java.util.Arrays;
import java.util.List;

/**
 * An implementation of {@link Clone}
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class CloneImpl implements Clone {
    protected final int id;
    protected NucleotideSQPair cdr3;
    //V = 0; J = 1; D = 2
    protected final BitArray[] barcodes = new BitArray[3];
    protected int[] segmentCoords;
    protected long count = 0;
    protected List<SequencingReadLink> links;
    protected CloneSet cloneSet;

    /**
     * Creates a clone based on id and CDR3 sequence
     *
     * @param id   clone id
     * @param cdr3 CDR3 sequence
     */
    public CloneImpl(int id, NucleotideSQPair cdr3) {
        this.id = id;
        this.cdr3 = cdr3;
    }

    /**
     * Creates a clone based on id, CDR3 sequence, count and segment data
     *
     * @param id
     * @param cdr3
     * @param count
     * @param barcodes
     * @param segmentCoords
     */
    public CloneImpl(int id, NucleotideSQPair cdr3, long count, BitArray[] barcodes, int[] segmentCoords) {
        this.id = id;
        this.cdr3 = cdr3;
        System.arraycopy(barcodes, 0, this.barcodes, 0, 3);
        this.segmentCoords = segmentCoords.clone();
        this.count = count;
    }

    /**
     * Clones the clone
     *
     * @param clone clone to clone
     */
    public CloneImpl(CloneImpl clone) {
        this.id = clone.id;
        this.cdr3 = clone.getCDR3();
        //this.cloneSet = clone.cloneSet;
        System.arraycopy(clone.barcodes, 0, this.barcodes, 0, 3);
        if (clone.segmentCoords != null)
            this.segmentCoords = clone.segmentCoords.clone();
        this.links = clone.links;
    }

    @Override
    public CloneSet getParentCloneSet() {
        return cloneSet;
    }

    @Override
    public long getCount() {
        return count;
    }

    @Override
    public int getVEnd() {
        return segmentCoords[0];
    }

    @Override
    public int getDStart() {
        if (!isDSegmentDetermined())
            return -1;
        return segmentCoords[1];
    }

    @Override
    public int getDEnd() {
        if (!isDSegmentDetermined())
            return -1;
        return segmentCoords[2];
    }

    @Override
    public boolean isDSegmentDetermined() {
        return segmentCoords.length == 4;
    }

    @Override
    public int insertionsVD() {
        if (!isDSegmentDetermined())
            return -1;
        return segmentCoords[1] - segmentCoords[0] - 1;
    }

    @Override
    public int insertionsDJ() {
        if (!isDSegmentDetermined())
            return -1;
        return segmentCoords[3] - segmentCoords[2] - 1;
    }

    @Override
    public int insertionsTotal() {
        if (isDSegmentDetermined())
            return segmentCoords[1] - segmentCoords[0] + segmentCoords[3] - segmentCoords[2] - 2;
        else
            return segmentCoords[1] - segmentCoords[0] - 1;
    }

    @Override
    public int getDLength() {
        if (!isDSegmentDetermined())
            return 0;
        return 1 + getDEnd() - getDStart();
    }

    @Override
    public int getJStart() {
        return segmentCoords[segmentCoords.length - 1];
    }

    @Override
    public String toString() {
        return count + " " + cdr3.getSequence().toString();
    }

    @Override
    public NucleotideSQPair getCDR3() {
        return cdr3;
    }

    @Override
    public CDRAminoAcidSequence getCDR3AA() {
        return new CDRAminoAcidSequence(cdr3.getSequence());
    }

    @Override
    public List<SequencingReadLink> getBackwardLinks() {
        return links;
    }

    @Override
    public SegmentSet getVSegments() {
        return new SegmentSet(cloneSet.getSegmentGroupContainer(SegmentGroupType.Variable)
                .convertToSegments(barcodes[0]), cloneSet.getSegmentGroupContainer(SegmentGroupType.Variable));
    }

    @Override
    public SegmentSet getJSegments() {
        return new SegmentSet(cloneSet.getSegmentGroupContainer(SegmentGroupType.Joining)
                .convertToSegments(barcodes[1]), cloneSet.getSegmentGroupContainer(SegmentGroupType.Joining));
    }

    @Override
    public SegmentSet getDSegments() {
        //if (cloneSet.getSegmentGroupContainer(SegmentGroupType.Diversity) != null)
        if (barcodes[2] != null)
            return new SegmentSet(cloneSet.getSegmentGroupContainer(SegmentGroupType.Diversity)
                    .convertToSegments(barcodes[2]), cloneSet.getSegmentGroupContainer(SegmentGroupType.Diversity));
        return null; //new SegmentSet(new BitArray(cloneSet.getSegmentGroupContainer(SegmentGroupType.Diversity)
        // .getSegmentCount()), cloneSet.getSegmentGroupContainer(SegmentGroupType.Diversity));
    }

    @Override
    public AlleleSet getVAlleles() {
        return new AlleleSet(barcodes[0], cloneSet.getSegmentGroupContainer(SegmentGroupType.Variable));
    }

    @Override
    public AlleleSet getJAlleles() {
        return new AlleleSet(barcodes[1], cloneSet.getSegmentGroupContainer(SegmentGroupType.Joining));
    }

    @Override
    public AlleleSet getDAlleles() {
        //if (cloneSet.getSegmentGroupContainer(SegmentGroupType.Diversity) != null)
        if (barcodes[2] != null)
            return new AlleleSet(barcodes[2], cloneSet.getSegmentGroupContainer(SegmentGroupType.Diversity));
        return null; //new AlleleSet(new BitArray(cloneSet.getSegmentGroupContainer(SegmentGroupType.Diversity)
        //.getAllelesCount()), cloneSet.getSegmentGroupContainer(SegmentGroupType.Diversity));
    }

    @Override
    public SegmentSet getSegments(SegmentGroupType groupType) {
        switch (groupType) {
            case Variable:
                return getVSegments();
            case Joining:
                return getJSegments();
            case Diversity:
                return getDSegments();
        }
        throw new IllegalArgumentException();
    }

    @Override
    public AlleleSet getAlleles(SegmentGroupType groupType) {
        switch (groupType) {
            case Variable:
                return getVAlleles();
            case Joining:
                return getJAlleles();
            case Diversity:
                return getDAlleles();
        }
        throw new IllegalArgumentException();
    }

    @Override
    public double getPart() {
        if (cloneSet == null)
            throw new IllegalStateException("Clone is not attached to any clone set.");
        return count / ((double) cloneSet.getTotalCount());
    }

    @Override
    public void attachToCloneSet(CloneSet cloneSet) {
        this.cloneSet = cloneSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CloneImpl)) return false;

        CloneImpl clone = (CloneImpl) o;

        if (count != clone.count) return false;
        if (!Arrays.equals(barcodes, clone.barcodes)) return false;
        if (!cdr3.equals(clone.cdr3)) return false;

        return Arrays.equals(segmentCoords, clone.segmentCoords);
    }

    @Override
    public int hashCode() {
        int result = cdr3.hashCode();
        result = 31 * result + Arrays.hashCode(barcodes);
        result = 31 * result + Arrays.hashCode(segmentCoords);
        result = 31 * result + (int) (count ^ (count >>> 32));
        return result;
    }
}
