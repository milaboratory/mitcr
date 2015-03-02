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
package com.milaboratory.util;

import java.io.Serializable;
import java.util.Arrays;

public final class Bit2Array implements Serializable {
    private static final long serialVersionUID = 1L;

    byte[] data;
    int size;

    public Bit2Array(int length) {
        this.size = length;
        data = new byte[(length + 3) >> 2];
    }

    Bit2Array(int size, byte[] data) {
        //if (data.length != ((size + 3) >> 2))
        //    throw new IllegalArgumentException();
        this.size = size;
        this.data = data;
    }

    public int size() {
        return size;
    }

    public int get(int index) {
        return (data[index >> 2] >>> ((index & 3) << 1)) & 0x3;
    }

    public void set(int index, int value) {
        data[index >> 2] &= ~(0x3 << ((index & 3) << 1));
        data[index >> 2] |= (value & 0x3) << ((index & 3) << 1);
    }

    public Bit2Array clone() {
        return new Bit2Array(size, Arrays.copyOf(data, data.length));
    }

    public void copyFrom(Bit2Array other, int otherOffset, int thisOffset, int length) {
        if (thisOffset < 0 || thisOffset + length > size ||
                otherOffset < 0 || otherOffset + length > other.size)
            throw new IndexOutOfBoundsException();

        //TODO optimize
        for (int i = 0; i < length; ++i)
            set(thisOffset + i, other.get(otherOffset + i));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Arrays.hashCode(this.data);
        hash = 47 * hash + this.size;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Bit2Array other = (Bit2Array) obj;
        if (this.size != other.size)
            return false;
        if (!Arrays.equals(this.data, other.data))
            return false;
        return true;
    }

    //TODO optimize
    public Bit2Array getRange(int from, int to) {
        if (from < 0 || (from >= size && size != 0)
                || to < from || to > size)
            throw new IndexOutOfBoundsException();

        Bit2Array ret = new Bit2Array(to - from);
        int i = 0;
        for (int j = from; j < to; ++j, ++i)
            ret.set(i, get(j));
        return ret;
    }

    /*public static Bit2Array wrap(byte[] data, int size) {
        return new Bit2Array(size, data);
    }*/

    public static byte[] extractRawDataArray(Bit2Array array) {
        return array.data;
    }

    public static Bit2Array construct(int size, byte[] data) {
        return new Bit2Array(size, data);
    }
}
