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

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class BitArrayTest {
    @Test
    public void generalTest() {
        Random r = new Random();
        for (int n = 0; n < 1000; ++n) {
            int size = r.nextInt(100) + 1;
            int count = r.nextInt(size);
            int[] bits = new int[count];
            BitArray ba = new BitArray(size);
            for (int j = 0; j < count; ++j) {
                bits[j] = r.nextInt(size);
                ba.set(bits[j]);
            }
            //Testing
            for (int i = 0; i < size; ++i) {
                boolean value = false;
                for (int b : bits)
                    if (b == i) {
                        value = true;
                        break;
                    }
                assertEquals(ba.get(i), value);
            }
        }
    }

    /*@Test
    public void generalTestPlusIO() throws IOException {
        Random r = new Random();
        for (int n = 0; n < 1000; ++n) {
            int size = r.nextInt(100) + 1;
            int count = r.nextInt(size);
            int[] bits = new int[count];
            BitArray ba = new BitArray(size);
            for (int j = 0; j < count; ++j) {
                bits[j] = r.nextInt(size);
                ba.set(bits[j]);
            }
            //IO
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            BitArrayIO.write(dos, ba);
            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
            DataInputStream dis = new DataInputStream(is);
            ba = BitArrayIO.read(dis);
            //Testing
            for (int i = 0; i < size; ++i) {
                boolean value = false;
                for (int b : bits)
                    if (b == i) {
                        value = true;
                        break;
                    }
                assertEquals(ba.get(i), value);
            }
        }
    }*/

    @Test
    public void bitCountTest() {
        for (int i = 1; i < 128; ++i) {
            BitArray ba = new BitArray(i);
            ba.setAll();
            assertEquals(ba.bitCount(), i);
        }
    }
}