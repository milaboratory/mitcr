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
package com.milaboratory.mitcr.clsexport;

import com.milaboratory.mitcr.clsexport.io.AbstractBinaryContainerIO;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * IO for {@link CloneClusterData}.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class CloneClusterDataIO extends AbstractBinaryContainerIO {
    public static final CloneClusterDataIO INSTANCE = new CloneClusterDataIO();

    private CloneClusterDataIO() {
    }

    @Override
    protected Class getEntityClass() {
        return CloneClusterData.class;
    }

    public Object read(DataInput input) throws IOException {
        int off = input.readInt();
        int len = input.readInt();
        int count = input.readInt();
        return new CloneClusterData(off, len, count);
    }

    public int typeId() {
        return 0xBEF3423;
    }

    public void write(DataOutput output, Object object) throws IOException {
        CloneClusterData data = (CloneClusterData) object;
        output.writeInt(data.getOffset());
        output.writeInt(data.getLength());
        output.writeInt(data.getCountCheck());
    }
}
