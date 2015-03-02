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
package com.milaboratory.core.sequence.nucleotide;

import com.milaboratory.core.sequence.SequenceBuilder;
import com.milaboratory.core.sequence.SequenceBuilderFactory;
import com.milaboratory.util.Bit2Array;

/**
 * Creates {@link NucleotideSequence}.
 */
public class NucleotideSequenceBuilder implements SequenceBuilder<NucleotideSequence> {
    public static final SequenceBuilderFactory<NucleotideSequence> FACTORY = new SequenceBuilderFactory<NucleotideSequence>() {
        @Override
        public SequenceBuilder<NucleotideSequence> create(int size) {
            return new NucleotideSequenceBuilder(size);
        }
    };
    private final Bit2Array storage;

    public NucleotideSequenceBuilder(int size) {
        this.storage = new Bit2Array(size);
    }

    @Override
    public int size() {
        return storage.size();
    }

    @Override
    public NucleotideSequence create() {
        return new NucleotideSequence(storage);
    }

    @Override
    public void setCode(int position, byte code) {
        storage.set(position, code);
    }

    @Override
    public void copyFrom(NucleotideSequence sequence, int otherOffset, int thisOffset, int length) {
        storage.copyFrom(sequence.data, otherOffset, thisOffset, length);
    }
}
