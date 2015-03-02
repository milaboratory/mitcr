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

public class BitArray implements Serializable {
    private static final long serialVersionUID = 1L;

    byte[] data;
    int size;

    public BitArray(int size) {
        this.size = size;
        this.data = new byte[(size + 7) >> 3];
    }

    BitArray(byte[] data, int size) {
        //if(data.length != ((size + 7) >> 3))
        //    throw new IllegalStateException();
        this.data = data;
        this.size = size;
    }

    public boolean get(int i) {
        return (data[i >> 3] & (1 << (i & 7))) != 0;
    }

    public void set(int i) {
        data[i >> 3] |= (1 << (i & 7));
    }

    public void clear(int i) {
        data[i >> 3] &= ~(1 << (i & 7));
    }

    public void set(int i, boolean value) {
        if (value)
            set(i);
        else
            clear(i);
    }

    //TODO Equals to load values
    public void set(BitArray ba) {
        if (ba.size != this.size)
            throw new IllegalArgumentException();
        for (int i = 0; i < this.data.length; ++i)
            this.data[i] = ba.data[i];
    }

    public void setAll() {
        //for (int i = 0; i < data.length; ++i)
        //    data[i] = 0xFFFFFFFF;
        Arrays.fill(data, (byte) 0xFF);
        if ((size & 7) != 0)
            data[data.length - 1] = (byte) (0xFF >>> (8 - (size & 0x7)));
    }

    public boolean intersects(BitArray bitArray) {
        if (bitArray.size != this.size)
            throw new IllegalArgumentException();
        for (int i = 0; i < this.data.length; ++i)
            if ((this.data[i] & bitArray.data[i]) != 0)
                return true;
        return false;
    }

    public int bitCount() {
        int count = 0;
        for (int i = 0; i < data.length; ++i)
            count += Integer.bitCount(0xFF & data[i]);
        return count;
    }

    public void or(BitArray bitArray) {
        if (size != bitArray.size)
            throw new IllegalArgumentException();
        for (int i = 0; i < data.length; ++i)
            data[i] |= bitArray.data[i];
    }

    public void xor(BitArray bitArray) {
        if (size != bitArray.size)
            throw new IllegalArgumentException();
        for (int i = 0; i < data.length; ++i)
            data[i] ^= bitArray.data[i];
    }

    public void and(BitArray bitArray) {
        if (size != bitArray.size)
            throw new IllegalArgumentException();
        for (int i = 0; i < data.length; ++i)
            data[i] &= bitArray.data[i];
    }

    public void loadValueFrom(BitArray bitArray) {
        if (size != bitArray.size)
            throw new IllegalArgumentException();
        System.arraycopy(bitArray.data, 0, data, 0, bitArray.data.length);
    }

    /**
     * Returns false if some bits in array are set.
     *
     * @return false if some bits are set
     */
    public boolean isClean() {
        for (byte d : data)
            if (d != 0)
                return false;
        return true;
    }

    public void clearAll() {
        //for (int i = 0; i < data.length; ++i)
        //    data[i] = 0;
        Arrays.fill(data, (byte) 0);
    }

    public int[] getBits() {
        int[] bits = new int[bitCount()];
        int n = 0;
        for (int i = 0; i < size; ++i)
            if (get(i))
                bits[n++] = i;
        return bits;
    }

    public int size() {
        return size;
    }

    public BitArray clone() {
        return new BitArray(Arrays.copyOf(data, data.length), size);
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final BitArray other = (BitArray) obj;
        if (!Arrays.equals(this.data, other.data))
            return false;
        if (this.size != other.size)
            return false;
        return true;
    }

    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Arrays.hashCode(this.data);
        hash = 19 * hash + this.size;
        return hash;
    }

    @Override
    public String toString() {
        char[] c = new char[size];
        for (int i = 0; i < size; ++i)
            if (get(i))
                c[i] = '1';
            else
                c[i] = '0';
        return new String(c);
    }

    public static byte[] extractRawDataArray(BitArray array) {
        return array.data;
    }

    public static BitArray construct(byte[] data, int size) {
        return new BitArray(data, size);
    }
}
