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
import java.util.*;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class PrimitiveIO {
    public static final Class[] supportedClasses = {
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            Boolean.class,
            Character.class,
            String.class,
            Byte.class,
            UUID.class,
            Date.class
    };
    public static final Set<Class> supportedSet = new HashSet<Class>(Arrays.asList(supportedClasses));

    /*
     * 
     * Types:
     * 0 - null
     * 1 - Short
     * 2 - Integer
     * 3 - Long
     * 4 - Float
     * 5 - Double
     * 6 - Boolean
     * 7 - Character
     * 8 - String
     * 9 - Byte
     * 10 - UUID
     * 11 - Date
     * 
     */
    public static Object read(DataInput input) throws IOException {
        byte type = input.readByte();
        switch (type) {
            case 0:
                return null;
            case 1:
                return input.readShort();
            case 2:
                return input.readInt();
            case 3:
                return input.readLong();
            case 4:
                return input.readFloat();
            case 5:
                return input.readDouble();
            case 6:
                return input.readBoolean();
            case 7:
                return input.readChar();
            case 8:
                return input.readUTF();
            case 9:
                return input.readByte();
            case 10:
                long msb = input.readLong();
                long lsb = input.readLong();
                UUID uuid = new UUID(msb, lsb);
                return uuid;
            case 11:
                return new Date(input.readLong());
        }
        throw new WrongStructureException();
    }

    public static boolean canWrite(Object o) {
        if (o == null)
            return true;
        return supportedSet.contains(o.getClass());
    }

    public static void write(DataOutput output, Object value) throws IOException {
        if (value == null)
            output.writeByte(0);
        Class vClass = value.getClass();
        if (vClass == Short.class) {
            output.writeByte(1);
            output.writeShort((Short) value);
            return;
        }
        if (vClass == Integer.class) {
            output.writeByte(2);
            output.writeInt((Integer) value);
            return;
        }
        if (vClass == Long.class) {
            output.writeByte(3);
            output.writeLong((Long) value);
            return;
        }
        if (vClass == Float.class) {
            output.writeByte(4);
            output.writeFloat((Float) value);
            return;
        }
        if (vClass == Double.class) {
            output.writeByte(5);
            output.writeDouble((Double) value);
            return;
        }
        if (vClass == Boolean.class) {
            output.writeByte(6);
            output.writeBoolean((Boolean) value);
            return;
        }
        if (vClass == Character.class) {
            output.writeByte(7);
            output.writeChar((Character) value);
            return;
        }
        if (vClass == String.class) {
            output.writeByte(8);
            output.writeUTF((String) value);
            return;
        }
        if (vClass == Byte.class) {
            output.writeByte(9);
            output.writeByte((Byte) value);
            return;
        }
        if (vClass == UUID.class) {
            output.writeByte(10);
            UUID uuid = (UUID) value;
            output.writeLong(uuid.getMostSignificantBits());
            output.writeLong(uuid.getLeastSignificantBits());
            return;
        }
        if (vClass == Date.class) {
            output.writeByte(11);
            output.writeLong(((Date) value).getTime());
            return;
        }
        throw new RuntimeException("Unsupported class exception.");
    }

    private static class Instance implements BinaryContainerIO {
        public static final int TYPE_ID = 0x80000001;

        @Override
        public boolean canRead(int typeId) {
            return typeId == TYPE_ID;
        }

        @Override
        public Object read(DataInput input) throws IOException {
            return PrimitiveIO.read(input);
        }

        @Override
        public boolean canWrite(Object object) {
            return PrimitiveIO.canWrite(object);
        }

        @Override
        public int typeId() {
            return TYPE_ID;
        }

        @Override
        public void write(DataOutput output, Object object) throws IOException {
            PrimitiveIO.write(output, object);
        }
    }

    public static BinaryContainerIO INSTANCE = new Instance();
}
