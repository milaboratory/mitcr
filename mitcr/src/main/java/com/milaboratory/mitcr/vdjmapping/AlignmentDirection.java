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

import com.milaboratory.core.segment.SegmentGroupType;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that holds the specified alignment direction
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public enum AlignmentDirection {
    /**
     * Align inside CDR3 Cys-&gt;... or ...&lt;-Phe only
     */
    InsideCDR3(+1, "insideCDR3", "Inside CDR3 only", false),
    /**
     * Align outside of CDR3 ...&lt;-Cys or Phe-&gt;... only
     */
    OutsideCDR3(-1, "outsideCDR3", "Outside CDR3 only", false),
    /**
     * Align in both directions ...&lt;-Cys-&gt... or ...&lt;-Phe-&gt;... only
     */
    Both(0, "both", "Both directions", true);
    /**
     * Refines V and J gene determination after running {@code AlignmentDirection.InsideCDR3}
     */
    //Smart(0, "smart", "Smart aligner", true),
    /**
     * Refines V and J gene determination after running {@code AlignmentDirection.OutsideCDR3}
     */
    //SmartV2(0, "smartV2", "Smart aligner V2", true);

    private static Map<String, AlignmentDirection> xmlMap;
    private int directionFactor;
    private boolean isBoth;
    private String xmlRepresentation;
    private String name;

    private AlignmentDirection(int directionFactor, String xmlRepresentation, String name, boolean isBoth) {
        this.directionFactor = directionFactor;
        this.xmlRepresentation = xmlRepresentation;
        this.name = name;
        this.isBoth = isBoth;
    }

    /**
     * Used internally
     */
    public int getDirectionFactor() {
        return directionFactor;
    }

    /**
     * Used internally
     */
    public int getDirection(SegmentGroupType type) {
        return directionFactor * type.cdr3Site();
    }

    /**
     * Writes {@link AlignmentDirection} object as XML-formatted string
     *
     * @return a XML-formatted string containing {@link AlignmentDirection} object
     */
    public String getXmlRepresentation() {
        return xmlRepresentation;
    }

    /**
     * Tells if alignment should be performed in both direction
     *
     * @return {@code true} if alignment should be performed in both direction
     */
    public boolean isBoth() {
        return isBoth;
    }

    /**
     * Extracts {@link AlignmentDirection} from XML-formatted string
     *
     * @param xml a XML-formatted string
     * @return {@link AlignmentDirection} object
     */
    public static AlignmentDirection fromXML(String xml) {
        return xmlMap.get(xml);
    }

    static {
        xmlMap = new HashMap<>();
        for (AlignmentDirection a : values())
            xmlMap.put(a.xmlRepresentation, a);
    }

    @Override
    public String toString() {
        return name;
    }
}
