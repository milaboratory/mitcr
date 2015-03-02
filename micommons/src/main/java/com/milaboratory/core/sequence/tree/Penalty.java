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

package com.milaboratory.core.sequence.tree;

/**
 * Interface for penalty calculators.<br/>
 */
public interface Penalty {
    /**
     * Contract:<br/> 1. <b>Commutativity:</b> penalty(a,b) = penalty(b,a)<br/> 2. <b>Monotone nondecreasing:</b> a1
     * &lt; a2 &rArr; penalty(a1,b) &le; penalty(a2,b)
     *
     * @param quality0 first quality value
     * @param quality1 second quality value
     * @return penalty for mismatch
     */
    float penalty(int quality0, int quality1);

    float threshold(int length);
}
