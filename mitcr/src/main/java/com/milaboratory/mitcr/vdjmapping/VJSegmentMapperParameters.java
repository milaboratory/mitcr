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

import org.jdom.Element;

/**
 * An object to store parameters for {@link VJSegmentMapper}
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class VJSegmentMapperParameters {
    private int seedFrom, seedTo, minAlignmentMatches, lengthTolerance;
    private AlignmentDirection alignmentDirection;

    /**
     * An empty constructor. For compatibility with groovy named parameters syntax. E.g. new
     * VJSegmentMapperParameters(minAlignmentMatches: 12, seedFrom: -4, seedTo: 1, lengthTolerance: 3)
     */
    public VJSegmentMapperParameters() {
        alignmentDirection = AlignmentDirection.Both;
    }

    /**
     * Parameter set seedTo be used with uni-direction alignment
     *
     * @param alignmentDirection  alignment direction, should be used with uni-direction methods only
     * @param seedFrom            mandatory alignment start relative seedTo reference point of segment (i.e. minimal
     *                            length in reverse direction)
     * @param seedTo              mandatory alignment end relative seedTo reference point of segment (i.e. minimal
     *                            length in forward direction)
     * @param minAlignmentMatches minimal length of alignment (i.e. total minimal length)
     * @param lengthTolerance     if alignment is shorter than best alignment more than by {@code lengthTolerance} it
     *                            will not be reported
     */
    public VJSegmentMapperParameters(AlignmentDirection alignmentDirection, int seedFrom, int seedTo, int minAlignmentMatches, int lengthTolerance) {
        this.seedFrom = seedFrom;
        this.seedTo = seedTo;
        this.minAlignmentMatches = minAlignmentMatches;
        this.lengthTolerance = lengthTolerance;
        this.alignmentDirection = alignmentDirection;
    }

    /**
     * Deserializes {@link VJSegmentMapperParameters} to a XML element
     *
     * @param element a XML element
     * @return parameters object
     */
    public static VJSegmentMapperParameters fromXML(Element element) {
        return new VJSegmentMapperParameters(AlignmentDirection.fromXML(element.getChildTextTrim("extensionDirections")), Integer.decode(element.getChildTextTrim("seedFrom")),
                Integer.decode(element.getChildTextTrim("seedTo")),
                Integer.decode(element.getChildTextTrim("minAlignmentMatches")),
                Integer.decode(element.getChildTextTrim("lengthTolerance"))
        );
    }

    /**
     * Serializes {@link VJSegmentMapperParameters} to a XML element
     *
     * @param e a XML element add content to
     * @return e (just for convenience)
     */
    public Element asXML(Element e) {
        e.addContent(new Element("extensionDirections").setText(alignmentDirection.getXmlRepresentation()));
        e.addContent(new Element("seedFrom").setText(Integer.toString(seedFrom)));
        e.addContent(new Element("seedTo").setText(Integer.toString(seedTo)));
        e.addContent(new Element("minAlignmentMatches").setText(Integer.toString(minAlignmentMatches)));
        e.addContent(new Element("lengthTolerance").setText(Integer.toString(lengthTolerance)));
        return e;
    }

    /**
     * Sets the direction of alignment
     *
     * @param alignmentDirection direction of alignment
     */
    public void setAlignmentDirection(AlignmentDirection alignmentDirection) {
        this.alignmentDirection = alignmentDirection;
    }

    /**
     * Sets start of alignment for one-directional methods or mandatory length of alignment in reverse direction for
     * uni-directional methods
     *
     * @param seedFrom coordinate relative seedTo reference point of segment
     */
    public void setSeedFrom(int seedFrom) {
        this.seedFrom = seedFrom;
    }

    /**
     * Sets end of alignment for one-directional methods or mandatory length of alignment in forward direction for
     * uni-directional methods
     *
     * @param seedTo coordinate relative seedTo reference point of segment
     */
    public void setSeedTo(int seedTo) {
        this.seedTo = seedTo;
    }

    /**
     * Sets the length tolerance for reporting of segment alignment
     *
     * @param lengthTolerance length tolerance
     */
    public void setLengthTolerance(int lengthTolerance) {
        this.lengthTolerance = lengthTolerance;
    }

    /**
     * Sets total minimal length of alignment
     *
     * @param minAlignmentMatches total minimal length of alignment
     */
    public void setMinAlignmentMatches(int minAlignmentMatches) {
        this.minAlignmentMatches = minAlignmentMatches;
    }

    /**
     * Gets the direction of alignment
     *
     * @return direction of alignment
     */
    public AlignmentDirection getAlignmentDirection() {
        return alignmentDirection;
    }

    /**
     * Gets start of alignment for one-directional methods or mandatory length of alignment in reverse direction for
     * uni-directional methods
     *
     * @return coordinate relative seedTo reference point of segment
     */
    public int getSeedFrom() {
        return seedFrom;
    }

    /**
     * Gets the length tolerance for reporting of segment alignment
     *
     * @return length tolerance
     */
    public int getLengthTolerance() {
        return lengthTolerance;
    }

    /**
     * Gets total minimal length of alignment
     *
     * @return total minimal length of alignment
     */
    public int getMinAlignmentMatches() {
        return minAlignmentMatches;
    }

    /**
     * Gets end of alignment for one-directional methods or mandatory length of alignment in forward direction for
     * uni-directional methods
     *
     * @return coordinate relative seedTo reference point of segment
     */
    public int getSeedTo() {
        return seedTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VJSegmentMapperParameters that = (VJSegmentMapperParameters) o;

        if (lengthTolerance != that.lengthTolerance) return false;
        if (minAlignmentMatches != that.minAlignmentMatches) return false;
        if (seedFrom != that.seedFrom) return false;
        if (seedTo != that.seedTo) return false;
        if (alignmentDirection != that.alignmentDirection) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = seedFrom;
        result = 31 * result + seedTo;
        result = 31 * result + minAlignmentMatches;
        result = 31 * result + lengthTolerance;
        result = 31 * result + alignmentDirection.hashCode();
        return result;
    }
}
