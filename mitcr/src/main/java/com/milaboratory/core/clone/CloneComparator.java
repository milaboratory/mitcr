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
package com.milaboratory.core.clone;

import java.util.Comparator;

public final class CloneComparator implements Comparator<Clone> {
    public static final CloneComparator INSTANCE = new CloneComparator();

    private CloneComparator() {
    }

    @Override
    public int compare(Clone o1, Clone o2) {
        int v;
        if ((v = Long.compare(o1.getCount(), o2.getCount())) != 0)
            return -v;
        if ((v = o1.getCDR3().getSequence().compareTo(o2.getCDR3().getSequence())) != 0)
            return -v;
        return 0;
    }
}
