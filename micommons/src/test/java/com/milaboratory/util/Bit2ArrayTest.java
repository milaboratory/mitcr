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
package com.milaboratory.util;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author dmitriybolotin
 */
public class Bit2ArrayTest {
    @Test
    public void generalTest() {
        Random r = new Random();
        for (int n = 0; n < 1000; ++n) {
            int length = r.nextInt(100);
            int[] values = new int[length];
            Bit2Array ba = new Bit2Array(length);
            for (int i = 0; i < length; ++i) {
                values[i] = r.nextInt(4);
                ba.set(i, values[i]);
            }
            assertTrue(ba.equals(ba.getRange(0, ba.size())));
            //Testing
            for (int i = 0; i < length; ++i)
                assertEquals(values[i], ba.get(i));
        }
    }

    @Test
    public void generalDoubleTest() {
        Random r = new Random();
        for (int n = 0; n < 1000; ++n) {
            int length = r.nextInt(100);
            int[] values = new int[length];
            Bit2Array ba = new Bit2Array(length);
            for (int i = 0; i < length; ++i)
                ba.set(i, r.nextInt(4));
            for (int i = 0; i < length; ++i) {
                values[i] = r.nextInt(4);
                ba.set(i, values[i]);
            }
            assertTrue(ba.equals(ba.getRange(0, ba.size())));
            //Testing
            for (int i = 0; i < length; ++i)
                assertEquals(values[i], ba.get(i));
        }
    }

    /*@Test
    public void generalTestPlusIO() throws IOException {
        Random r = new Random();
        for (int n = 0; n < 1000; ++n) {
            int length = r.nextInt(100);
            int[] values = new int[length];
            Bit2Array ba = new Bit2Array(length);
            for (int i = 0; i < length; ++i) {
                values[i] = r.nextInt(4);
                ba.set(i, values[i]);
            }
            //IO
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            Bit2ArrayIO.write(dos, ba);
            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
            DataInputStream dis = new DataInputStream(is);
            ba = Bit2ArrayIO.read(dis);
            //Testing
            for (int i = 0; i < length; ++i)
                assertEquals(values[i], ba.get(i));
        }
    }*/
}
