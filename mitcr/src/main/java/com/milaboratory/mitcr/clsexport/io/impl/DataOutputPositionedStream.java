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

import com.milaboratory.mitcr.clsexport.io.DataOutputPositioned;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class DataOutputPositionedStream extends DataOutputStream implements DataOutputPositioned {
    private final OutputStreamWrapper streamWrapper;

    public DataOutputPositionedStream(OutputStream out) {
        super(new OutputStreamWrapper(out));
        streamWrapper = (OutputStreamWrapper) this.out;
    }

    @Override
    public long position() {
        return streamWrapper.position;
    }

    public void resetPosition() {
        streamWrapper.reset();
    }

    private static class OutputStreamWrapper extends OutputStream {
        private OutputStream innerStream;
        public long position = 0;

        public OutputStreamWrapper(OutputStream innerStream) {
            this.innerStream = innerStream;
        }

        public void reset() {
            position = 0;
        }

        @Override
        public void write(int b) throws IOException {
            position++;
            innerStream.write(b);
        }

        @Override
        public void close() throws IOException {
            innerStream.close();
        }

        @Override
        public void flush() throws IOException {
            innerStream.flush();
        }

        @Override
        public void write(byte[] b) throws IOException {
            innerStream.write(b);
            position += b.length;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            innerStream.write(b, off, len);
            position += len;
        }
    }
}
