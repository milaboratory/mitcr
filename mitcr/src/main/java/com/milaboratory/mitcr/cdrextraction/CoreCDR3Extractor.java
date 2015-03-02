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
package com.milaboratory.mitcr.cdrextraction;

import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.mitcr.vdjmapping.VDJSegmentsMappingResult;

/**
 * Inner extractor of CDR3 using results of V, J gene segments mappings.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
final class CoreCDR3Extractor {
    private final int beginShift, endShift;
    private final int upperLengthThreshold;
    private final int lowerLengthThreshold;
    //private final AtomicInteger extracted = new AtomicInteger();

    CoreCDR3Extractor(int lowerLengthThreshold, int upperLengthThreshold, boolean includeCysPhe) {
        if (includeCysPhe) {
            this.beginShift = -3;
            this.endShift = +3;
        } else {
            this.beginShift = 0;
            this.endShift = 0;
        }
        this.upperLengthThreshold = upperLengthThreshold;
        this.lowerLengthThreshold = lowerLengthThreshold;
    }

    public NucleotideSQPair extract(VDJSegmentsMappingResult mappingResult,
                                    NucleotideSQPair data) {
        if (!mappingResult.isGood())
            return null;
        int length = mappingResult.getJResult().getRefPoint() - mappingResult.getVResult().getRefPoint() + 1;
        if (length > upperLengthThreshold || length < lowerLengthThreshold)
            return null;
        int cys = mappingResult.getVResult().getRefPoint() + beginShift;
        int phe = mappingResult.getJResult().getRefPoint() + endShift;
        if (phe < cys)
            return null;
        try {
            return data.getRange(cys, phe + 1);
        } catch (IndexOutOfBoundsException ex) {  //Very rear event, so Exception usage in this place does make sense.
            return null;
        }
    }
}
