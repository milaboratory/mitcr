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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.milaboratory.mitcr.clsexport.io.impl;

import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;

/**
 * @author dmitriybolotin
 */
public class BBBackedLongArrayList {
    private byte[] bytes;
    private LongBuffer buffer;
    private int size = 0;

    public BBBackedLongArrayList() {
        bytes = new byte[10 * 8];
        buffer = ByteBuffer.wrap(bytes).asLongBuffer();
    }

    private void ensureCapacity(int minCapacity) {
        int oldCapacity = bytes.length / 8;
        //minCapacity *= 8;
        if (oldCapacity < minCapacity) {
            int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity)
                newCapacity = minCapacity;
            newCapacity *= 8;
            bytes = Arrays.copyOf(bytes, newCapacity);
            buffer = ByteBuffer.wrap(bytes).asLongBuffer();
        }
    }

    public long get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();
        return buffer.get(index);
    }

    public void add(long value) {
        ensureCapacity(size + 1);
        buffer.put(size++, value);
    }

    public void output(DataOutput out) throws IOException {
        out.write(bytes, 0, size * 8);
    }

    public int size() {
        return size;
    }
}
