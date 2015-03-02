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

import com.milaboratory.core.segment.Allele;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;

/**
 * Used to analyse behaviour of VJSegmentMappers. Implements methods to output VJSegmentMappingResult in a
 * human-readable pretty format.
 */
public class VJSegmentMappingResultFormatter {
    public static String format(NucleotideSequence sequence,
                                VJSegmentMappingResult result) {
        StringBuilder builder = new StringBuilder();
        printCoords(builder, sequence.size());
        builder.append("\n");
        builder.append(sequence.toString());
        builder.append("\n");

        Allele allele;
        NucleotideSequence allelesSequence;
        int i, position;
        for (int alleleId : result.getBarcode().getBits()) {
            allele = result.getContainer().getAllele(alleleId);
            allelesSequence = allele.getSequence();
            for (i = 0; i < sequence.size(); ++i) {
                position = i + allele.getReferencePointPosition() - result.getRefPoint();
                if (position < 0 || position >= allelesSequence.size()) {
                    builder.append(" ");
                    continue;
                }

                if (i > result.getSegmentBorderTo() || i < result.getSegmentBorderFrom())
                    builder.append(Character.toLowerCase(allelesSequence.charFromCodeAt(position)));
                else
                    builder.append(allelesSequence.charFromCodeAt(position));
            }
            builder.append("  -  ");
            builder.append(allele.getFullName());
            builder.append("\n");
        }

        return builder.toString();
    }

    private static void printCoords(StringBuilder builder, int max) {
        int step = 10;
        int current = 0;
        int next;
        String c;
        while (current < max) {
            builder.append('|');
            c = String.valueOf(current);
            builder.append(c);
            current += 1 + c.length();
            next = (current / step + 1) * step;
            builder.append(new String(new char[next - current]).replace("\0", " "));
            current = next;
        }
    }
}
