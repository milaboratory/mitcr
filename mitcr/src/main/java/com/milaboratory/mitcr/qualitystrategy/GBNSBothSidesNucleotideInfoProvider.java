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
package com.milaboratory.mitcr.qualitystrategy;

import com.milaboratory.mitcr.vdjmapping.ntree.NucleotideInfo;
import com.milaboratory.mitcr.vdjmapping.ntree.NucleotideInfoProvider;

public final class GBNSBothSidesNucleotideInfoProvider implements NucleotideInfoProvider {
    private final NucleotideInfo info = new NucleotideInfo();
    private final GoodBadNucleotideSequence sequence;
    private final int size; //for performance
    private int position = 0;

    public GBNSBothSidesNucleotideInfoProvider(GoodBadNucleotideSequence sequence) {
        this.sequence = sequence;
        this.size = sequence.size();
    }

    @Override
    public NucleotideInfo next() {
        if (position == size)
            return null;
        int coord = ++position;
        coord = ((coord & 1) == 1) ? coord >> 1 : size - (coord >> 1);
        info.bad = sequence.isBad(coord);
        info.code = sequence.codeAt(coord);
        return info;
    }
}
