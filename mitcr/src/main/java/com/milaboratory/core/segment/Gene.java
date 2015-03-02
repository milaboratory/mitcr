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
package com.milaboratory.core.segment;

/**
 * Enum of immunological genes. T and B cell receptors.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public enum Gene {
    TRA(0, Chain.Alpha, "TRA", false), TRB(1, Chain.Beta, "TRB", true),
    IGL(2, Chain.Lambda, "IGL", false), IGK(3, Chain.Kappa, "IGK", false), IGH(4, Chain.Heavy, "IGH", true),
    TRG(5, Chain.Gamma, "TRG", false), TRD(6, Chain.Delta, "TRD", true);
    private int id;
    private Chain chain;
    private String name;
    private boolean hasDSegment;

    private Gene(int id, Chain chain, String name, boolean hasDSegment) {
        this.id = id;
        this.chain = chain;
        this.name = name;
        this.hasDSegment = hasDSegment;
    }

    public static Gene getGene(Chain c) {
        switch (c) {
            case Alpha:
                return TRA;
            case Beta:
                return TRB;
        }
        throw new NullPointerException();
    }

    public static Gene get(int id) {
        switch (id) {
            case 0:
                return TRA;
            case 1:
                return TRB;
        }
        return null;
    }

    public static Gene fromXML(String xml) {
        for (Gene g : values())
            if (xml.equalsIgnoreCase(g.name))
                return g;
        return null;
    }

    public int id() {
        return id;
    }

    public Chain chain() {
        return chain;
    }

    public boolean hasDSegment() {
        return hasDSegment;
    }

    public String getXmlRepresentation() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
