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

import cc.redberry.pipe.blocks.AbstractOutputPortUninterruptible;
import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequence.nucleotide.NucleotideAlphabetWithN;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.core.sequence.quality.SequenceQualityPhred;
import com.milaboratory.core.sequencing.io.SSequencingDataReader;
import com.milaboratory.core.sequencing.read.SSequencingRead;
import com.milaboratory.core.sequencing.read.SSequencingReadImpl;
import com.milaboratory.util.Bit2Array;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import static com.milaboratory.core.sequence.quality.SequenceQualityUtils.BAD_QUALITY_VALUE;
import static com.milaboratory.core.sequence.quality.SequenceQualityUtils.GOOD_QUALITY_VALUE;

/**
 * Reads sequences from a FASTA file
 */
public class FastaReader extends AbstractOutputPortUninterruptible<SSequencingRead> implements SSequencingDataReader {
    private final FastaItemsReader innerReader;
    private long counter = 0;
    //private FileChannel channel;
    //private volatile int percentRed = 0;

    /**
     * Creates the reader
     *
     * @param file file in FASTA format
     */
    public FastaReader(File file) throws FileNotFoundException {
        FileInputStream stream = new FileInputStream(file);
        //channel = stream.getChannel();
        innerReader = new FastaItemsReader(stream);
    }

    /**
     * Closes the reader
     */
    @Override
    public void _close() {
        //is synchronized with itself and _next calls,
        //so no synchronization on innerReader is needed
        try {
            innerReader.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Takes next read
     *
     * @return single-end sequencing read with all quality set to "good"
     */
    @Override
    public SSequencingRead _take() {
        FastaItem item;
        final long id;
        synchronized (innerReader) {
            try {
                item = innerReader.read();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            id = counter++;
        }

        if (item == null)
            return null;

        char[] chars = item.getSequence();
        //No quality in FAST format, so use good string
        //SequenceQualityPhred quality = SequenceQualityUtils.createGoodQualityObject(chars.length);//new SequenceQualityPhred(SequenceQuality.Constants.goodQuality, chars.length);
        byte[] quality = new byte[chars.length];
        Arrays.fill(quality, GOOD_QUALITY_VALUE);
        Bit2Array seqData = new Bit2Array(chars.length);
        for (int i = 0; i < chars.length; ++i) {
            byte base = NucleotideAlphabetWithN.INSTANCE.codeFromSymbol(chars[i]);
            if (base == 4)
                //The letter will be "A"
                quality[i] = BAD_QUALITY_VALUE;
            else
                seqData.set(i, base);
        }
        //setPercentRed();
        return new SSequencingReadImpl(item.getDescription(),
                new NucleotideSQPair(new NucleotideSequence(seqData),
                        new SequenceQualityPhred(quality)), id);
    }

    /*public void setPercentRed() {
        try {
            percentRed = (int) (channel.position() * 100 / channel.size());
        } catch (IOException ex) {
            Logger.getLogger(FastaReader.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException();
        }
    }

    @Override
    public Status getStatus() {
        if (ended)
            return new Status();
        return new Status("Reading sequences", percentRed);
    }*/
}
