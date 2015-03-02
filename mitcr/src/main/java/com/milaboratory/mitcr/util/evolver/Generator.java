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
package com.milaboratory.mitcr.util.evolver;

import java.util.List;

/**
 * Main abstract interface evolver infrastructure.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public interface Generator<I, C> {
    /**
     * Creates new generation from previous one.
     *
     * @param currentGeneration elements of current generation
     * @param nextGeneration    list to add elements from new generation (must be empty)
     * @param condition         condition
     */
    void generate(List<I> currentGeneration, List<I> nextGeneration, C condition);
}
