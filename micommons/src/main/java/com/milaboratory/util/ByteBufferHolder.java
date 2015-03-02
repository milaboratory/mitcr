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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public final class ByteBufferHolder {
    private byte[] buffer = new byte[10];
    private ByteArrayInputStream inputStream = null;
    private DataInputStream dataInputStream = null;

    private void ensureCapacity(int minCapacity) {
        int oldCapacity = buffer.length;
        if (oldCapacity < minCapacity) {
            int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity)
                newCapacity = minCapacity;
            buffer = new byte[newCapacity];
            inputStream = null;
            dataInputStream = null;
        }
    }

    public ByteArrayInputStream getInputStream() {
        if (inputStream == null)
            inputStream = new ByteArrayInputStream(buffer);
        else
            inputStream.reset();
        return inputStream;
    }

    public DataInputStream getDataInputStream() {
        if (dataInputStream == null)
            dataInputStream = new DataInputStream(getInputStream());
        else
            inputStream.reset();
        return dataInputStream;
    }

    public byte[] readToBuffer(InputStream stream, int bytes) throws IOException {
        getBuffer(bytes);
        stream.read(buffer, 0, bytes);
        return buffer;
    }

    public byte[] getBuffer(int minLength) {
        ensureCapacity(minLength);
        return buffer;
    }

    public void reset() {
        buffer = new byte[10];
    }
}
