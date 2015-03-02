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

import com.milaboratory.util.BitArray;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

/**
 * A set of unqiue segments
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class SegmentSet extends AbstractSet<Segment> {
    final BitArray bitArray;
    final SegmentGroupContainer container;

    /**
     * Creates a set of segments
     *
     * @param bitArray  barcode of segments to select in parent container
     * @param container parent container with all possible segments
     */
    public SegmentSet(BitArray bitArray, SegmentGroupContainer container) {
        this.bitArray = bitArray;
        this.container = container;
        if (bitArray.size() != container.getSegmentCount())
            throw new IllegalArgumentException("Bit array size != segment count");
    }

    /**
     * Checks if the set contains segment with a given name (e.g. TRBV12)
     */
    public boolean contains(String name) {
        return contains(container.getSegmentByName(name));
    }

    /**
     * Checks if the set contains segment with a given index
     */
    public boolean contains(int index) {
        return bitArray.get(index);
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Segment))
            return false;
        if (!container.contains((Segment) o))
            return false;
        return bitArray.get(((Segment) o).getIndex());
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        return bitArray.isClean();
    }

    @Override
    public Iterator<Segment> iterator() {
        return new It();
    }

    @Override
    public int size() {
        return bitArray.bitCount();
    }

    public BitArray getBitArrayCopy() {
        return bitArray.clone();
    }

    @Override
    public String toString() {
        String s = super.toString();
        return s.substring(1, s.length() - 1);
    }

    private class It implements Iterator<Segment> {
        private int i = 0;
        private final int[] indices;

        private It() {
            this.indices = bitArray.getBits();
        }

        @Override
        public boolean hasNext() {
            return i < indices.length;
        }

        @Override
        public Segment next() {
            return container.getSegment(indices[i++]);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
