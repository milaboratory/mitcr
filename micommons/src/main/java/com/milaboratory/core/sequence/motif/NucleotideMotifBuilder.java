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
package com.milaboratory.core.sequence.motif;

import com.milaboratory.util.BitArray;

public class NucleotideMotifBuilder {
    private final BitArray store;

    public NucleotideMotifBuilder(int size) {
        this.store = new BitArray(size << 2);
    }

    public void set(int position, byte code) {
        store.set(code + (position << 2));
    }

    public int size() {
        return store.size() >> 2;
    }

    public NucleotideMotif build() {
        return new NucleotideMotif(store.clone());
    }
}
