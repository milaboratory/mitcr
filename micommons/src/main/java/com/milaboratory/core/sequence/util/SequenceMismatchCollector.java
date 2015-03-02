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

import com.milaboratory.core.sequence.Alphabet;

public class SequenceMismatchCollector {
    private final long[][] values;
    private final Alphabet alphabet;

    public SequenceMismatchCollector(Alphabet alphabet) {
        this.alphabet = alphabet;
        this.values = new long[alphabet.codesCount()][alphabet.codesCount()];
    }

    public void add(SequenceMismatch mm, long count) {
        values[mm.from][mm.to] += count;
    }

    public void add(byte from, byte to, long count) {
        values[from][to] += count;
    }

    public long get(byte from, byte to) {
        return values[from][to];
    }

    public String stringRepresentation() {
        StringBuilder sb = new StringBuilder();
        byte i, j;
        sb.append(' ');
        for (i = 0; i < alphabet.codesCount(); ++i)
            sb.append('\t').append(alphabet.symbolFromCode(i));
        sb.append('\n');
        for (i = 0; i < alphabet.codesCount(); ++i) {
            sb.append(alphabet.symbolFromCode(i));
            for (j = 0; j < alphabet.codesCount(); ++j)
                sb.append('\t').append(values[i][j]);
            sb.append('\n');
        }
        return sb.toString();
    }

    public String stringRepresentationNormalized(long[] fromAmounts, int cycles) {
        StringBuilder sb = new StringBuilder();
        byte i, j;
        sb.append(' ');
        for (i = 0; i < alphabet.codesCount(); ++i)
            sb.append('\t').append(alphabet.symbolFromCode(i));
        sb.append('\n');
        for (i = 0; i < alphabet.codesCount(); ++i) {
            sb.append(alphabet.symbolFromCode(i));
            for (j = 0; j < alphabet.codesCount(); ++j)
                sb.append('\t').append(((double) values[i][j]) / fromAmounts[i] / cycles);
            sb.append('\n');
        }
        return sb.toString();
    }
}
