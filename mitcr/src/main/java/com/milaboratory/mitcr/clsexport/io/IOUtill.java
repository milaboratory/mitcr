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
package com.milaboratory.mitcr.clsexport.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class IOUtill {
    public static int[] readIntArray(DataInput input) throws IOException {
        int length = input.readInt();
        byte[] bytes = new byte[length * 4];
        input.readFully(bytes);
        int[] data = new int[length];
        ByteBuffer.wrap(bytes).asIntBuffer().get(data);
        return data;
    }

    //Backward compatibility method
    //Reads int array as byte array + flip bytes in it
    //Because of endianness
    public static byte[] readBCIntArrayToBytes(DataInput input) throws IOException {
        int length = input.readInt();
        byte[] bytes = new byte[length << 2];
        input.readFully(bytes);
        byte b;
        int offset;
        for (int i = 0; i < length; ++i) {
            offset = i << 2;
            b = bytes[offset];
            bytes[offset] = bytes[offset + 3];
            bytes[offset + 3] = b;
            b = bytes[offset + 1];
            bytes[offset + 1] = bytes[offset + 2];
            bytes[offset + 2] = b;
        }
        return bytes;
    }

    public static void writeIntArray(DataOutput output, int[] array) throws IOException {
        output.writeInt(array.length);
        ByteBuffer buffer = ByteBuffer.allocate(array.length * 4);
        buffer.asIntBuffer().put(array);
        output.write(buffer.array());
    }
}
