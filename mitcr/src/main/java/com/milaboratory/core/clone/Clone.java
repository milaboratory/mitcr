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

import java.util.List;

/**
 * An interface representing unique CDR3 region (presumably of one T-cell clone)
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public interface Clone {
    /**
     * Gets nucleotide sequence of CDR3 region
     *
     * @return nucleotide sequence of CDR3 region
     */
    NucleotideSQPair getCDR3();

    /**
     * Gets amino acid sequence (in extended amino acid alphabet, see {@link com.milaboratory.core.sequence.aminoacid.CDRAminoAcidAlphabet}
     * of CDR3 region
     *
     * @return amino acid sequence of CDR3 region
     */
    CDRAminoAcidSequence getCDR3AA();

    /**
     * Get the count of clone in dataset. Throws {@link IllegalStateException} exception if the clone is not attached to
     * clone set
     *
     * @return count of clone in dataset
     */
    long getCount();

    //Throws illegalState exception if not attached to clone set ...

    /**
     * Get the percentage of clone count from total count of clones in dataset. Throws {@link IllegalStateException}
     * exception if the clone is not attached to clone set
     *
     * @return the percentage of clone in dataset
     */
    double getPart();

    /**
     * Gets {@link SequencingReadLink}s for tracking reads used to assemble the clone
     *
     * @return links to reads used to assemble the clone
     */
    List<SequencingReadLink> getBackwardLinks();

    /**
     * Gets V segment set
     *
     * @return V segment set
     */
    SegmentSet getVSegments();

    /**
     * Gets J segment set
     *
     * @return J segment set
     */
    SegmentSet getJSegments();

    /**
     * Gets D segment set
     *
     * @return Gets D segment set
     */
    SegmentSet getDSegments();

    /**
     * Gets V alleles
     *
     * @return V alleles
     */
    AlleleSet getVAlleles();


    /**
     * Gets J alleles
     *
     * @return J alleles
     */
    AlleleSet getJAlleles();

    /**
     * Gets D alleles
     *
     * @return D alleles
     */
    AlleleSet getDAlleles();

    /**
     * Gets segment set of specified type
     *
     * @param groupType segment type
     * @return segment set of specified type
     */
    SegmentSet getSegments(SegmentGroupType groupType);

    /**
     * Gets allele set of specified type
     *
     * @param groupType segment type
     * @return allele set of specified type
     */
    AlleleSet getAlleles(SegmentGroupType groupType);

    /**
     * Gets last coordinate (inclusive) of aligned V gene
     *
     * @return last coordinate (inclusive) of aligned V gene
     */
    int getVEnd();

    /**
     * Gets first coordinate (inclusive) of aligned J gene
     *
     * @return first coordinate (inclusive) of aligned J gene
     */
    int getJStart();

    /**
     * Gets first coordinate (inclusive) of aligned D gene, {@code -1} if not determined
     *
     * @return first coordinate (inclusive) of aligned D gene, {@code -1} if not determined
     */
    int getDStart();

    /**
     * Gets last coordinate (inclusive) of aligned D gene, {@code -1} if not determined
     *
     * @return last coordinate (inclusive) of aligned D gene, {@code -1} if not determined
     */
    int getDEnd();

    /**
     * Gets size of aligned D fragment, {@code -1} if not determined
     *
     * @return size of aligned D fragment, {@code -1} if not determined
     */
    int getDLength();

    /**
     * Is D segment determined successfully?
     *
     * @return {@code true} if D segment aligned successfully, otherwise {@code false}
     */
    boolean isDSegmentDetermined();

    /**
     * Gets the number of insertions between V and D fragments, {@code -1} if D fragment not determined
     *
     * @return the number of insertions between V and D fragments or {@code -1} if D fragment not determined
     */
    int insertionsVD();

    /**
     * Gets the number of insertions between D and J fragments, {@code -1} if D fragment not determined
     *
     * @return the number of insertions between D and J fragments or {@code -1} if D fragment not determined
     */
    int insertionsDJ();

    /**
     * Gets the total number of insertions in CDR3 sequence, {@code -1} if D fragment not determined
     *
     * @return the total number of insertions in CDR3 sequence or {@code -1} if D fragment not determined
     */
    int insertionsTotal();

    /**
     * Gets parent clone set
     *
     * @return parent clone set
     */
    CloneSet getParentCloneSet();

    /**
     * Sets parent clone set
     *
     * @param cloneSet clone set to attach to
     */
    void attachToCloneSet(CloneSet cloneSet);
}
