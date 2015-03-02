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
package com.milaboratory.mitcr.clonegenerator;

import cc.redberry.pipe.InputPortUninterruptible;
import com.milaboratory.core.clone.CloneSet;
import com.milaboratory.core.segment.SegmentGroupContainer;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult;

/**
 * An interface for clone generation from raw sequences. Acts as an input port in pipeline
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public interface CloneGenerator extends InputPortUninterruptible<CDR3ExtractionResult<?>> {
    /**
     * In normal conditions clone generator can get information about gene and particular set of gene segments from the
     * input stream of extraction results, but in some conditions (if there where no CDR3s or no D segment was mapped)
     * it requires explicit initialization usint this method.
     *
     * @param v segment group container for V segments
     * @param j segment group container for J segments
     * @param d segment group container for D segments
     */
    void preInitialize(SegmentGroupContainer v, SegmentGroupContainer j, SegmentGroupContainer d);

    /**
     * Build and gets the resulting clone set. Should be called after this port is closed (null was put)
     *
     * @return set of generated clone
     */
    CloneSet getCloneSet();
}
