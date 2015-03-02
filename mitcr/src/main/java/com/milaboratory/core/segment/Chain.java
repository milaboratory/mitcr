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

public enum Chain {
    Alpha("a", 0), Beta("b", 1), Lambda("l", 2), Kappa("k", 3), Heavy("h", 4),
    Gamma("g", 5), Delta("d", 6);
    private String suffix;
    private int id;

    private Chain(String suffix, int id) {
        this.suffix = suffix;
        this.id = id;
    }

    public String getSuffix() {
        return suffix;
    }

    public int id() {
        return id;
    }
}
