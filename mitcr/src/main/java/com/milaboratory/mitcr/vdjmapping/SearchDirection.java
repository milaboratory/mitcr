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

/**
 * Search direction in cDNA coordinates
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public enum SearchDirection {
    /**
     * Sense
     */
    BeginToEnd("5to3", "From 5' to 3'", +1),
    /**
     * Antisense
     */
    EndToBegin("3to5", "From 3' to 5'", -1);
    private String xmlRepresentation;
    private String name;
    private int direction;

    private SearchDirection(String xmlRepresentation, String name, int direction) {
        this.xmlRepresentation = xmlRepresentation;
        this.name = name;
        this.direction = direction;
    }

    /**
     * Serealizes search direction in XML string
     *
     * @return XML string with search direction
     */
    public String getXmlRepresentation() {
        return xmlRepresentation;
    }

    /**
     * Integer representation of direction
     *
     * @return +1 for sense or -1 for antisense
     */
    public int getDirection() {
        return direction;
    }

    /**
     * Deserealize search direction from XML string
     *
     * @param xml a XML string
     * @return search direction
     */
    public static SearchDirection fromXML(String xml) {
        for (SearchDirection s : values())
            if (s.xmlRepresentation.equals(xml))
                return s;
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
