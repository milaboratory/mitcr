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

import cc.redberry.pipe.Processor;
import cc.redberry.pipe.ThreadSafe;
import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequencing.read.SSequencingReadWithFlowgram;
import com.milaboratory.core.sequencing.read.SSequencingReadWithFlowgramImpl;
import com.milaboratory.util.IndexRange;

/**
 * Processor clipping SFF reads from adapter sequences
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class SFFClipper implements Processor<SFFRAWRead, SSequencingReadWithFlowgram>, ThreadSafe {
    @Override
    public SSequencingReadWithFlowgram process(SFFRAWRead input) {
        int from = input.getActualFirstBase();
        int to = input.getActualLastBase() + 1;
        int[] flowgramIndexes = new int[to - from];
        System.arraycopy(input.getSequenceFlowgramMappping(),
                from, flowgramIndexes, 0, to - from);
        NucleotideSQPair clippedSQPair = input.getData().getRange(from, to);
        //last element in this array. (input.getData().size() == input.getSequenceFlowgramMappping().length)
        int fFrom = 0, fTo = input.getSequenceFlowgramMappping()[input.getData().size() - 1];
        if (from > 0)
            fFrom = input.getSequenceFlowgramMappping()[from - 1] + 1;
        if (to < input.getData().size())
            fTo = input.getSequenceFlowgramMappping()[to] - 1;
        if (fTo > input.getFlowgramSequence().size())
            fTo = input.getFlowgramSequence().size();
        IndexRange range = new IndexRange(fFrom, fTo);
        return new SSequencingReadWithFlowgramImpl(input.getDescription(), clippedSQPair,
                range, input.getFlowgramValues(),
                flowgramIndexes, input.getFlowgramSequence(), input.id());
    }
}
