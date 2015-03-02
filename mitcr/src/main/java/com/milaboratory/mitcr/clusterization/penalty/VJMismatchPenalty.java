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
package com.milaboratory.mitcr.clusterization.penalty;

import com.milaboratory.core.clone.Clone;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.mitcr.clusterization.PenaltyCalculator;

public class VJMismatchPenalty implements PenaltyCalculator {
    public static final VJMismatchPenalty VJ3N1_INSTANCE = new VJMismatchPenalty(1.26f, 0.9f, 0.35f, 3);
    public static final VJMismatchPenalty VJ3N0_INSTANCE = new VJMismatchPenalty(1.26f, 1.9f, 0.35f, 3);
    public static final VJMismatchPenalty VJ3N0B2_INSTANCE = new VJMismatchPenalty(1.26f, 1.9f, 0.35f, 2);
    private final float maxPenalty;
    private final float ndnPenalty;
    private final float vjPenalty;
    private final int vjMargin;

    public VJMismatchPenalty(float maxPenalty, float ndnPenalty, float vjPenalty, int vjMargin) {
        this.maxPenalty = maxPenalty;
        this.ndnPenalty = ndnPenalty;
        this.vjPenalty = vjPenalty;
        this.vjMargin = vjMargin;
    }

    @Override
    public int getMaxMismatches() {
        return 3;
    }

    @Override
    public float getMaxPenaltyValue() {
        return maxPenalty;
    }

    @Override
    public float getTotalPenalty(Clone clone0, Clone clone1) {
        //Extracting cdr3
        NucleotideSequence cdr30 = clone0.getCDR3().getSequence();
        NucleotideSequence cdr31 = clone1.getCDR3().getSequence();

        //Length
        final int length = cdr30.size();
        if (length != cdr31.size())
            throw new IllegalArgumentException();

        //Calculating V & J boundaries
        int lastVNuc = Math.max(clone0.getVEnd(), clone1.getVEnd());
        lastVNuc -= vjMargin;
        if (lastVNuc < 0)
            lastVNuc = 0;
        int firstJNuc = Math.min(clone0.getJStart(), clone1.getJStart());
        firstJNuc += vjMargin;
        if (firstJNuc >= length)
            firstJNuc = length - 1;

        //Calculating penalty
        int i = 0;
        float totalPenalty = 0;

        //Scanning V region
        for (; i <= lastVNuc; ++i) {
            if (cdr30.codeAt(i) != cdr31.codeAt(i))
                totalPenalty += vjPenalty;
            if (totalPenalty > maxPenalty)
                return totalPenalty; //this clone will be thrown away anyway
        }

        //Scanning NDN region
        for (; i < firstJNuc; ++i) {
            if (cdr30.codeAt(i) != cdr31.codeAt(i))
                totalPenalty += ndnPenalty;
            if (totalPenalty > maxPenalty)
                return totalPenalty; //this clone will be thrown away anyway
        }

        //Scanning J region
        for (; i < length; ++i) {
            if (cdr30.codeAt(i) != cdr31.codeAt(i))
                totalPenalty += vjPenalty;
            if (totalPenalty > maxPenalty)
                return totalPenalty; //this clone will be thrown away anyway
        }

        return totalPenalty;
    }
}
