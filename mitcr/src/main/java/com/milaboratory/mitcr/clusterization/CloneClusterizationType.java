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
package com.milaboratory.mitcr.clusterization;

import com.milaboratory.mitcr.clusterization.penalty.NoClusterizationPenalty;
import com.milaboratory.mitcr.clusterization.penalty.OneMismatchPenaltyCalculator;
import com.milaboratory.mitcr.clusterization.penalty.VDJExplicitPenalty;
import com.milaboratory.mitcr.clusterization.penalty.VJMismatchPenalty;

import java.util.HashMap;
import java.util.Map;

/**
 * Type of clusterization algorithm
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public enum CloneClusterizationType {
    /**
     * Clusterizes clones allowing one mismatch in CDR3
     */
    OneMismatch("One Mismatch", "oneMismatch", OneMismatchPenaltyCalculator.INSTANCE),
    /**
     * Clusterizes clones allowing one mismatch in VD/DJ junction regions in total and three mismatches in V and J
     * regions in total
     */
    VJ3N1Priority("VJ3 N1 Priority", "vj3n1Priority", VJMismatchPenalty.VJ3N1_INSTANCE),
    /**
     * Clusterizes clones allowing no mismatches in VD/DJ junction regions in total and three mismatches in V and J
     * regions in total
     */
    VJ3N0Priority("VJ3 N0 Priority", "vj3n0Priority", VJMismatchPenalty.VJ3N0_INSTANCE),
    /**
     * Clusterizes clones allowing two mismatches in V region, two mismatches in J region and one mismatch in D region,
     * but no more than 3 mismatches in total
     */
    //EJI
    V2D1J2T3Explicit("V2 D1 J2 T3 EXPLICIT", "v2d1j2t3Explicit", VDJExplicitPenalty.V2D1J2T3_INSTANCE),
    /**
     * No clusterization is applied
     */
    None("None", "none", NoClusterizationPenalty.INSTANCE);
    private static Map<String, CloneClusterizationType> xmlMap;
    private String name;
    private String xmlRepresentation;
    private final PenaltyCalculator penaltyCalculator;

    private CloneClusterizationType(String name, String xmlRepresentation,
                                    PenaltyCalculator penaltyCalculator) {
        this.name = name;
        this.xmlRepresentation = xmlRepresentation;
        this.penaltyCalculator = penaltyCalculator;
    }

    public PenaltyCalculator getPenaltyCalculator() {
        return penaltyCalculator;
    }

    public String getName() {
        return name;
    }

    public String getXmlRepresentation() {
        return xmlRepresentation;
    }

    public static CloneClusterizationType fromXML(String string) {
        if (xmlMap == null) {
            xmlMap = new HashMap<String, CloneClusterizationType>();
            for (CloneClusterizationType v : values())
                xmlMap.put(v.xmlRepresentation, v);
        }
        return xmlMap.get(string);
    }

    @Override
    public String toString() {
        return name;
    }
}
