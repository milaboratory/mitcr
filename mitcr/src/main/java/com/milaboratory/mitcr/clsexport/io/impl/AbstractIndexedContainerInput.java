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

import com.milaboratory.mitcr.clsexport.io.*;
import com.milaboratory.util.ByteBufferHolder;
import com.milaboratory.util.ByteBufferHolderCache;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public abstract class AbstractIndexedContainerInput implements IndexedContainerInput {
    private int signature;
    private long indexAddress;
    protected final DataInput input;
    private List<BinaryContainerReader> registeredReaders = new ArrayList<>();
    private int currentIndex = 0;
    private int size = -1;
    //private long[] addresses = null;
    private LongBuffer addresses = null;
    private final ByteBufferHolderCache bufferHolderCache = new ByteBufferHolderCache();

    public AbstractIndexedContainerInput(DataInput input, int signature) throws IOException {
        this.input = input;
        this.signature = signature;
        //randomAccessFile = new RandomAccessFile(file, "r");
        int sig = input.readInt();
        if (sig != signature)
            throw new SignatureCheckError();
        indexAddress = input.readLong();
    }

    public AbstractIndexedContainerInput(DataInput input) throws IOException {
        this.input = input;
        this.signature = input.readInt();
        indexAddress = input.readLong();
    }

    protected abstract void seek(long address) throws IOException;

    public void readIndex() throws IOException {
        seek(indexAddress);
        currentIndex = -1;
        size = input.readInt();
        //addresses = new long[size];
        byte[] buffer = new byte[size * 8];
        input.readFully(buffer);
        addresses = ByteBuffer.wrap(buffer).asLongBuffer();
        //DataInputStream ds = new DataInputStream(new ByteArrayInputStream(buffer));
        //for (int i = 0; i < size; ++i)
        //    addresses[i] = ds.readLong();
        if (addresses.get(0) != 12L)
            throw new WrongStructureException();
    }

    @Override
    public Object get(int index) {
        try {
            return read(index);
        } catch (IOException ex) {
            Logger.getLogger(FileIndexedContainerInput.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException();
        }
    }

    public <T> void readArray(int offset, T[] buffer) throws IOException {
        readArray(offset, buffer.length, buffer);
    }

    public <T> void readArray(int offset, int length, T[] buffer) throws IOException {
        if (addresses == null)
            readIndex();
        if (buffer.length < length && offset + length > size)
            throw new IllegalArgumentException();
        if (currentIndex != offset) {
            currentIndex = offset;
            seek(addresses.get(offset));
        }
        //Creating buffer holder
        ByteBufferHolder bufferHolder = bufferHolderCache.get();

        int bufferLength = (int) (address(offset + length) - address(offset));
        byte[] bBuffer = bufferHolder.getBuffer(bufferLength);
        input.readFully(bBuffer, 0, bufferLength);
        ByteArrayInputStream bais = new ByteArrayInputStream(bBuffer);
        DataInputStream stream = new DataInputStream(bais);
        for (int i = 0; i < length; ++i)
            buffer[i] = (T) readObject(bais, stream);
    }

    private long address(int index) {
        if (index == size)
            return indexAddress;
        return addresses.get(index);
    }

    private BinaryContainerReader checkIndexAndGetReader(DataInput input) throws IOException {
        int index = input.readInt();
        if (index != currentIndex)
            if (size == -1)
                return null;
            else
                throw new WrongStructureException();
        int type = input.readInt();
        BinaryContainerReader reader = null;
        for (BinaryContainerReader r : registeredReaders)
            if (r.canRead(type))
                reader = r;
        if (reader == null)
            throw new WrongStructureException("Appropriate reader not found for type #" + type);
        return reader;
    }

    private Object readObject(ByteArrayInputStream stream, DataInputStream dataInput) throws IOException {
        BinaryContainerReader reader = checkIndexAndGetReader(dataInput);
        currentIndex++;
        if (reader instanceof FixedBlockSize) {
            int blockSize = ((FixedBlockSize) reader).blockSize();
            int before = stream.available();
            Object obj = reader.read(dataInput);
            blockSize -= before - stream.available();
            stream.skip(blockSize);
            return obj;
        } else
            return reader.read(dataInput);
    }

    public Object read(int index) throws IOException {
        if (addresses == null)
            readIndex();
        if (currentIndex != index) {
            seek(addresses.get(index));
            currentIndex = index;
        }
        //Creating buffer holder
        ByteBufferHolder bufferHolder = bufferHolderCache.get();

        int blockSize = (int) (address(currentIndex + 1) - address(currentIndex));
        byte[] buffer = bufferHolder.getBuffer(blockSize);
        input.readFully(buffer, 0, blockSize);
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer, 0, blockSize);
        Object obj = readObject(bais, new DataInputStream(bais));
        if (bais.available() != 0)
            throw new WrongStructureException();
        return obj;
    }

    //Special Case
    public Object readFirstFixed() throws IOException {
        seek(12L); //First antry address
        BinaryContainerReader reader = checkIndexAndGetReader(input);
        if (!(reader instanceof FixedBlockSize))
            throw new IllegalStateException();
        //Creating buffer holder
        ByteBufferHolder bufferHolder = bufferHolderCache.get();

        int blockSize = ((FixedBlockSize) reader).blockSize();
        byte[] buffer = bufferHolder.getBuffer(blockSize);
        input.readFully(buffer, 0, blockSize);
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer, 0, blockSize);
        Object obj = reader.read(new DataInputStream(bais));
        return obj;
    }

    public abstract void close() throws IOException;

    public void unload() {
        addresses = null;
        bufferHolderCache.reset();
    }

    public int getSignature() {
        return signature;
    }

    public int getNextIndex() {
        return currentIndex;
    }

    public void registerReader(BinaryContainerReader reader) {
        registeredReaders.add(reader);
    }

    @Override
    public int size() {
        try {
            if (size == -1)
                readIndex();
            return size;
        } catch (IOException ex) {
            Logger.getLogger(FileIndexedContainerInput.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException();
        }
    }
}
