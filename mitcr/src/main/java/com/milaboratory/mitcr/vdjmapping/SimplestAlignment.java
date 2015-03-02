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
package com.milaboratory.mitcr.vdjmapping;

import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;

/**
 * Used to align D genes.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class SimplestAlignment {
    public int queryFrom = 0, targetFrom = 0, length = -1;

    public static SimplestAlignment build(NucleotideSequence target, NucleotideSequence query, final int minLength) {
        if (target.size() < minLength || query.size() < minLength)
            //Alignment is impossible
            return null;
        final int shiftFrom = minLength - target.size();
        final int shiftTo = query.size() - minLength;
        int from, to, i, lastMismatch, length;
        SimplestAlignment alignment = new SimplestAlignment();
        for (int shift = shiftFrom; shift <= shiftTo; ++shift) {
            from = Math.max(0, shift);
            to = Math.min(target.size() + shift, query.size());
            lastMismatch = from - 1;
            for (i = from; i < to; ++i) {
                if (target.codeAt(i - shift) != query.codeAt(i))
                    lastMismatch = i;
                if ((length = i - lastMismatch) >= minLength && alignment.length < length) {
                    alignment.length = length;
                    alignment.queryFrom = lastMismatch + 1;
                    alignment.targetFrom = lastMismatch - shift + 1;
                }
            }
        }

        if (alignment.length == -1)
            return null;
        return alignment;

        //int qCoordStart = -query.size() + minLength;
        //int qCoordEnd = target.size() - minLength;
        //int tStart, tEnd;
        //int lastWrong = 0;
        //SimplestAlignment alignment = new SimplestAlignment();
        //for (int qCoord = qCoordStart; qCoord <= qCoordEnd; ++qCoord) {
        //    tStart = Math.max(0, qCoord);
        //    tEnd = Math.min(target.size() - 1, qCoord + query.size() - 1);
        //    lastWrong = tStart - 1;
        //    for (int tCoord = tStart; tCoord <= tEnd; ++tCoord) {
        //        if (target.codeAt(tCoord) != query.codeAt(tCoord - qCoord))
        //            lastWrong = tCoord;
        //        if (alignment.length < (tCoord - lastWrong)) {
        //            alignment.length = tCoord - lastWrong;
        //            alignment.targetFrom = lastWrong + 1;
        //            alignment.queryFrom = lastWrong - qCoord + 1;
        //        }
        //    }
        //}
        //return alignment;
    }

    //public static SimplestAlignment build(NucleotideSequence target, NucleotideSequence query, int minLength) {
    //    int qCoordStart = -query.size() + minLength;
    //    int qCoordEnd = target.size() - minLength;
    //    int tStart, tEnd;
    //    int lastWrong = 0;
    //    SimplestAlignment alignment = new SimplestAlignment();
    //    for (int qCoord = qCoordStart; qCoord <= qCoordEnd; ++qCoord) {
    //        tStart = Math.max(0, qCoord);
    //        tEnd = Math.min(target.size() - 1, qCoord + query.size() - 1);
    //        lastWrong = tStart - 1;
    //        for (int tCoord = tStart; tCoord <= tEnd; ++tCoord) {
    //            if (target.codeAt(tCoord) != query.codeAt(tCoord - qCoord))
    //                lastWrong = tCoord;
    //            if (alignment.length < (tCoord - lastWrong)) {
    //                alignment.length = tCoord - lastWrong;
    //                alignment.targetFrom = lastWrong + 1;
    //                alignment.queryFrom = lastWrong - qCoord + 1;
    //            }
    //        }
    //    }
    //    return alignment;
    //}
}
