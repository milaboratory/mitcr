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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A central clone with children clones (leaves)
 *
 * @param <T>
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class MonolayerCluster<T> {
    public T center;
    public List<T> leaves;

    public MonolayerCluster(T center) {
        this.center = center;
    }

    public void addLeaf(T element) {
        if (leaves == null)
            leaves = new ArrayList<T>();
        leaves.add(element);
    }

    public List<T> getLeaves() {
        if (leaves == null)
            return Collections.EMPTY_LIST;
        return leaves;
    }
}
