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
package com.milaboratory.mitcr.clonegenerator;

import org.jdom.Element;

/**
 * A class providing method to create {@link CloneGeneratorParameters} from XML.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class CloneGeneratorParametersDeserializer {
    private CloneGeneratorParametersDeserializer() {
    }

    public static CloneGeneratorParameters fromXML(Element e) {
        Element mb = e.getChild("maxErrorsInBadPoints");
        AccumulatorType accumulatorType = AccumulatorType.get(e.getChild("compressedSegmentsStatistics") != null,
                e.getChild("averageQuality") != null);
        boolean filterOff = (e.getChild("filterOffLQReads") != null);
        float baf = Float.valueOf(e.getChildTextTrim("segmentInformationAggregationFactor"));
        if (mb == null) {
            if (filterOff)
                return new LQFilteringOffCloneGeneratorParameters(accumulatorType, baf);
            else
                return new BasicCloneGeneratorParameters(accumulatorType, baf);
        }
        if (filterOff)
            throw new IllegalArgumentException("FilterOffLQReads flag is not compatible with maxErrorsInBadPoints parameters.");

        return new LQMappingCloneGeneratorParameters(accumulatorType, baf, Integer.decode(mb.getTextTrim()),
                e.getChild("proportionalMapping") != null);
    }
}
