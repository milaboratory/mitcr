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
 * Group type of a segment.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public enum SegmentGroupType {
    Variable(0, 'V', +1), Diversity(2, 'D', 0), Joining(1, 'J', -1), Constant(3, 'C', -2);
    private int id;
    private char letter;
    private int cdr3Site;

    private SegmentGroupType(int id, char letter, int cdr3Site) {
        this.id = id;
        this.letter = letter;
        this.cdr3Site = cdr3Site;
    }

    /**
     * Gets the associated letter, e.g. V for TRBV
     */
    public char getLetter() {
        return letter;
    }

    /**
     * Id of segment
     */
    public int id() {
        return id;
    }

    /**
     * Gets an integer indicating position of segment of this type relative to CDR3
     *
     * @return +1 (upstream of CDR3, V gene), 0 (inside CDR3, D gene), -1 (downstream of CDR3, J gene) and -2
     *         (downstream of CDR3, C segment)
     */
    public int cdr3Site() {
        return cdr3Site;
    }

    /**
     * Gets a segment by id
     *
     * @param id
     */
    public static SegmentGroupType get(int id) {
        for (SegmentGroupType st : values())
            if (st.id == id)
                return st;
        throw new RuntimeException("Unknown ID");
    }
}
