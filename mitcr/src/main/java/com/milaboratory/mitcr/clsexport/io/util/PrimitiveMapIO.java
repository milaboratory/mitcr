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
package com.milaboratory.mitcr.clsexport.io.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

public abstract class PrimitiveMapIO {
    /*
     * Record structure:
     * |Count|{|key0|type0|data0|}...{|keyN-1|typeN-1|dataN-1|}
     * 
     */
    public static Map<String, Object> read(DataInput input) throws IOException {
        byte count = input.readByte();
        Map<String, Object> primitives = new HashMap<String, Object>();
        for (int i = 0; i < count; ++i) {
            String key = input.readUTF();
            primitives.put(key, PrimitiveIO.read(input));
        }
        return primitives;
    }

    public static boolean canWrite(Object o) {
        return PrimitiveIO.canWrite(o);
    }

//    public static Map<String, Object> read(RandomAccessFile input, int length) throws IOException {
//        long pointerBefore = input.getFilePointer();
//        Map<String, Object> map = read(input);
//        int skip = (int) (length - input.getFilePointer() + pointerBefore);
//        if (skip != input.skipBytes(skip))
//            throw new WrongStructureException();
//        return map;
//    }

    public static void write(DataOutput output, Map<String, Object> map) throws IOException {
        output.writeByte((byte) map.entrySet().size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            output.writeUTF(entry.getKey());
            Object value = entry.getValue();
            PrimitiveIO.write(output, value);
        }
    }

    public static void write(RandomAccessFile output, Map<String, Object> map, int length) throws IOException {
        long pointerBefore = output.getFilePointer();
        write(output, map);
        int written = (int) (output.getFilePointer() - pointerBefore);
        byte[] bytes = new byte[length - written];
        output.write(bytes);
    }
}
