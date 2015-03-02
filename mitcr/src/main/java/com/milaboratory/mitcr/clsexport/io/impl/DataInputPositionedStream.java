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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class DataInputPositionedStream extends DataInputStream {
    private InputStreamWrapper wrapper;

    public DataInputPositionedStream(InputStream in) {
        super(new InputStreamWrapper(in));
        wrapper = (InputStreamWrapper) this.in;
    }

    public void resetPosition() {
        wrapper.resetPosition();
    }

    private static class InputStreamWrapper extends InputStream {
        public long position = 0L;
        private InputStream innerStream;

        public InputStreamWrapper(InputStream innerStream) {
            this.innerStream = innerStream;
        }

        @Override
        public int read() throws IOException {
            int value = read();
            if (value > 0)
                position++;
            return value;
        }

        @Override
        public int available() throws IOException {
            return innerStream.available();
        }

        @Override
        public void close() throws IOException {
            innerStream.close();
        }

        @Override
        public synchronized void mark(int readlimit) {
        }

        @Override
        public boolean markSupported() {
            return false;
        }

        @Override
        public int read(byte[] b) throws IOException {
            int length = innerStream.read(b);
            position += length;
            return length;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int length = innerStream.read(b, off, len);
            position += length;
            return length;
        }

        @Override
        public synchronized void reset() throws IOException {
            throw new IOException();
        }

        @Override
        public long skip(long n) throws IOException {
            long length = super.skip(n);
            position += length;
            return length;
        }

        public void resetPosition() {
            position = 0L;
        }
    }
}
