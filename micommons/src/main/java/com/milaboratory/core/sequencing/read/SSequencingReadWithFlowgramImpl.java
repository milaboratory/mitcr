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
package com.milaboratory.core.sequencing.read;

import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.util.IndexRange;

/**
 * Main implementation of SSequencingReadWithFlowgram.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class SSequencingReadWithFlowgramImpl extends SSequencingReadImpl implements SSequencingReadWithFlowgram {
    private IndexRange flowgramRange;
    private int[] flowgramValues;
    private int[] flowgramIndexes;
    private NucleotideSequence flowgramSequence;

    public SSequencingReadWithFlowgramImpl(String description, NucleotideSQPair pair, IndexRange flowgramRange,
                                           int[] flowgramValues, int[] flowgramIndexes,
                                           NucleotideSequence flowgramSequence, long id) {
        super(description, pair, id);
        if (flowgramIndexes.length != pair.size())
            throw new IllegalArgumentException("flowgramIndexes array length doesn't match sequence length");
        this.flowgramRange = flowgramRange;
        this.flowgramValues = flowgramValues;
        this.flowgramIndexes = flowgramIndexes;
        this.flowgramSequence = flowgramSequence;
    }

    /*public void setFlowgramSequence(NucleotideSequence flowgramSequence) {
        if (flowgramSequence == null)
            throw new NullPointerException();
        if (this.flowgramSequence != null)
            throw new IllegalStateException();
        if (flowgramSequence.size() != flowgramValues.length)
            throw new IllegalArgumentException("flowgramValues array length doesn't match flowgram sequence length");
        this.flowgramSequence = flowgramSequence;
    }*/

    @Override
    public IndexRange getFlowgramRange() {
        return flowgramRange;
    }

    @Override
    public NucleotideSequence getFlowgramSequence() {
        return flowgramSequence;
    }

    @Override
    public int[] getFlowgramValues() {
        return flowgramValues;
    }

    @Override
    public int[] getSequenceFlowgramMappping() {
        return flowgramIndexes;
    }
}
