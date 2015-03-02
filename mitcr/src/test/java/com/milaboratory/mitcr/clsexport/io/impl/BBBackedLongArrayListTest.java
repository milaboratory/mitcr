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
package com.milaboratory.mitcr.clsexport.io.impl;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author dmitriybolotin
 */
public class BBBackedLongArrayListTest {
    @Test
    public void testSomeMethod() {
        long[] testArray = new long[]{1L, 74L, 23551444L, 123442332L, 2352234444L,
                1L, 74L, 23551444L, 123442332L, 2352234444L,
                1L, 74L, 23551444L, 123442332L, 2352234444L,
                1L, 74L, 23551444L, 123442332L, 2352234444L,
                1L, 74L, 23551444L, 123442332L, 2352234444L,
                1L, 74L, 23551444L, 123442332L, 2352234444L,
                1L, 74L, 23551444L, 123442332L, 2352234444L};
        BBBackedLongArrayList list = new BBBackedLongArrayList();
        for (int i = 0; i < testArray.length; ++i)
            list.add(testArray[i]);
        for (int i = 0; i < testArray.length; ++i)
            Assert.assertEquals(list.get(i), testArray[i]);
    }
}
