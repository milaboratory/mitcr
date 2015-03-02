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

import com.milaboratory.mitcr.clsexport.io.BinaryContainerIO;
import com.milaboratory.mitcr.clsexport.io.WrongStructureException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class PrimitiveArrayIO {
    /*
     * Types:
     * 1 - short[] (Not supported)
     * 2 - int[]
     * 3 - long[]
     * 4 - float[] (Not supported)
     * 5 - double[] (Not supported)
     * 6 - boolean[] (Not supported)
     * 7 - char[] (Not supported)
     * 9 - byte[] (Not supported)
     */
    public static Object read(DataInput input) throws IOException {
        byte type = input.readByte();
        int length = input.readInt();
        switch (type) {
            case 2:
                return readIntArrayWithoutSize(input, length);
            case 3:
                return readLongArrayWithoutSize(input, length);
        }
        throw new WrongStructureException();
    }

    public static boolean canWrite(Object o) {
        if (o == null)
            return false;
        Class c = o.getClass();
        if (!c.isArray())
            return false;
        return c.getComponentType() == Integer.TYPE || c.getComponentType() == Long.TYPE;
    }

    public static void write(DataOutput output, Object value) throws IOException {
        Class cClass = value.getClass().getComponentType();
        if (cClass == Integer.TYPE) {
            output.writeByte(2);
            int[] array = (int[]) value;
            writeIntArray(output, array);
            return;
        }
        if (cClass == Long.TYPE) {
            output.writeByte(3);
            long[] array = (long[]) value;
            writeLongArray(output, array);
            return;
        }
        throw new RuntimeException("Unsupported class exception.");
    }


    //Integer
    public static void writeIntArray(DataOutput output, int[] array) throws IOException {
        output.writeInt(array.length);
        writeIntArrayWithoutSize(output, array);
    }

    public static void writeIntArrayWithoutSize(DataOutput output, int[] array) throws IOException {
        for (int i = 0; i < array.length; ++i)
            output.writeInt(array[i]);
    }

    public static int[] readIntArray(DataInput input) throws IOException {
        return readIntArrayWithoutSize(input, input.readInt());
    }

    public static int[] readIntArrayWithoutSize(DataInput input, int length) throws IOException {
        int[] array = new int[length];
        readIntArrayWithoutSize(input, array);
        return array;
    }

    public static void readIntArrayWithoutSize(DataInput input, int[] array) throws IOException {
        for (int i = 0; i < array.length; ++i)
            array[i] = input.readInt();
    }

    //Long
    public static void writeLongArray(DataOutput output, long[] array) throws IOException {
        output.writeInt(array.length);
        writeLongArrayWithoutSize(output, array);
    }

    public static void writeLongArrayWithoutSize(DataOutput output, long[] array) throws IOException {
        for (int i = 0; i < array.length; ++i)
            output.writeLong(array[i]);
    }

    public static long[] readLongArray(DataInput input) throws IOException {
        return readLongArrayWithoutSize(input, input.readInt());
    }

    public static long[] readLongArrayWithoutSize(DataInput input, int length) throws IOException {
        long[] array = new long[length];
        readLongArrayWithoutSize(input, array);
        return array;
    }

    public static void readLongArrayWithoutSize(DataInput input, long[] array) throws IOException {
        for (int i = 0; i < array.length; ++i)
            array[i] = input.readLong();
    }

    private static class Instance implements BinaryContainerIO {
        public static final int TYPE_ID = 0x80000002;

        @Override
        public boolean canRead(int typeId) {
            return typeId == TYPE_ID;
        }

        @Override
        public Object read(DataInput input) throws IOException {
            return PrimitiveArrayIO.read(input);
        }

        @Override
        public boolean canWrite(Object object) {
            return PrimitiveArrayIO.canWrite(object);
        }

        @Override
        public int typeId() {
            return TYPE_ID;
        }

        @Override
        public void write(DataOutput output, Object object) throws IOException {
            PrimitiveArrayIO.write(output, object);
        }
    }

    public static BinaryContainerIO INSTANCE = new Instance();
}
