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
import com.milaboratory.core.sequence.quality.QualityFormat;
import com.milaboratory.core.sequencing.io.PSequencingDataReader;
import com.milaboratory.core.sequencing.read.PSequencingRead;
import com.milaboratory.core.sequencing.read.PSequencingReadImpl;
import com.milaboratory.core.sequencing.read.SSequencingRead;
import com.milaboratory.util.CompressionType;

import java.io.*;

/**
 * A reader for paired-end read data in FASTQ format. Acts as an output port
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
// TODO: _close() and _take()
public class PFastqReader extends AbstractOutputPortUninterruptible<PSequencingRead> implements PSequencingDataReader {
    private final BufferedReader[] readers;
    private final QualityFormat format;
    private final ReadInfoProvider infoProvider;
    private long counter = 0;
    private final boolean notFilteredOnly, check;

    //TODO docs....
    public PFastqReader(String file0, String file1, QualityFormat format) throws IOException {
        this(file0, file1, format, CompressionType.None);
    }

    public PFastqReader(String file0, String file1, QualityFormat format, CompressionType ct) throws IOException {
        this(new File(file0), new File(file1), format, ct, null, false, false);
    }

    public PFastqReader(File file0, File file1, QualityFormat format) throws IOException {
        this(file0, file1, format, CompressionType.None);
    }

    public PFastqReader(File file0, File file1, QualityFormat format, CompressionType ct) throws IOException {
        this(file0, file1, format, ct, null, false, false);
    }

    /**
     * Creates a {@link PSequencingRead} stream from two FASTQ files with paired-end read data
     *
     * @param file0           file with first read mates
     * @param file1           file with second read mates
     * @param format          read quality encoding format
     * @param ct              type of compression (NONE, GZIP, etc)
     * @param check           checks if reads are paired using their headers
     * @param notFilteredOnly outputs only reads that are not marked by 'filtered' flag in their header
     * @throws IOException in case there is problem with reading from files
     */
    public PFastqReader(File file0, File file1, QualityFormat format, CompressionType ct,
                        ReadInfoProvider infoProvider,
                        boolean check, boolean notFilteredOnly) throws IOException {
        this(new FileInputStream(file0), new FileInputStream(file1), format, ct, infoProvider, check, notFilteredOnly);
    }

    /**
     * Creates a {@link PSequencingRead} stream from two FASTQ files with paired-end read data
     *
     * @param stream0         stream containing first read mates
     * @param stream1         stream containing second read mates
     * @param format          read quality encoding format
     * @param ct              type of compression (NONE, GZIP, etc)
     * @param check           checks if reads are paired using their headers
     * @param notFilteredOnly outputs only reads that are not marked by 'filtered' flag in their header
     * @throws IOException in case there is problem with reading from files
     */
    public PFastqReader(InputStream stream0, InputStream stream1, QualityFormat format, CompressionType ct,
                        ReadInfoProvider infoProvider,
                        boolean check, boolean notFilteredOnly) throws IOException {
        this.readers = new BufferedReader[]{
                new BufferedReader(new InputStreamReader(ct.createInputStream(stream0))),
                new BufferedReader(new InputStreamReader(ct.createInputStream(stream1)))
        };
        this.format = format;

        if (infoProvider == null && (check || notFilteredOnly))
            throw new IllegalArgumentException("Read info provider is required for filtering checking and reads pair errors detection.");

        this.infoProvider = infoProvider;
        this.check = check;
        this.notFilteredOnly = notFilteredOnly;
    }

    @Override
    public PSequencingRead _take() {
        long id;
        while (true) {
            String[] lines0, lines1;

            synchronized (readers) {
                try {
                    lines0 = read4Lines(readers[0]);
                    lines1 = read4Lines(readers[1]);
                    if (lines0[0] == null)
                        if (lines1[0] == null)
                            return null;
                        else
                            throw new RuntimeException("Different records count in files.");
                    id = counter++;
                } catch (IOException | RuntimeException ex) {
                    closeReaders(readers);
                    throw new RuntimeException(ex);
                }
            }

            //Parsing
            SSequencingRead read0 = SFastqReader.parse(format, lines0.clone(), id);
            SSequencingRead read1 = SFastqReader.parse(format, lines1.clone(), id);

            if (check || notFilteredOnly) {
                //Creating info
                ReadInfo info0 = infoProvider.getInfo(read0.getDescription());
                ReadInfo info1 = infoProvider.getInfo(read1.getDescription());

                if (check) {
                    //Correct reads number and pairing
                    if (info0.getReadNumber() != 0 || info1.getReadNumber() != 1)
                        throw new RuntimeException("Wrong run numbering");

                    //Test for unpaired reads
                    if (!info0.isPairOf(info1))
                        throw new RuntimeException("Unpaired reads.");
                }

                if (notFilteredOnly && (info0.isFiltered() || info1.isFiltered()))
                    continue;
            }

            return new PSequencingReadImpl(read0, read1);
        }
    }

    @Override
    public void _close() {
        closeReaders(readers);
    }

    private static void closeReaders(BufferedReader[] readers) {
        try {
            try {
                readers[0].close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } finally {
                readers[1].close();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String[] read4Lines(BufferedReader reader) throws IOException {
        String[] lines = new String[4];
        for (int i = 0; i < 4; ++i)
            if ((lines[i] = reader.readLine()) == null)
                break;
        return lines;
    }

    /*@Override
    public Status getStatus() {
        return reader0.getStatus();
    }*/

    //TODO move to some util class
    /*public static OutputPort<PSequencingRead> fromFolderSingleRun(String folder, QualityFormat
            format, CompressionType ct,
                                                                  boolean notFiltered, boolean trim) {
        return fromFolderSingleRun(new File(folder), format, ct, notFiltered, trim);
    }

    public static OutputPort<PSequencingRead> fromFolderSingleRun(File folder, QualityFormat
            format, CompressionType ct,
                                                                  boolean notFiltered, boolean trim) {
        Pattern pattern = Pattern.compile("R(\\d)_(\\d{3})\\.fastq(.*)");
        File[] allFiles = folder.listFiles();
        int pCount = 0;
        for (File file : allFiles) {
            Matcher matcher = pattern.matcher(file.getName());
            if (!matcher.find())
                continue;
            int nis = Integer.parseInt(matcher.group(2), 10);
            if (nis > pCount)
                pCount = nis;
        }
        File[][] files = new File[pCount][2];
        for (File file : allFiles) {
            Matcher matcher = pattern.matcher(file.getName());
            if (!matcher.find())
                continue;
            int nis = Integer.parseInt(matcher.group(2), 10);
            int run = Integer.parseInt(matcher.group(1), 10);
            files[nis - 1][run - 1] = file;
        }
        OutputPort<PSequencingRead>[] ops = new OutputPort[pCount];
        try {
            for (int i = 0; i < pCount; ++i)
                ops[i] = new SequencingDataReaderOutputPortAdapter<PSequencingRead>(new PFastqReader(files[i][0], files[i][1], format, ct, notFiltered));
        } catch (FileNotFoundException e) {
        }
        OutputPort<PSequencingRead> port = OutputPort.Utill.cat(ops);
        if (trim)
            port = Processor.Utils.wrap(port, new PSequencingReadsTrimmer(format));
        return port;
    }*/
}