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
 * Species enum.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public enum Species {
    HomoSapiens(0, "Homo sapiens", "hs"), MusMusculus(1, "Mus musculus", "mm");
    public final int index;
    public final String name, shortName;

    private Species(int index, String name, String shortName) {
        this.index = index;
        this.name = name;
        this.shortName = shortName;
    }

    //TODO: better algorithm considered
    public static Species getSpeciesByIndex(int index) {
        for (Species s : values())
            if (s.index == index)
                return s;
        return null;
    }

    public static Species getFromShortName(String name) {
        for (Species s : values())
            if (s.shortName.equalsIgnoreCase(name))
                return s;
        return null;

    }
}
