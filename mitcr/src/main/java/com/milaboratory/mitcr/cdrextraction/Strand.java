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

import java.util.HashMap;
import java.util.Map;

/**
 * Strand to perform search on.
 */
public enum Strand {
    Forward(0, "Forward", "forward", true, false),
    ReverseComplement(1, "Reverse Complement", "reverseComplement", false, true),
    Both(-1, "Both", "both", true, true);
    private static Map<String, Strand> xmlMap;
    private int id;
    private final boolean forward, reverse;
    private String name;
    private String xmlRepresentation;

    private Strand(int id, String name, String xmlRepresentation,
                   boolean forward, boolean reverse) {
        this.id = id;
        this.name = name;
        this.xmlRepresentation = xmlRepresentation;
        this.forward = forward;
        this.reverse = reverse;
    }

    public String getXmlRepresentation() {
        return xmlRepresentation;
    }

    public int id() {
        return id;
    }

    public boolean isForward() {
        return forward;
    }

    public boolean isReverse() {
        return reverse;
    }

    public static Strand fromXML(String xml) {
        return xmlMap.get(xml);
    }

    @Override
    public String toString() {
        return name;
    }

    static {
        xmlMap = new HashMap<String, Strand>();
        for (Strand s : values())
            xmlMap.put(s.xmlRepresentation, s);
    }
}
