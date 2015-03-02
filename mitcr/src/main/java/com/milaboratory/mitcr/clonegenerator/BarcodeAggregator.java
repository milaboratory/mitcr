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

import com.milaboratory.util.BitArray;

/**
 * Used internally for allele barcode generation
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
final class BarcodeAggregator {
    private int[] counts;

    BarcodeAggregator(int[] counts) {
        this.counts = counts;
    }

    public BarcodeAggregator(int size) {
        counts = new int[size];
    }

    public void addBarcode(BitArray barcode) {
        if (barcode.size() != counts.length)
            throw new IllegalArgumentException();
        for (int i = 0; i < barcode.size(); ++i)
            if (barcode.get(i))
                counts[i]++;
    }

    public BitArray calculateBarcode(float aggregationFactor) {
        int treshold = 0;
        for (int count : counts)
            if (treshold < count)
                treshold = count;
        treshold *= (1.0f - aggregationFactor);
        BitArray result = new BitArray(counts.length);
        for (int i = 0; i < counts.length; ++i)
            if (counts[i] > treshold)
                result.set(i);
        return result;
    }
}
