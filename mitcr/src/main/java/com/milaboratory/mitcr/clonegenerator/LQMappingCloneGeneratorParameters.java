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
 * A set of parameters for low quality mapping clone generator
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class LQMappingCloneGeneratorParameters extends BasicCloneGeneratorParameters {
    private int maxErrorsInBadPoints;
    private boolean proportionalMapping;

    /**
     * Constructor with default parameters
     */
    public LQMappingCloneGeneratorParameters() {
        this(3, true);
    }

    /**
     * Creates a set of parameters for low quality mapping clone generator
     *
     * @param maxErrorsInBadPoints maximal number of allowed errors in bad points
     * @param proportionalMapping  determines LQ mapping target selection strategy (false - map to the biggest possible
     *                             clone, true - choose target with weight equal to clone sequence count)
     */
    public LQMappingCloneGeneratorParameters(int maxErrorsInBadPoints, boolean proportionalMapping) {
        this.maxErrorsInBadPoints = maxErrorsInBadPoints;
        this.proportionalMapping = proportionalMapping;
    }

    /**
     * Creates a set of parameters for low quality mapping clone generator
     *
     * @param maxErrorsInBadPoints maximal number of allowed errors in bad points
     */
    public LQMappingCloneGeneratorParameters(int maxErrorsInBadPoints) {
        this(maxErrorsInBadPoints, true);
    }


    /**
     * Creates a set of parameters for low quality mapping clone generator
     *
     * @param segmentInformationAggregationFactor
     *                             collect only segments with frequency not less than segmentInformationAggregationFactor
     *                             of maximal
     * @param maxErrorsInBadPoints maximal number of allowed errors in bad points
     */
    public LQMappingCloneGeneratorParameters(float segmentInformationAggregationFactor, int maxErrorsInBadPoints, boolean proportionalMapping) {
        super(segmentInformationAggregationFactor);
        this.maxErrorsInBadPoints = maxErrorsInBadPoints;
        this.proportionalMapping = proportionalMapping;
    }

    /**
     * Creates a set of parameters for low quality mapping clone generator
     *
     * @param segmentInformationAggregationFactor
     *                             collect only segments with frequency not less than segmentInformationAggregationFactor
     *                             of maximal
     * @param maxErrorsInBadPoints maximal number of allowed errors in bad points
     */
    public LQMappingCloneGeneratorParameters(AccumulatorType accumulatorType, float segmentInformationAggregationFactor, int maxErrorsInBadPoints, boolean proportionalMapping) {
        super(accumulatorType, segmentInformationAggregationFactor);
        this.maxErrorsInBadPoints = maxErrorsInBadPoints;
        this.proportionalMapping = proportionalMapping;
    }


    public int getMaxErrorsInBadPoints() {
        return maxErrorsInBadPoints;
    }

    public void setMaxErrorsInBadPoints(int maxErrorsInBadPoints) {
        this.maxErrorsInBadPoints = maxErrorsInBadPoints;
    }

    public boolean isProportionalMapping() {
        return proportionalMapping;
    }

    public void setProportionalMapping(boolean proportionalMapping) {
        this.proportionalMapping = proportionalMapping;
    }

    @Override
    public Element asXML(Element e) {
        final Element element = super.asXML(e)
                .addContent(new Element("maxErrorsInBadPoints").setText(String.valueOf(maxErrorsInBadPoints)));
        if (proportionalMapping)
            element.addContent(new Element("proportionalMapping"));
        return element;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        LQMappingCloneGeneratorParameters that = (LQMappingCloneGeneratorParameters) o;

        if (maxErrorsInBadPoints != that.maxErrorsInBadPoints) return false;

        return proportionalMapping == that.proportionalMapping;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + maxErrorsInBadPoints;
        result = 31 * result + (proportionalMapping ? 7 : 3);
        return result;
    }
}
