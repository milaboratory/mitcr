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
package com.milaboratory.mitcr.clsexport;

/**
 * Object used in serialization and deserialization of clone set to createInputStream of clone clusters}.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class CloneClusterData {
    private int offset, length, countCheck;

    public CloneClusterData(int offset, int length, int countCheck) {
        this.offset = offset;
        this.length = length;
        this.countCheck = countCheck;
    }

    public int getCountCheck() {
        return countCheck;
    }

    public int getLength() {
        return length;
    }

    public int getOffset() {
        return offset;
    }
}
