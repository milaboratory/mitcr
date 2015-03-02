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

import cc.redberry.pipe.blocks.AbstractOutputPortUninterruptible;
import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.core.sequence.quality.QualityFormat;
import com.milaboratory.core.sequence.quality.SequenceQualityPhred;
import com.milaboratory.core.sequence.quality.SequenceQualityUtils;
import com.milaboratory.core.sequence.quality.WrongQualityStringException;
import com.milaboratory.core.sequencing.io.SSequencingDataReader;
import com.milaboratory.core.sequencing.read.SSequencingRead;
import com.milaboratory.core.sequencing.read.SSequencingReadImpl;
import com.milaboratory.util.CanReportProgress;
import com.milaboratory.util.CompressionType;
import com.milaboratory.util.CountingInputStream;

import java.io.*;

/**
 * FASTQ files reader, using BufferedReader.readLine() method.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
// TODO: _close() and _take()
public class SFastqReader extends AbstractOutputPortUninterruptible<SSequencingRead> implements SSequencingDataReader, CanReportProgress {
    private static final int BUFFER_SIZE = 32768;

    //Main Input
    private final BufferedReader reader;
    //The total size of input stream if known.
    private final long totalSize;
    //Reads read
    private long counter = 0;
    //Bytes read counting IS
    private final CountingInputStream countingStream;
    //FASTQ format
    private final QualityFormat format;
    private final ReadInfoProvider infoProvider;
    //private final boolean notFilteredOnly;

    /**
     * Creates a {@link SSequencingRead} stream from a FASTQ files with single-end read data
     *
     * @param file file with reads
     * @throws IOException in case there is problem with reading from files
     */
    public SFastqReader(String file) throws IOException {
        this(new FileInputStream(file), null, file.endsWith(".gz") ? CompressionType.GZIP : CompressionType.None,
                true, null, false);
    }

    /**
     * Creates a {@link SSequencingRead} stream from a FASTQ files with single-end read data
     *
     * @param file file with reads
     * @param ct   type of compression (NONE, GZIP, etc)
     * @throws IOException in case there is problem with reading from files
     */
    public SFastqReader(String file, CompressionType ct) throws IOException {
        this(new FileInputStream(file), null, ct, true, null, false);
    }

    /**
     * Creates a {@link SSequencingRead} stream from a FASTQ files with single-end read data
     *
     * @param file   file with reads
     * @param format read quality encoding format
     * @param ct     type of compression (NONE, GZIP, etc)
     * @throws IOException in case there is problem with reading from files
     */
    public SFastqReader(String file, QualityFormat format, CompressionType ct) throws IOException {
        this(new FileInputStream(file), format, ct, false, null, false);
    }

    /**
     * Creates a {@link SSequencingRead} stream from a FASTQ files with single-end read data
     *
     * @param file file with reads
     * @throws IOException in case there is problem with reading from files
     */
    public SFastqReader(File file) throws IOException {
        this(new FileInputStream(file), null, file.getName().endsWith(".gz") ? CompressionType.GZIP : CompressionType.None,
                true, null, false);
    }

    /**
     * Creates a {@link SSequencingRead} stream from a FASTQ files with single-end read data
     *
     * @param file file with reads
     * @param ct   type of compression (NONE, GZIP, etc)
     * @throws IOException in case there is problem with reading from files
     */
    public SFastqReader(File file, CompressionType ct) throws IOException {
        this(new FileInputStream(file), null, ct, true, null, false);
    }

    /**
     * Creates a {@link SSequencingRead} stream from a FASTQ files with single-end read data
     *
     * @param file   file with reads
     * @param format read quality encoding format
     * @param ct     type of compression (NONE, GZIP, etc)
     * @throws IOException in case there is problem with reading from files
     */
    public SFastqReader(File file, QualityFormat format, CompressionType ct) throws IOException {
        this(new FileInputStream(file), format, ct, false, null, false);
    }

    /**
     * Creates a {@link SSequencingRead} stream from a FASTQ stream with single-end reads data
     *
     * @param stream stream with reads
     * @param ct     type of compression (NONE, GZIP, etc)
     * @throws IOException in case there is problem with reading from files
     */
    public SFastqReader(InputStream stream, CompressionType ct) throws IOException {
        this(stream, null, ct, true, null, false);
    }

    /**
     * Creates a {@link SSequencingRead} stream from a FASTQ files with single-end read data
     *
     * @param stream stream with reads
     * @param format read quality encoding format
     * @param ct     type of compression (NONE, GZIP, etc)
     * @throws IOException in case there is problem with reading from files
     */
    public SFastqReader(InputStream stream, QualityFormat format, CompressionType ct) throws IOException {
        this(stream, format, ct, false, null, false);
    }

    //Root constructor

    /**
     * Creates a {@link SSequencingRead} stream from a FASTQ files with single-end read data
     *
     * @param stream             stream with reads
     * @param format             read quality encoding format, if {@code guessQualityFormat} is true this value is used
     *                           as a default format
     * @param ct                 type of compression (NONE, GZIP, etc)
     * @param guessQualityFormat if true reader will try to guess quality string format, if guess fails {@code format}
     *                           will be used as a default quality string format, if {@code format==null} exception will
     *                           be thrown
     * @param infoProvider       read info provider
     * @param notFilteredOnly    outputs only reads that are not marked by 'filtered' flag in their header
     * @throws IOException
     */
    public SFastqReader(InputStream stream, QualityFormat format, CompressionType ct,
                        boolean guessQualityFormat, ReadInfoProvider infoProvider, boolean notFilteredOnly) throws IOException {
        //Check for null
        if (stream == null)
            throw new NullPointerException();

        if (stream instanceof FileInputStream)
            totalSize = ((FileInputStream) stream).getChannel().size();
        else
            totalSize = -1L;

        this.infoProvider = infoProvider;
        if (infoProvider == null && notFilteredOnly)
            throw new IllegalArgumentException("Read info provider should be provided for filtering.");

        //Initialization
        InputStream is = this.countingStream = new CountingInputStream(stream);

        //Wrapping stream if un-compression needed
        is = ct.createInputStream(is);

        //Creating main reder
        this.reader = new BufferedReader(new InputStreamReader(is), BUFFER_SIZE);

        //Guessing quality format
        if (guessQualityFormat) {
            reader.mark(BUFFER_SIZE);
            QualityFormat f = QualityFormatChecker.guessFormat(reader, BUFFER_SIZE - 3072); //Buffer minus ~ one read.

            if (f != null)
                format = f;

            reader.reset();
        }

        if (format == null)
            if (guessQualityFormat)
                throw new RuntimeException("Format guess failed.");
            else
                throw new NullPointerException();

        this.format = format;
    }

    @Override
    public SSequencingRead _take() {
        //Read serial id
        long id;
        SSequencingRead read;

        while (true) {
            //Read all 4 raw lines before parsing in synchronized block
            String[] lines = new String[4];
            synchronized (reader) {
                try {
                    for (int i = 0; i < 4; ++i)
                        if ((lines[i] = reader.readLine()) == null)
                            break;

                    //Close condition
                    if (lines[0] == null) {
                        reader.close();
                        return null;
                    }

                    //getting an id for future sequence
                    id = counter++;
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            read = parse(format, lines, id);

            if (infoProvider != null) {
                ReadInfo info = infoProvider.getInfo(read.getDescription());

                if (info.isFiltered())
                    continue;
            }

            return read;
        }
    }

    public static SSequencingRead parse(QualityFormat format, String[] lines, long id) {
        String descriptionLine = lines[0];

        //Parsing:

        //Error check...
        if (descriptionLine.charAt(0) != '@')
            throw new RuntimeException("Wrong file format");

        //Remove '@' from description
        descriptionLine = descriptionLine.substring(1);

        //Reading sequence, plusline, quality
        String sequenceLine = lines[1];
        String plusLine = lines[2];
        String qualityString = lines[3];

        //First check
        if (sequenceLine == null || plusLine == null || qualityString == null
                || plusLine.charAt(0) != '+')
            throw new RuntimeException("Wrong file format");

        //Creating quality
        //Dot correction
        //if (sequenceLine.contains(".")
        //        || sequenceLine.contains("n")
        //        || sequenceLine.contains("N")) {

        byte[] qualityValues;
        try {
            qualityValues = SequenceQualityPhred.parse(format, qualityString.getBytes(), true);
        } catch (WrongQualityStringException ex) {
            throw new RuntimeException("Error while parsing quality", ex);
        }

        char[] seqChars = sequenceLine.toCharArray();
        for (int i = 0; i < seqChars.length; ++i)
            if (seqChars[i] == '.' || seqChars[i] == 'n' || seqChars[i] == 'N') {
                //Substituting '.'/'n'/'N' with A
                seqChars[i] = 'A';
                //and setting bad quality to this nucleotide
                qualityValues[i] = SequenceQualityUtils.BAD_QUALITY_VALUE;
            }

        SequenceQualityPhred quality = new SequenceQualityPhred(qualityValues);

        //Parsing sequence
        NucleotideSequence sequence;
        try {
            sequence = new NucleotideSequence(seqChars);
        } catch (RuntimeException re) {
            throw new RuntimeException("Error while parsing sequence.", re);
        }

        //Additional check
        if (sequence.size() != quality.size())
            throw new RuntimeException("Wrong file format. Different sequence and quality sizes.");

        return new SSequencingReadImpl(descriptionLine, new NucleotideSQPair(sequence, quality), id);
    }

    public QualityFormat getQualityFormat() {
        return format;
    }

    /**
     * Closes the output port
     */
    @Override
    public void _close() {
        //is synchronized with itself and _next calls,
        //so no synchronization on innerReader is needed
        try {
            reader.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public double getProgress() {
        if (totalSize == -1L)
            return Double.NaN;
        return ((double) countingStream.getBytesRead()) / totalSize;
    }

    @Override
    public boolean isFinished() {
        return closed;
    }
}
