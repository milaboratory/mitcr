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

import com.milaboratory.core.clone.Clone;

/**
 * Penalization scheme for clone clustering
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public interface PenaltyCalculator {
    /**
     * Gets the maximum number of allowed mismatches
     *
     * @return the maximum number of allowed mismatches
     */
    int getMaxMismatches();

    /**
     * Gets the maximum allowed penalty value
     *
     * @return the maximum allowed penalty value
     */
    float getMaxPenaltyValue();

    /**
     * Get the penalty for agglomerating two clones
     *
     * @param clone0 first clone
     * @param clone1 second clone
     * @return the penalty for agglomerating two clones
     */
    float getTotalPenalty(Clone clone0, Clone clone1);
}
