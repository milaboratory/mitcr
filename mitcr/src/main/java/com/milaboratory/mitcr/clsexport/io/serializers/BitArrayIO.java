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
package com.milaboratory.mitcr.clsexport.io.serializers;

import com.milaboratory.util.BitArray;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class BitArrayIO {
    public static void write(DataOutput outputStream, BitArray object) throws IOException {
        //Old IO for int[] backed BitArray
        //if (object == null) {
        //     outputStream.writeInt(-1);
        //    return;
        //}
        //outputStream.writeInt(object.size);
        //IOUtill.writeIntArray(outputStream, object.data);

        //New IO for byte[] backed BitArray
        if (object == null) {
            outputStream.writeInt(-1);  // == 0xFFFFFFFF
            return;
        }
        outputStream.writeInt(0x80000000 | object.size()); //0x80000000 is a version flag
        outputStream.write(BitArray.extractRawDataArray(object));
    }

    public static BitArray read(DataInput inputStream) throws IOException {
        int size = inputStream.readInt();
        if (size == -1)
            return null;
        byte[] data;
        /*if ((0x80000000 & size) == 0) //Old version
            //int[] data = IOUtill.readIntArray(inputStream);
            //return new BitArray(data, length);
            data = IOUtill.readBCIntArrayToBytes(inputStream);
        else { //New IO*/
        size &= 0x7FFFFFFF;
        data = new byte[(size + 7) >> 3];
        inputStream.readFully(data);
        //}
        return BitArray.construct(data, size);
    }
}
