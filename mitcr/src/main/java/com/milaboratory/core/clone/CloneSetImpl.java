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

import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.SegmentGroupContainer;
import com.milaboratory.core.segment.SegmentGroupType;
import com.milaboratory.core.segment.Species;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;

import java.util.*;

/**
 * An implementation of {@link CloneSet}
 *
 * @param <C> clone type
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class CloneSetImpl<C extends Clone> implements CloneSet {
    final SegmentGroupContainer[] segmentContainers = new SegmentGroupContainer[3];
    final List<C> clones;
    Map<NucleotideSequence, C> cdr3ToClone;
    final long totalCount;
    final Gene gene;
    final Species species;

    /**
     * Creates a clone set
     *
     * @param clones     collection of clones
     * @param gene       the parent gene of CDR3 region
     * @param species    species
     * @param vContainer container with used V segments
     * @param jContainer container with used J segments
     * @param dContainer container with used D segments
     */
    public CloneSetImpl(Collection<? extends C> clones, Gene gene, Species species, SegmentGroupContainer vContainer,
                        SegmentGroupContainer jContainer, SegmentGroupContainer dContainer) {
        this.clones = Collections.unmodifiableList(new ArrayList<>(clones));
        long t = 0;
        for (Clone clone : this.clones) {
            t += clone.getCount();
            clone.attachToCloneSet(this);
        }
        this.totalCount = t;
        this.gene = gene;
        this.species = species;
        this.segmentContainers[0] = vContainer;
        this.segmentContainers[1] = jContainer;
        this.segmentContainers[2] = dContainer;
    }

    @Override
    public Clone getCloneByCDR3(NucleotideSequence sequence) {
        if (cdr3ToClone == null) {
            cdr3ToClone = new HashMap<>(cdr3ToClone.size());
            for (C c : clones)
                cdr3ToClone.put(c.getCDR3().getSequence(), c);
        }
        return cdr3ToClone.get(sequence);
    }

    @Override
    public long getTotalCount() {
        return totalCount;
    }

    @Override
    public List<C> getClones() {
        return clones;
    }

    @Override
    public Species getSpecies() {
        return species;
    }

    @Override
    public Gene getGene() {
        return gene;
    }

    @Override
    public Iterator<Clone> iterator() {
        return (Iterator) clones.iterator();
    }

    @Override
    public SegmentGroupContainer getSegmentGroupContainer(SegmentGroupType type) {
        return segmentContainers[type.id()];
    }
}
