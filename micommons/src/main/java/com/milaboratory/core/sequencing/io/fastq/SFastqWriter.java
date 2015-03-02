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
package com.milaboratory.core.sequencing.io.fastq;

import com.milaboratory.core.sequence.quality.QualityFormat;
import com.milaboratory.core.sequencing.read.SSequencingRead;
import com.milaboratory.util.CompressionType;

import java.io.*;
import java.util.Collection;

/**
 * File writer in FASTQ format for single-end reads
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class SFastqWriter {
    private static final byte[] PLUS_DELIMITER = "\n+\n".getBytes();
    //private FileOutputStream foStream;
    private final BufferedOutputStream bufferedOutputStream;
    private final QualityFormat format;

    /**
     * Creates file writer in FASTQ format for single-end reads
     *
     * @param file   file to store reads
     * @param format sequencing quality format
     */
    public SFastqWriter(String file, QualityFormat format) throws IOException {
        this(new File(file), format, CompressionType.None);
    }

    /**
     * Creates file writer in FASTQ format for single-end reads
     *
     * @param file   file to store reads
     * @param format sequencing quality format
     * @param ct     compression type to use
     */
    public SFastqWriter(String file, QualityFormat format, CompressionType ct) throws IOException {
        this(new File(file), format, ct);
    }

    /**
     * Creates file writer in FASTQ format for single-end reads
     *
     * @param file   file to store reads
     * @param format sequencing quality format
     */
    public SFastqWriter(File file, QualityFormat format) throws IOException {
        this(new FileOutputStream(file), format, CompressionType.None);
    }

    /**
     * Creates file writer in FASTQ format for single-end reads
     *
     * @param file   file to store reads
     * @param format sequencing quality format
     * @param ct     compression type to use
     */
    public SFastqWriter(File file, QualityFormat format, CompressionType ct) throws IOException {
        this(new FileOutputStream(file), format, ct);
    }

    /**
     * Creates file writer in FASTQ format for single-end reads
     *
     * @param outputStream stream for formatted output
     * @param format       sequencing quality format
     */
    public SFastqWriter(OutputStream outputStream, QualityFormat format) throws IOException {
        this(outputStream, format, CompressionType.None);
    }

    /**
     * Creates file writer in FASTQ format for single-end reads
     *
     * @param outputStream stream for formatted output
     * @param format       sequencing quality format
     * @param ct           compression type to use
     */
    public SFastqWriter(OutputStream outputStream, QualityFormat format, CompressionType ct) throws IOException {
        this.format = format;
        bufferedOutputStream = new BufferedOutputStream(ct.createOutputStream(outputStream), 65536);
    }

    /**
     * Writes a {@link SSequencingRead} to output
     *
     * @param read {@link SSequencingRead} to write
     */
    public void write(SSequencingRead read) throws IOException {
        bufferedOutputStream.write('@');
        bufferedOutputStream.write(read.getDescription().getBytes());
        bufferedOutputStream.write('\n');
        bufferedOutputStream.write(read.getData().getSequence().toString().getBytes());
        bufferedOutputStream.write(PLUS_DELIMITER);

        /* //Not optimized
        byte quality;
        for (int i = 0; i < read.getData().size(); ++i) {
            quality = read.getData().getQuality().value(i);
            if (!format.isUnSafe() && quality < format.getMinValue() || quality > format.getMinValue())
                throw new RuntimeException("Incompatible format and quality value");
            bufferedOutputStream.write(quality + format.getOffset());
        }*/

        bufferedOutputStream.write(read.getData().getQuality().encode(format.getOffset()));

        bufferedOutputStream.write('\n');
    }

    /**
     * Writes a collection of {@link SSequencingRead}
     *
     * @param reads a collection of {@link SSequencingRead}s to write
     */
    public void write(Collection<? extends SSequencingRead> reads) throws IOException {
        for (SSequencingRead sr : reads)
            write(sr);
    }


    /**
     * Flushes the buffer
     */
    public void flush() throws IOException {
        bufferedOutputStream.flush();
    }

    /**
     * Closes the writer
     */
    public void close() throws IOException {
        bufferedOutputStream.close();
    }
}
