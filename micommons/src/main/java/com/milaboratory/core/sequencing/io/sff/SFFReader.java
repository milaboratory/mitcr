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
package com.milaboratory.core.sequencing.io.sff;

import cc.redberry.pipe.blocks.AbstractOutputPortUninterruptible;
import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequence.nucleotide.NucleotideAlphabet;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.core.sequence.quality.SequenceQualityPhred;
import com.milaboratory.core.sequencing.io.SSequencingDataReaderWithFlowgram;
import com.milaboratory.core.sequencing.read.SSequencingReadWithFlowgram;
import com.milaboratory.util.*;

import java.io.*;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class SFFReader extends AbstractOutputPortUninterruptible<SSequencingReadWithFlowgram> implements SSequencingDataReaderWithFlowgram {
    private long counter = 0;
    private final DataInputStream stream;
    private SFFHeader header;
    private final ByteBufferHolderCache bufferHolderCache = new ByteBufferHolderCache();

    public SFFReader(File file) throws IOException {
        this(new FileInputStream(file), CompressionType.None);
    }

    public SFFReader(File file, CompressionType ct) throws IOException {
        this(new FileInputStream(file), ct);
    }

    public SFFReader(InputStream is, CompressionType ct) throws IOException {
        is = ct.createInputStream(is);
        BufferedInputStream bis = new BufferedInputStream(is);
        stream = new DataInputStream(bis);
        readHeader();
    }

    private void readHeader() throws IOException {
        //Checking magic number
        int magicNumber = stream.readInt();
        if (magicNumber != 0x2E736666)
            throw new IOException("Wrong magic number.");
        //Creating buffer holder
        ByteBufferHolder bufferHolder = bufferHolderCache.get();

        char[] version = new char[4];
        byte[] buf = bufferHolder.getBuffer(4);
        stream.read(buf, 0, 4);
        for (int i = 0; i < 4; ++i)
            version[i] = (char) buf[i];
        if (version[0] != 0 || version[1] != 0 || version[2] != 0 || version[3] != 1)
            throw new IOException("Unsupported version.");
        long indexOffset = stream.readLong();
        int indexLength = stream.readInt();
        int numberOfReads = stream.readInt();
        int headerLength = stream.readUnsignedShort();

        bufferHolder.readToBuffer(stream, headerLength - 26);
        DataInputStream bufferStream = bufferHolder.getDataInputStream();
        int keyLength = bufferStream.readUnsignedShort();
        int numberOfFlows = bufferStream.readUnsignedShort();
        int flowgramFormatCode = bufferStream.readUnsignedByte();
        char[] flowChars = new char[numberOfFlows];
        char[] keySequence = new char[keyLength];
        for (int i = 0; i < numberOfFlows; ++i)
            flowChars[i] = (char) bufferStream.readUnsignedByte();
        for (int i = 0; i < keyLength; ++i)
            keySequence[i] = (char) bufferStream.readUnsignedByte();
        if (numberOfFlows < 0 || numberOfReads < 0)
            throw new IOException("Unsigned to signed overflow.");
        header = new SFFHeader(version, indexOffset, indexLength, numberOfReads, keyLength, flowgramFormatCode, numberOfFlows, flowChars, keySequence);
    }

    @Override
    public SFFRAWRead _take() {
        if (counter == header.getNumberOfReads() - 1) {
            synchronized (stream) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            return null;
        }

        //Getting buffer holder
        ByteBufferHolder bufferHolder = bufferHolderCache.get();

        //Buffer stream and byte buffer
        DataInputStream bufferStream;
        byte[] buff;

        //Read metadata
        int numberOfBases, clipQualLeft,
                clipQualRight, clipAdapterLeft,
                clipAdapterRight;
        String name;

        //id
        long id;

        //Accessing stream
        synchronized (stream) {
            try {
                //Reading Read Header
                int headerLength = stream.readUnsignedShort();
                buff = bufferHolder.readToBuffer(stream, headerLength - 2);
                bufferStream = bufferHolder.getDataInputStream();
                int nameLength = bufferStream.readUnsignedShort();
                numberOfBases = bufferStream.readInt();
                if (numberOfBases < 0)
                    throw new IOException("Unsigned to signed overflow.");

                //Indexing in file is 1-based
                clipQualLeft = bufferStream.readUnsignedShort() - 1;
                clipQualRight = bufferStream.readUnsignedShort() - 1;
                clipAdapterLeft = bufferStream.readUnsignedShort() - 1;
                clipAdapterRight = bufferStream.readUnsignedShort() - 1;

                //Direct array to String convertion
                name = new String(buff, 14, nameLength);

                //Readin Read data
                int mainPartLength = 2 * header.getNumberOfFlows() + 3 * numberOfBases;
                mainPartLength = (((mainPartLength - 1) / 8) + 1) * 8;
                buff = bufferHolder.readToBuffer(stream, mainPartLength);
                id = counter++;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        bufferStream = bufferHolder.getDataInputStream();
        int[] flowgramValues = new int[header.getNumberOfFlows()];
        try {
            for (int i = 0; i < header.getNumberOfFlows(); ++i)
                flowgramValues[i] = bufferStream.readUnsignedShort();
        } catch (IOException ex) {
            throw new RuntimeException("Impossible exception.", ex);
        }
        byte[] flowIndexPerBaseIncrements = new byte[numberOfBases];
        System.arraycopy(buff, 2 * header.getNumberOfFlows(), flowIndexPerBaseIncrements, 0, numberOfBases);
        flowIndexPerBaseIncrements[0] = 0; // Correction for 0-based indexing
        Bit2Array sequenceData = new Bit2Array(numberOfBases);
        for (int i = 0; i < numberOfBases; ++i)
            sequenceData.set(i, NucleotideAlphabet.INSTANCE.codeFromSymbol((char) (buff[2 * header.getNumberOfFlows() + numberOfBases + i])));
        byte[] qualityData = new byte[numberOfBases];
        NucleotideSequence sequence = new NucleotideSequence(sequenceData);
        System.arraycopy(buff, 2 * header.getNumberOfFlows() + 2 * numberOfBases, qualityData, 0, numberOfBases);
        NucleotideSQPair sqData = new NucleotideSQPair(sequence, new SequenceQualityPhred(qualityData));
        int[] flowgramIndexes = new int[numberOfBases];
        int index = 0;
        for (int i = 0; i < numberOfBases; ++i)
            flowgramIndexes[i] = (index += flowIndexPerBaseIncrements[i]);
        return new SFFRAWRead(name, sqData, new IndexRange(0, header.getNumberOfFlows()),
                flowgramValues, flowgramIndexes, header.getFlowsSequence(),
                clipQualLeft, clipQualRight, clipAdapterLeft, clipAdapterRight, id);
    }

    public SFFHeader getHeader() {
        return header;
    }

    @Override
    public NucleotideSequence getFlowgramSequence() {
        return header.getFlowsSequence();
    }

    @Override
    public void _close() {
        synchronized (stream) {
            try {
                stream.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /*@Override
    public Status getStatus() {
        return new Status("Reading SFF", 100 * readsRed / header.getNumberOfReads());
    }*/

    /*public static SSequencingDataReaderWithFlowgram create(InputStream is, CompressionType ct) throws IOException {
        return SequencindDad.wrap(new SFFReader(is, ct), new SFFClipper());
    }

    public static SSequencingDataReaderWithFlowgram create(File file, CompressionType ct) throws IOException {
        return create(new FileInputStream(file), ct);
    }

    public static SSequencingDataReaderWithFlowgram create(String file, CompressionType ct) throws IOException {
        return create(new File(file), ct);
    }

    public static SSequencingDataReaderWithFlowgram create(String file) throws IOException {
        return create(new File(file), CompressionType.None);
    }

    public static SSequencingDataReaderWithFlowgram create(File file) throws IOException {
        return create(file, CompressionType.None);
    }*/
}
