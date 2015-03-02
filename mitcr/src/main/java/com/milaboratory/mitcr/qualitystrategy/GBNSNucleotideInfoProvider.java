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

/**
 * Implementation of {@link com.milaboratory.mitcr.vdjmapping.ntree.NucleotideInfoProvider} for {@link
 * GoodBadNucleotideSequence} to be used in core mappers ({@link com.milaboratory.mitcr.vdjmapping.tree.CoreVJSegmentMapper}).
 *
 * <p>This class is thread-unsafe.</p>
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class GBNSNucleotideInfoProvider implements NucleotideInfoProvider {
    private final NucleotideInfo info = new NucleotideInfo();
    private final GoodBadNucleotideSequence sequence;
    private final int size; //for performance
    private final byte direction;
    private int position;

    public GBNSNucleotideInfoProvider(GoodBadNucleotideSequence sequence) {
        this(sequence, +1);
    }

    public GBNSNucleotideInfoProvider(GoodBadNucleotideSequence sequence, int direction) {
        this.sequence = sequence;
        this.direction = (byte) direction;
        this.size = sequence.size();
    }

    public void resetPosition(int position) {
        this.position = position;
    }

    @Override
    public NucleotideInfo next() {
        if (position < 0 || position >= size)
            return null;
        info.bad = sequence.isBad(position);
        info.code = sequence.codeAt(position);
        position += direction;
        return info;
    }
}
