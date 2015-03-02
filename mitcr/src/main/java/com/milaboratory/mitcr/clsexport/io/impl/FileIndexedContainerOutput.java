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

import com.milaboratory.mitcr.clsexport.io.BinaryContainerWriter;
import com.milaboratory.mitcr.clsexport.io.FixedBlockSize;
import com.milaboratory.mitcr.clsexport.io.IndexedContainerOutput;
import com.milaboratory.mitcr.clsexport.io.WrongStructureException;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileIndexedContainerOutput implements IndexedContainerOutput {
    private int signature;
    private boolean closed = false;
    private final IByteArrayOutputStream bufferStream;
    private final DataOutputStream bufferDataStream;
    private final RandomAccessFile randomAccessFile;
    private final List<BinaryContainerWriter> registeredWriters = new ArrayList<>();
    private final BBBackedLongArrayList addresses = new BBBackedLongArrayList();

    public FileIndexedContainerOutput(File file, int signature) throws IOException {
        this.signature = signature;
        randomAccessFile = new RandomAccessFile(file, "rw");
        //Delete previous content
        randomAccessFile.getChannel().truncate(0L);
        //Write signature
        randomAccessFile.writeInt(signature);
        //Write placeholder for index address
        randomAccessFile.writeLong(0L);
        //Setting up buffer
        bufferStream = new IByteArrayOutputStream();
        bufferDataStream = new DataOutputStream(bufferStream);
    }

    public FileIndexedContainerOutput(String fileName, int signature) throws IOException {
        this(new File(fileName), signature);
    }

    @Override
    public int add(Object obj) {
        try {
            return write(obj);
        } catch (IOException ex) {
            Logger.getLogger(FileIndexedContainerOutput.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException();
        }
    }

    public synchronized int write(Object obj) throws IOException {
        BinaryContainerWriter writer = null;
        for (BinaryContainerWriter w : registeredWriters)
            if (w.canWrite(obj)) {
                writer = w;
                break;
            }
        if (writer == null)
            throw new RuntimeException("Appropriate writer not found");
        //Next Index
        int index = addresses.size();
        //Forming buffer to put to file
        bufferStream.reset();
        bufferDataStream.writeInt(index);
        bufferDataStream.writeInt(writer.typeId());
        writeToBuffer(writer, obj);
        //Writing to file
        addresses.add(randomAccessFile.getFilePointer());
        bufferStream.writeTo(randomAccessFile);
        return index;
    }

    public synchronized void rewrite(int index, Object obj) throws IOException {
        BinaryContainerWriter writer = null;
        for (BinaryContainerWriter w : registeredWriters)
            if (w.canWrite(obj)) {
                writer = w;
                break;
            }
        if (writer == null)
            throw new RuntimeException("Appropriate writer not found");
        //Saving current address
        long currentAddress = randomAccessFile.getFilePointer();
        randomAccessFile.seek(addresses.get(index));
        //Redundant Check
        int rIndex = randomAccessFile.readInt();
        if (rIndex != index)
            throw new WrongStructureException();
        int rType = randomAccessFile.readInt();
        if (rType != writer.typeId())
            throw new WrongStructureException();
        //Forming Buffer
        bufferStream.reset();
        writeToBuffer(writer, obj);
        bufferStream.writeTo(randomAccessFile);
        //Restoring file position
        randomAccessFile.seek(currentAddress);
    }

    private void writeToBuffer(BinaryContainerWriter writer, Object obj) throws IOException {
        int startPosition = bufferStream.size();
        writer.write(bufferDataStream, obj);
        if (writer instanceof FixedBlockSize) {
            int blockSize = ((FixedBlockSize) writer).blockSize();
            blockSize += startPosition;
            blockSize -= bufferStream.size();
            if (blockSize < 0)
                throw new IOException("Serialized Object is too long to wrap in to fixed block.");
            bufferStream.write((byte) 0, blockSize);
        }
    }

    public int getSignature() {
        return signature;
    }

    public void close() throws IOException {
        long indexAddress = randomAccessFile.getFilePointer();
        randomAccessFile.writeInt(addresses.size());
        //for (int i = 0; i < addresses.size(); ++i)
        //    randomAccessFile.writeLong(addresses.get(i));
        addresses.output(randomAccessFile);
        randomAccessFile.seek(4L);
        randomAccessFile.writeLong(indexAddress);
        randomAccessFile.close();
        closed = true;
    }

    public final boolean isClosed() {
        return closed;
    }

    public void registerWriter(BinaryContainerWriter writer) {
        registeredWriters.add(writer);
    }
}
