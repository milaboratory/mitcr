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

/**
 * This penalty calculator allows to explicitly set number of allowed mismatches in V, D, and J regions. Also it is
 * possible to limit total number of allowed mismatches and V, D, J genes margins.
 */
public class VDJExplicitPenalty implements PenaltyCalculator {
    public static final VDJExplicitPenalty V2D1J2T3_INSTANCE = new VDJExplicitPenalty(2, 1, 2, 3, 3, 3);
    private final int maxVMismatches, maxDMismatches, maxJMismatches;
    private final int maxTotalMismatches;
    private final int vjMargin, dMargin;

    public VDJExplicitPenalty(int maxVMismatches, int maxDMismatches, int maxJMismatches, int maxTotalMismatches, int vjMargin, int dMargin) {
        this.maxVMismatches = maxVMismatches;
        this.maxDMismatches = maxDMismatches;
        this.maxJMismatches = maxJMismatches;
        this.maxTotalMismatches = maxTotalMismatches;
        this.vjMargin = vjMargin;
        this.dMargin = dMargin;
    }

    @Override
    public int getMaxMismatches() {
        return maxTotalMismatches;
    }

    @Override
    public float getMaxPenaltyValue() {
        return 1.0f;
    }

    @Override
    public float getTotalPenalty(Clone clone0, Clone clone1) {
        //Forcing clone0 to have mav length of D region
        if ((clone1.isDSegmentDetermined() && (!clone0.isDSegmentDetermined())) || //D was determined in clone1 but not determined in clone0
                (clone1.isDSegmentDetermined() && clone0.isDSegmentDetermined() && clone1.getDLength() > clone0.getDLength())) { //clone0 has more long D segment
            //Swapping clone0 and clone1
            final Clone tClone = clone0;
            clone0 = clone1;
            clone1 = tClone;
        }

        //clone0 supposed to be central

        //Extracting cdr3s
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

        //Calculating D boundaries
        boolean dFound = clone0.isDSegmentDetermined();
        int firstDNuc = -1, lastDNuc = -1;
        if (dFound) {
            firstDNuc = clone0.getDStart() + vjMargin;
            lastDNuc = clone0.getDEnd() - vjMargin;
            if (firstDNuc > lastDNuc)
                dFound = false;
        }

        //Calculating penalty
        int i = 0;
        int mm = 0;

        //Additional check (redundant)
        int tmm = 0;

        //Scanning V region
        for (; i <= lastVNuc; ++i) {
            if (cdr30.codeAt(i) != cdr31.codeAt(i)) {
                ++mm;
                ++tmm;
            }
            if (mm > maxVMismatches)
                return 2.0f; //this clone will be thrown away anyway
        }
        mm = 0;

        if (dFound) {
            //Scanning V-D. No mismatches allowed
            for (; i < firstDNuc; ++i)
                if (cdr30.codeAt(i) != cdr31.codeAt(i))
                    return 2.0f;

            //Scanning D region
            for (i = firstDNuc; i <= lastDNuc; ++i) {
                if (cdr30.codeAt(i) != cdr31.codeAt(i)) {
                    ++mm;
                    ++tmm;
                }
                if (mm > maxDMismatches)
                    return 2.0f; //this clone will be thrown away anyway
            }
            mm = 0;
        }

        //Scanning D-J or V-J. No mismatches allowed
        for (; i < firstJNuc; ++i)
            if (cdr30.codeAt(i) != cdr31.codeAt(i))
                return 2.0f;

        for (; i < length; ++i) {
            if (cdr30.codeAt(i) != cdr31.codeAt(i)) {
                ++mm;
                ++tmm;
            }
            if (mm > maxJMismatches)
                return 2.0f; //this clone will be thrown away anyway
        }

        //Redundant check
        if (tmm > maxTotalMismatches)
            return 2.0f;

        return 0.0f;
    }
}
