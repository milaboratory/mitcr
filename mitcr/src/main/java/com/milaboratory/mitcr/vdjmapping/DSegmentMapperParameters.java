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
 * Parameters for D segment mapper
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class DSegmentMapperParameters {
    private int minLength;
    /**
     * If true also search for reverse-complement sequence of D segments
     */
    private boolean searchRC = true;

    /**
     * Creates and object that stores parameters for D segment mapper
     *
     * @param minLength minimal length of mapped fragment
     * @param searchRC  if true also search for reverse-complement sequence of D segments
     */
    public DSegmentMapperParameters(int minLength, boolean searchRC) {
        this.minLength = minLength;
        this.searchRC = searchRC;
    }

    /**
     * Creates and object that stores parameters for D segment mapper
     *
     * @param minLength minimal length of mapped fragment
     */
    public DSegmentMapperParameters(int minLength) {
        this.minLength = minLength;
    }

    /**
     * Creates and object that stores parameters for D segment mapper from a XML element
     *
     * @param e a corresponding XML element
     */
    public static DSegmentMapperParameters fromXML(Element e) {
        if (e == null)
            return null;
        return new DSegmentMapperParameters(Integer.decode(e.getChildTextTrim("minLength")),
                e.getChild("searchForRC") != null);
    }

    /**
     * Stores D segment mapper parameters in a XML element
     *
     * @param e a XML element to update
     * @return updated XML element
     */
    public Element asXML(Element e) {
        e.addContent(new Element("minLength").setText(Integer.toString(minLength)));
        if (searchRC)
            e.addContent(new Element("searchForRC"));
        return e;
    }

    /**
     * Gets minimal length of mapped fragment
     *
     * @return minimal length of mapped fragment
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * Returns true if segment mapper will search for reverse-complement sequence of the segment.
     */
    public boolean isSearchRC() {
        return searchRC;
    }

    /**
     * If true is set segment mapper will search for reverse-complement sequence of the segment.
     */
    public void setSearchRC(boolean searchRC) {
        this.searchRC = searchRC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DSegmentMapperParameters that = (DSegmentMapperParameters) o;

        if (minLength != that.minLength) return false;

        return searchRC == that.searchRC;
    }

    @Override
    public int hashCode() {
        return minLength * 31 + (searchRC ? 1 : 0);
    }
}
