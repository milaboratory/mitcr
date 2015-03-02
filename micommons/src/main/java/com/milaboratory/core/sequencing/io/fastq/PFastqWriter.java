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
import com.milaboratory.core.sequencing.read.PSequencingRead;
import com.milaboratory.util.CompressionType;

import java.io.IOException;

/**
 * File writer in FASTQ format for paired-end reads
 */
public final class PFastqWriter {
    private final SFastqWriter writer0, writer1;

    /**
     * Creates file writer in FASTQ format for paired-end reads
     *
     * @param fileR1 file to store first reads
     * @param fileR2 file to store second read
     * @param format sequencing quality format
     */
    public PFastqWriter(String fileR1, String fileR2, QualityFormat format) throws IOException {
        this.writer0 = new SFastqWriter(fileR1, format);
        this.writer1 = new SFastqWriter(fileR2, format);
    }

    /**
     * Creates file writer in FASTQ format for paired-end reads
     *
     * @param fileR1 file to store first reads
     * @param fileR2 file to store second read
     * @param format sequencing quality format
     * @param ct     compression type to use
     */
    public PFastqWriter(String fileR1, String fileR2, QualityFormat format, CompressionType ct) throws IOException {
        this.writer0 = new SFastqWriter(fileR1, format, ct);
        this.writer1 = new SFastqWriter(fileR2, format, ct);
    }

    /**
     * Writes {@link PSequencingRead} to a file
     *
     * @param read paired-end read
     */
    public void write(PSequencingRead read) throws IOException {
        writer0.write(read.getSingleRead(0));
        writer1.write(read.getSingleRead(1));
    }

    /**
     * Closes the writer
     */
    public void close() throws IOException {
        writer0.close();
        writer1.close();
    }
}
