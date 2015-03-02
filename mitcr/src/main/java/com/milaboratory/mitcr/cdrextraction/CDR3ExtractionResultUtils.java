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
package com.milaboratory.mitcr.cdrextraction;

import com.milaboratory.core.sequencing.read.SequencingRead;

public final class CDR3ExtractionResultUtils {
    private CDR3ExtractionResultUtils() {
    }

    /**
     * Trys to convert source field of this result to something serializable id. In case of {@link SequencingRead}
     * source will be converted to the Long received from {@link SequencingRead#id()}.
     *
     * <p>If such conversion is impossible source of returned result will be {@code null}.</p>
     *
     * @param result
     */
    @SuppressWarnings("unchecked")
    public static CDR3ExtractionResult makeSerializable(CDR3ExtractionResult result) {
        Object source = result.getSource();
        if (source instanceof Long)
            return result;

        if (source instanceof SequencingRead)
            source = ((SequencingRead) source).id();
        else // if (!(source instanceof Long))
            source = null;

        return result.substituteSource(source);
    }

    /**
     * Returns whether the source of {@link CDR3ExtractionResult} has id.
     */
    public static boolean hasSourceId(CDR3ExtractionResult result) {
        final Object source = result.getSource();
        return source instanceof Number || source instanceof SequencingRead;
    }

    /**
     * Extracts the ID of source sequencing read from any form of {@link CDR3ExtractionResult}.
     */
    public static long getSourceId(CDR3ExtractionResult result) {
        final Object source = result.getSource();

        if (source instanceof SequencingRead)
            return ((SequencingRead) source).id();
        else if (source instanceof Number)
            return ((Number) source).longValue();

        throw new IllegalArgumentException("Unsupported source type.");
    }
}
