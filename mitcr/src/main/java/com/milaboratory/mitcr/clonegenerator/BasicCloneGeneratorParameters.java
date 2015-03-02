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
 * Basic set of parameters for clone generator
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
//TODO documentation
public class BasicCloneGeneratorParameters implements CloneGeneratorParameters {
    private float segmentInformationAggregationFactor = .15f;
    private AccumulatorType accumulatorType = AccumulatorType.MaxStrict;

    public BasicCloneGeneratorParameters() {
    }

    public BasicCloneGeneratorParameters(float segmentInformationAggregationFactor) {
        this.segmentInformationAggregationFactor = segmentInformationAggregationFactor;
    }

    /**
     * Creates a basic set of parameters for clone generator
     *
     * @param segmentInformationAggregationFactor
     *         collect only segments with frequency not less than segmentInformationAggregationFactor of maximal
     */
    public BasicCloneGeneratorParameters(AccumulatorType accumulatorType, float segmentInformationAggregationFactor) {
        this.accumulatorType = accumulatorType;
        this.segmentInformationAggregationFactor = segmentInformationAggregationFactor;
    }

    public float getSegmentInformationAggregationFactor() {
        return segmentInformationAggregationFactor;
    }

    public void setSegmentInformationAggregationFactor(float segmentInformationAggregationFactor) {
        this.segmentInformationAggregationFactor = segmentInformationAggregationFactor;
    }

    public AccumulatorType getAccumulatorType() {
        return accumulatorType;
    }

    public void setAccumulatorType(AccumulatorType accumulatorType) {
        this.accumulatorType = accumulatorType;
    }

    public Element asXML() {
        return asXML(new Element("cloneGenerator"));
    }

    @Override
    public Element asXML(Element e) {
        e.addContent(new Element("segmentInformationAggregationFactor").setText(String.valueOf(segmentInformationAggregationFactor)));
        if (accumulatorType == AccumulatorType.AvrgCompressed || accumulatorType == AccumulatorType.AvrgStrict)
            e.addContent(new Element("averageQuality"));
        if (accumulatorType == AccumulatorType.MaxCompressed || accumulatorType == AccumulatorType.AvrgCompressed)
            e.addContent(new Element("compressedSegmentsStatistics"));
        return e;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BasicCloneGeneratorParameters that = (BasicCloneGeneratorParameters) o;

        if (Float.compare(that.segmentInformationAggregationFactor, segmentInformationAggregationFactor) != 0)
            return false;

        return accumulatorType == that.accumulatorType;
    }

    @Override
    public int hashCode() {
        return (segmentInformationAggregationFactor != +0.0f ? Float.floatToIntBits(segmentInformationAggregationFactor) : 0);
    }
}
