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
package com.milaboratory.core.sequencing.io.fasta;

import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.core.sequencing.read.SSequencingRead;

import java.io.*;
import java.util.Collection;

/**
 * Writes reads in a FASTA file. Unsynchronized
 *
 * @author Bolotin Dmitriy <bolotin.dmitriy@gmail.com>
 */
public class FastaWriter {
    private static final int MAX_LINE_LENGTH = 75;
    private final OutputStream stream;
    private int ndCounter = 0;

    /**
     * Creates the writer
     *
     * @param fileName file to be created
     */
    public FastaWriter(String fileName) throws FileNotFoundException {
        this(new File(fileName));
    }

    /**
     * Creates the writer
     *
     * @param file output file
     */
    public FastaWriter(File file) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(file);
        this.stream = new BufferedOutputStream(fos);
    }

    /**
     * Writes the {@link SSequencingRead}, quality values are lost
     *
     * @param read single-end sequencing read
     */
    public final void write(SSequencingRead read) throws IOException {
        stream.write('>');
        String desc = read.getDescription();
        if (desc == null || desc.isEmpty())
            desc = "ND" + (ndCounter++);
        stream.write(desc.getBytes());
        stream.write('\n');
        NucleotideSequence sequence = read.getData().getSequence();
        final byte[] nucteotideBytes = sequence.toString().toUpperCase().getBytes();
        int pointer = 0;
        while (true)
            if (nucteotideBytes.length - pointer > MAX_LINE_LENGTH) {
                stream.write(nucteotideBytes, pointer, MAX_LINE_LENGTH);
                stream.write('\n');
                pointer += MAX_LINE_LENGTH;
            } else {
                stream.write(nucteotideBytes, pointer, nucteotideBytes.length - pointer);
                stream.write('\n');
                break;
            }
    }

    /**
     * Writes several {@link SSequencingRead}s, quality values are lost
     *
     * @param reads a collection of reads
     */
    public final void write(Collection<? extends SSequencingRead> reads) throws IOException {
        for (SSequencingRead sr : reads)
            write(sr);
    }

    /**
     * Closes the output stream
     */
    public final void close() throws IOException {
        stream.close();
    }
}
