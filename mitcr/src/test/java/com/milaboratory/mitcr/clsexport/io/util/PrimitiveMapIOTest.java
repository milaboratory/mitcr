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

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class PrimitiveMapIOTest {
    @Test
    public void test() {
        File file = new File("ec2.test.map");
        try {
            Map<String, Object> origMap = new HashMap<String, Object>();
            origMap.put("intVal", 23);
            origMap.put("strVal", "veselok");
            origMap.put("byte", (byte) 5);
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
            PrimitiveMapIO.write(dos, origMap);
            dos.close();
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            Map<String, Object> loadedMap = PrimitiveMapIO.read(dis);
            dis.close();
            for (String key : origMap.keySet())
                Assert.assertEquals(origMap.get(key), loadedMap.get(key));
        } catch (IOException ex) {
            Logger.getLogger(PrimitiveMapIOTest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            file.delete();
        }
    }
}