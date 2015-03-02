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

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of {@link CloneCluster}
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class CloneClusterImpl implements CloneCluster {
    protected final Clone centralClone;
    protected final List<Clone> leafs;
    protected final long count;
    protected CloneSet cloneSet;

    /**
     * Creates a clone cluster
     *
     * @param centralClone representative clone from the cluster
     * @param leafs        child clones
     */
    public CloneClusterImpl(Clone centralClone, List<Clone> leafs) {
        this.centralClone = centralClone;
        this.leafs = leafs;
        long c = centralClone.getCount();
        for (Clone leaf : leafs)
            c += leaf.getCount();
        this.count = c;
    }

    @Override
    public List<Clone> getChildClones() {
        return leafs;
    }

    @Override
    public Clone getCentralClone() {
        return centralClone;
    }

    @Override
    public NucleotideSQPair getCDR3() {
        return centralClone.getCDR3();
    }

    @Override
    public CDRAminoAcidSequence getCDR3AA() {
        return centralClone.getCDR3AA();
    }

    @Override
    public long getCount() {
        return count;
    }

    @Override
    public double getPart() {
        return ((double) count) / cloneSet.getTotalCount();
    }

    @Override
    public List<SequencingReadLink> getBackwardLinks() {
        int size = centralClone.getBackwardLinks().size();
        for (Clone leaf : getChildClones())
            size += leaf.getBackwardLinks().size();
        final List<SequencingReadLink> links = new ArrayList<>(size);
        links.addAll(centralClone.getBackwardLinks());
        for (Clone leaf : getChildClones())
            links.addAll(leaf.getBackwardLinks());
        return links;
    }

    @Override
    public SegmentSet getVSegments() {
        return centralClone.getVSegments();
    }

    @Override
    public SegmentSet getJSegments() {
        return centralClone.getJSegments();
    }

    @Override
    public SegmentSet getDSegments() {
        return centralClone.getDSegments();
    }

    @Override
    public AlleleSet getVAlleles() {
        return centralClone.getVAlleles();
    }

    @Override
    public AlleleSet getJAlleles() {
        return centralClone.getJAlleles();
    }

    @Override
    public AlleleSet getDAlleles() {
        return centralClone.getDAlleles();
    }

    @Override
    public SegmentSet getSegments(SegmentGroupType groupType) {
        return centralClone.getSegments(groupType);
    }

    @Override
    public AlleleSet getAlleles(SegmentGroupType groupType) {
        return centralClone.getAlleles(groupType);
    }

    @Override
    public int getVEnd() {
        return centralClone.getVEnd();
    }

    @Override
    public int getJStart() {
        return centralClone.getJStart();
    }

    @Override
    public int getDStart() {
        return centralClone.getDStart();
    }

    @Override
    public int getDEnd() {
        return centralClone.getDEnd();
    }

    @Override
    public int getDLength() {
        return centralClone.getDLength();
    }

    @Override
    public boolean isDSegmentDetermined() {
        return centralClone.isDSegmentDetermined();
    }

    @Override
    public int insertionsVD() {
        return centralClone.insertionsVD();
    }

    @Override
    public int insertionsDJ() {
        return centralClone.insertionsDJ();
    }

    @Override
    public int insertionsTotal() {
        return centralClone.insertionsTotal();
    }

    @Override
    public CloneSet getParentCloneSet() {
        return cloneSet;
    }

    @Override
    public void attachToCloneSet(CloneSet cloneSet) {
        this.cloneSet = cloneSet;
    }
}
