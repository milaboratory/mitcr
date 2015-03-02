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
package com.milaboratory.core.sequence.util;

import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.util.Bit2Array;

public class NucleotideSequenceGenerator {
    public static NucleotideSequence[] generate(int length, int count) {
        if (length % 4 != 0)
            throw new IllegalArgumentException("length % 4 == 0 only");
        NucleotideSequence[] result = new NucleotideSequence[count];
        int stSize = length >> 2;
        byte[] data = new byte[stSize];
        for (int i = 0; i < count; ++i) {
            Bit2Array st = Bit2Array.construct(length, data.clone());
            result[i] = new NucleotideSequence(st);

            data[data.length - 1]++;
            for (int j = data.length - 2; j >= 0; --j)
                if (data[j + 1] == 0)
                    data[j]++;
                else
                    break;
        }
        return result;
    }
}
