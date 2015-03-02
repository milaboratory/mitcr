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
package com.milaboratory.core.sequencing.io.sff;

import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.core.sequencing.read.SSequencingReadWithFlowgramImpl;
import com.milaboratory.util.IndexRange;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class SFFRAWRead extends SSequencingReadWithFlowgramImpl {
    private int clipQualLeft, clipQualRight, clipAdapterLeft, clipAdapterRight;

    public SFFRAWRead(String description, NucleotideSQPair pair, IndexRange flowgramRange,
                      int[] flowgramValues, int[] flowgramIndexes, NucleotideSequence flowgramSequence,
                      int clipQualLeft, int clipQualRight, int clipAdapterLeft, int clipAdapterRight,
                      long id) {
        super(description, pair, flowgramRange, flowgramValues, flowgramIndexes, flowgramSequence,
                id);
        this.clipQualLeft = clipQualLeft;
        this.clipQualRight = clipQualRight;
        this.clipAdapterLeft = clipAdapterLeft;
        this.clipAdapterRight = clipAdapterRight;
    }

    public int getClipAdapterLeft() {
        return clipAdapterLeft;
    }

    public int getClipAdapterRight() {
        return clipAdapterRight;
    }

    public int getClipQualLeft() {
        return clipQualLeft;
    }

    public int getClipQualRight() {
        return clipQualRight;
    }

    public int getActualFirstBase() {
        return Math.max(0, Math.max(clipQualLeft, clipAdapterLeft));
    }

    public int getActualLastBase() {
        return Math.min(clipQualRight == -1 ? getData().size() - 1 : clipQualRight,
                clipAdapterRight == -1 ? getData().size() - 1 : clipAdapterRight);
    }
}
