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
 * Tuple of species and segment group.<br/> Used as a key in {@link SegmentLibrary}.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class SpeciesSegmentGroup {
    public final Species species;
    public final SegmentGroup group;

    public SpeciesSegmentGroup(Species species, SegmentGroup group) {
        if (species == null || group == null)
            throw new NullPointerException();
        this.species = species;
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpeciesSegmentGroup that = (SpeciesSegmentGroup) o;

        if (group != that.group) return false;
        if (species != that.species) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = species.hashCode();
        result = 31 * result + group.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return species.shortName + "_" + group.getName();
    }
}
