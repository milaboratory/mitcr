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

import java.lang.ref.SoftReference;

public final class ByteBufferHolderCache {
    private ThreadLocal<SoftReference<ByteBufferHolder>> holder = new ThreadLocal<>(); // new SoftReference<ByteBufferHolder>(new ByteBufferHolder()));

    public ByteBufferHolderCache() {
        //this.holder.set();
    }

    public ByteBufferHolder get() {
        //For performance
        final ThreadLocal<SoftReference<ByteBufferHolder>> holder = this.holder;

        SoftReference<ByteBufferHolder> reference = holder.get();
        if (reference == null)
            //Initialization
            holder.set(reference = new SoftReference<ByteBufferHolder>(new ByteBufferHolder()));

        ByteBufferHolder result = reference.get();
        if (result == null)
            //Reference was collected by GC
            holder.set(new SoftReference<ByteBufferHolder>(result = new ByteBufferHolder()));

        return result;
    }

    public void reset() {
        holder = new InheritableThreadLocal<>();
    }
}
