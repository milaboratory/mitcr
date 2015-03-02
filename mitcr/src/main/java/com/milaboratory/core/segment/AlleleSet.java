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
 * A set of unique segment alleles
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class AlleleSet extends AbstractSet<Allele> {
    final BitArray bitArray;
    final SegmentGroupContainer container;

    /**
     * Creates a set of unique segment alleles
     *
     * @param bitArray  barcode of alleles to include
     * @param container storage of segment/allele data
     */
    public AlleleSet(BitArray bitArray, SegmentGroupContainer container) {
        this.bitArray = bitArray;
        this.container = container;
        if (bitArray.size() != container.getAllelesCount())
            throw new IllegalArgumentException("Bit array size != alleles count");
    }

    /**
     * Check if set contains an allele that matches given name (e.g. TRBV12-3*01)
     *
     * @param name full name of allele
     * @return true if allele is found, otherwise false
     */
    public boolean contains(String name) {
        return contains(container.getAlleleByName(name));
    }

    public boolean contains(int index) {
        return bitArray.get(index);
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Allele))
            return false;
        if (!container.contains((Allele) o))
            return false;
        return contains(((Allele) o).getIndex());
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
    public Iterator<Allele> iterator() {
        return new It();
    }

    @Override
    public int size() {
        return bitArray.bitCount();
    }

    public BitArray getBitArrayCopy() {
        return bitArray.clone();
    }

    public SegmentSet convertToSegments() {
        return new SegmentSet(container.convertToSegments(bitArray), container);
    }

    public SegmentGroupContainer getContainer() {
        return container;
    }

    @Override
    public String toString() {
        String s = super.toString();
        return s.substring(1, s.length() - 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        //if (!super.equals(o)) return false;

        AlleleSet alleles = (AlleleSet) o;

        if (!container.getSSGTuple().equals(alleles.container.getSSGTuple()))
            return false;

        return bitArray.equals(alleles.bitArray);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + bitArray.hashCode();
        result = 31 * result + container.getSSGTuple().hashCode();
        return result;
    }

    private class It implements Iterator<Allele> {
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
        public Allele next() {
            return container.getAllele(indices[i++]);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
