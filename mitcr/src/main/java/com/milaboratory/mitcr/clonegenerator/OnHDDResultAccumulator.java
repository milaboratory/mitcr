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
package com.milaboratory.mitcr.clonegenerator;

import cc.redberry.pipe.OutputPort;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

public class OnHDDResultAccumulator implements ResultsAccumulator {
    private final AtomicInteger count = new AtomicInteger();
    private File file;
    private ObjectOutputStream stream;

    public OnHDDResultAccumulator() {
        try {
            this.file = File.createTempFile("mitcr_", ".rsl");
            this.file.deleteOnExit();
            this.stream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public int getCount() {
        return count.get();
    }

    @Override
    public synchronized OutputPort<CDR3ExtractionResult> getBack() {
        try {
            boolean closed = (stream == null);
            if (!closed)
                closeStream();

            final ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            final File oFile = file;

            if (!closed) {
                //Re-open stream
                file = File.createTempFile("mitcr_", ".rsl");
                file.deleteOnExit();
                stream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            }

            return new OutputPort<CDR3ExtractionResult>() {
                boolean closed = false;

                @Override
                public synchronized CDR3ExtractionResult take() throws InterruptedException {
                    if (closed)
                        return null;

                    try {
                        if (ois.readBoolean())
                            return (CDR3ExtractionResult) ois.readObject();
                        else {
                            closed = true;
                            ois.close();
                            oFile.delete();
                            return null;
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public synchronized void close() {
                    if (closed)
                        return;
                    try {
                        ois.close();
                        closed = true;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeStream() throws IOException {
        if (stream == null)
            return;
        stream.writeBoolean(false);
        stream.close();
        stream = null;
    }

    @Override
    public synchronized void put(CDR3ExtractionResult cdr3ExtractionResult) {
        try {
            if (cdr3ExtractionResult == null)
                closeStream();
            else if (stream != null) {
                stream.writeBoolean(true);
                stream.writeObject(cdr3ExtractionResult);
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
