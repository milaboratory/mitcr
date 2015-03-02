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

import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;

/**
 * Container with full information about a recombination segment allele. Contains allele sequence, etc.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class Allele {
    private Segment segment;
    private String accessionNumber;
    private String fullName;
    //TRBV12-3*number <-- ??
    private int number;
    private int index; //Main Index
    private String functionality;
    private int referencePointPosition;
    private NucleotideSequence sequence;

    /**
     * Creates a new segment allele
     *
     * @param fullName               full name of the allele
     * @param number                 allele number (e.g. *01, *02,...)
     * @param index                  allele internal index
     * @param functionality          is allele functional
     * @param accessionNumber        allele accession (e.g. IMGT id)
     * @param referencePointPosition reference point (e.g. first letter after Cys for V genes)
     * @param sequence               allele sequence
     */
    public Allele(String fullName, int number, int index, String functionality, String accessionNumber, int referencePointPosition, NucleotideSequence sequence) {
        this.fullName = fullName;
        this.index = index;
        this.functionality = functionality;
        this.referencePointPosition = referencePointPosition;
        this.sequence = sequence;
        this.accessionNumber = accessionNumber;
        this.number = number;
    }

    void assignSegment(Segment segment) {
        if (this.segment != null)
            throw new IllegalStateException("Segment already assigned");
        this.segment = segment;
    }

    /**
     * Get the segment this allele belongs to
     *
     * @return parent segment
     */
    public Segment getSegment() {
        return segment;
    }

    /**
     * Gets the full name of allele. E.g. TRBV12-3*01
     *
     * @return full name of allele.
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Checks if this allele is functional
     *
     * @return true if this allele is functional, otherwise false
     */
    public String getFunctionality() {
        return functionality;
    }

    /**
     * Gets the internal index of allele in allele list
     *
     * @return internal index of allele in allele list TODO: consider including parent segment library
     */
    public int getIndex() {
        return index;
    }

    /**
     * Gets the reference point (zero-based) of the allele. E.g. coordinate of first letter after Cysteine for V gene,
     * last letter before Phenylalanine for J gene and 0 for D gene. Reference points are to be extracted from IMGT.
     *
     * @return reference point coordinate
     */
    public int getReferencePointPosition() {
        return referencePointPosition;
    }

    /**
     * Gets the nucleotide sequence of this allele
     *
     * @return the nucleotide sequence of this allele
     */
    public NucleotideSequence getSequence() {
        return sequence;
    }

    /**
     * Gets accession number of allele according to IMGT
     *
     * @return accession number of allele according to IMGT
     */
    public String getAccessionNumber() {
        return accessionNumber;
    }

    /**
     * Allele number, ordered by their frequency (e.g. TRBV12-3*01 is more frequent than TRBV12-3*02)
     *
     * @return allele number
     */
    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return fullName;
    }
}
