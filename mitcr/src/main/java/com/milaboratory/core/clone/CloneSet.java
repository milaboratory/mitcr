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

import java.util.List;

/**
 * A set of clones
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public interface CloneSet extends Iterable<Clone> {
    /**
     * Gets total number of CDR3-containing sequences in dataset
     *
     * @return total number of CDR3-containing sequences in dataset
     */
    long getTotalCount();

    /**
     * List of clones with unique CDR3 sequences
     *
     * @return list of clones with unique CDR3 sequences
     */
    List<? extends Clone> getClones();

    //TODO doc
    Clone getCloneByCDR3(NucleotideSequence sequence);

    /**
     * Gets segment library of a specified type used to extract CDR3
     *
     * @param type type of segment
     * @return segment library of a specified type used to extract CDR3
     */
    SegmentGroupContainer getSegmentGroupContainer(SegmentGroupType type);

    /**
     * Gets the parent gene of CDR3 region
     *
     * @return the parent gene of CDR3 region
     */
    Gene getGene();

    Species getSpecies();
}
