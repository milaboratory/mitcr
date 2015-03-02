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

import com.milaboratory.core.segment.AlleleSet;
import com.milaboratory.core.segment.SegmentGroupContainer;
import com.milaboratory.util.BitArray;

import java.io.Serializable;

/**
 * Class to store information about segment mapping to sequencing read.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class SegmentMappingResult implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final BitArray barcode;
    protected final transient SegmentGroupContainer group;
    /**
     * inclusive
     */
    protected int segmentBorderFrom, segmentBorderTo;
    protected float score;

    /**
     * Creates segment mapping result.
     *
     * @param barcode           aligned alleles barcode.
     * @param group             segment group container used in alignment process.
     * @param segmentBorderFrom coord of first aligned nucleotide. (inclusive)
     * @param segmentBorderTo   coord of last aligned nucleotide. (inclusive)
     * @param score             some alignment score. Alignment size for implemented algorithms.
     */
    public SegmentMappingResult(BitArray barcode, SegmentGroupContainer group, int segmentBorderFrom, int segmentBorderTo, float score) {
        this.barcode = barcode;
        this.group = group;
        this.segmentBorderFrom = segmentBorderFrom;
        this.segmentBorderTo = segmentBorderTo;
        this.score = score;
    }

    /**
     * Gets alignment score
     *
     * @return alignment score
     */
    public float getScore() {
        return score;
    }

    /**
     * Gets aligned alleles barcode with corresponding alleles marked with {@code 1}
     *
     * @return aligned alleles barcode
     */
    public BitArray getBarcode() {
        return barcode;
    }

    /**
     * Gets segment group container used in alignment process
     *
     * @return segment group container used in alignment process
     */
    public SegmentGroupContainer getContainer() {
        return group;
    }

    /**
     * Gets the coordinate of first aligned nucleotide (inclusive)
     *
     * @return coordinate of first aligned nucleotide
     */
    public int getSegmentBorderFrom() {
        return segmentBorderFrom;
    }

    /**
     * Gets the coordinate of last aligned nucleotide (inclusive)
     *
     * @return coordinate of last aligned nucleotide
     */
    public int getSegmentBorderTo() {
        return segmentBorderTo;
    }

    /**
     * Sets the coordinate of first aligned nucleotide (inclusive)
     *
     * @param segmentBorderFrom coordinate
     */
    public void setSegmentBorderFrom(int segmentBorderFrom) {
        this.segmentBorderFrom = segmentBorderFrom;
    }

    /**
     * Sets the coordinate of last aligned nucleotide (inclusive)
     *
     * @param segmentBorderTo coordinate
     */
    public void setSegmentBorderTo(int segmentBorderTo) {
        this.segmentBorderTo = segmentBorderTo;
    }

    /**
     * Sets the alignment score to some value
     *
     * @param score new score value
     */
    public void setScore(float score) {
        this.score = score;
    }

    public AlleleSet getAlleles() {
        return new AlleleSet(barcode, group);
    }
}
