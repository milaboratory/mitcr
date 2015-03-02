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
package com.milaboratory.core.sequence.motif;

import com.milaboratory.util.BitArray;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public interface LowQualityIndicator {
    boolean isLowQuality(int point);

    public static class Utils {
        /**
         * true = low quality, false = goodQuality
         *
         * @param bitArray information source
         */
        public static LowQualityIndicator wrap(final BitArray bitArray) {
            return new LowQualityIndicator() {
                @Override
                public boolean isLowQuality(int point) {
                    return bitArray.get(point);
                }
            };
        }

        /**
         * '.' = low quality, other chars = good quality
         *
         * @param points points (ex. "---..----.---.--..  ")
         */
        public static LowQualityIndicator wrap(String points) {
            BitArray ba = new BitArray(points.length());
            for (int i = 0; i < points.length(); ++i)
                if (points.charAt(i) == '.')
                    ba.set(i);
            return wrap(ba);
        }
    }
}
