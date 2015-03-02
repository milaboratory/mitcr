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

import java.io.*;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class ByteArrayIndexedContainerInput extends AbstractIndexedContainerInput {
    private MovableByteArrayInputStream stream;

    public ByteArrayIndexedContainerInput(InputStream streamToCopy) throws IOException {
        this(createStream(streamToCopy));
    }

    public ByteArrayIndexedContainerInput(InputStream streamToCopy, int signature) throws IOException {
        this(createStream(streamToCopy), signature);
    }

    public ByteArrayIndexedContainerInput(byte[] array) throws IOException {
        this(new MovableByteArrayInputStream(array));
    }

    public ByteArrayIndexedContainerInput(byte[] array, int signature) throws IOException {
        this(new MovableByteArrayInputStream(array), signature);
    }

    private ByteArrayIndexedContainerInput(MovableByteArrayInputStream stream) throws IOException {
        super(new DataInputStream(stream));
        this.stream = stream;
    }

    private ByteArrayIndexedContainerInput(MovableByteArrayInputStream stream, int signature) throws IOException {
        super(new DataInputStream(stream), signature);
        this.stream = stream;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    protected void seek(long address) throws IOException {
        stream.seek(address);
    }

    private static MovableByteArrayInputStream createStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int size;
        byte[] buf = new byte[512];
        while ((size = is.read(buf)) > 0)
            baos.write(buf, 0, size);
        return new MovableByteArrayInputStream(baos.toByteArray());
    }

    private static class MovableByteArrayInputStream extends ByteArrayInputStream {
        public MovableByteArrayInputStream(byte[] buf) {
            super(buf);
        }

        public void seek(long position) {
            pos = (int) position;
        }
    }
}
