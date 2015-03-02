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

import com.milaboratory.core.segment.SegmentGroupContainer;
import com.milaboratory.util.BitArray;

import java.io.Serializable;

/**
 * Class to store results of V and J segments mapping.<br/> Adds refPoint field.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class VJSegmentMappingResult extends SegmentMappingResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private int refPoint;

    /**
     * Creates VJ segment mapping result.
     *
     * @param barcode           aligned alleles barcode.
     * @param group             segment group container for witch alignment where performed.
     * @param refPoint          coordinate of CDR3 border. For V: nucleotide right after 2nd-CYS. For J: nucleotide
     *                          right before J-PHE.
     * @param segmentBorderFrom coord of first aligned nucleotide. (inclusive)
     * @param segmentBorderTo   coord of last aligned nucleotide. (inclusive)
     * @param score             some alignment score. Alignment size for implemented algorithms.
     */
    public VJSegmentMappingResult(BitArray barcode, SegmentGroupContainer group, int refPoint, int segmentBorderFrom,
                                  int segmentBorderTo, float score) {
        super(barcode, group, segmentBorderFrom, segmentBorderTo, score);
        this.refPoint = refPoint;
    }

    public int getRefPoint() {
        return refPoint;
    }

    public void setRefPoint(int refPoint) {
        this.refPoint = refPoint;
    }
}
