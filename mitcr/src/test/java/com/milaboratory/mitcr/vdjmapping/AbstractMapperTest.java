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
package com.milaboratory.mitcr.vdjmapping;

import com.milaboratory.core.segment.SegmentLibrary;
import com.milaboratory.core.sequencing.io.fastq.SFastqReader;
import com.milaboratory.core.sequence.quality.QualityFormat;
import com.milaboratory.core.sequencing.read.SSequencingRead;
import com.milaboratory.util.CompressionType;
import org.junit.BeforeClass;
import org.junit.Ignore;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mikhail Shugay (mikhail.shugay@gmail.com)
 */
@Ignore
public abstract class AbstractMapperTest {
    protected static SegmentLibrary library;
    /* Some marked reads to use
          TCGGCCGTGTATCTC[TGT]GCCAGCAGC|CCGGACAGGCCTGGGGG|GGACAG|TAT|CTACAATGAGCAGTTC[TTC]GGGCCAGGGACACGGCTCACCGTGCTAG
          GTGCCCATCCTGAAGACAGCAGCTTCTACATC[TGC]AGTG|GGCTGGGATCT|ACAGGG|CCTCCC|AATGAAAAACTGTTT[TTT]GGCAGTGGAACCCAGCTCTC
          CAGCCAGAAGACTCGGCCCTGTATCTC[TGT]GCCAGCAGCCAAG|CT|GGACAG|GATCTCT|CCTACGAGCAGTAC[TTC]GGGCCGGGCACCAGGCTCACGGTCA
          TCTGACGATTCAGCGCACAGAGCAGCGGGACTCAGCCATGTATCGC[TGT]GCCAGCAGCTTAG||AGCGGG||AGACCCAGTAC[TTC]GGGCCAGGCACGCGGCTC
     */
    protected static String readsForTest =
            /**/"@HWI-ST383:166:C0P9TACXX:8:1101:1229:1985 1:N:0:\n" + // No mms
            /**/"TCGGCCGTGTATCTCTGTGCCAGCAGCCCGGACAGGCCTGGGGGGGACAGTATCTACAATGAGCAGTTCTTCGGGCCAGGGACACGGCTCACCGTGCTAG\n+\n" +
            /**/"BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB\n" +

            /**/"@HWI-ST383:166:C0P9TACXX:8:1101:1229:1985 1:N:0:\n" + // Two mms bad qual
            /////TCGGCCGTGTATCTCTGTGCCAGCAGCCCGGACAGGCCTGGGGGGGACAGTATCTACAATGAGCAGTTCTTCGGGCCAGGGACACGGCTCACCGTGCTAG
            /////                    |     |
            /**/"TCGGCCGTGTATCTCTGTGCTAGCAGTCCGGACAGGCCTGGGGGGGACAGTATCTACAATGAGCAGTTCTTCGGGCCAGGGACACGGCTCACCGTGCTAG\n+\n" +
            /////                    |     |
            /**/"BBBBBBBBBBBBBBBBBBBB#BBBBB#BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB\n" +

            /**/"@HWI-ST383:166:C0P9TACXX:8:1101:1229:1985 1:N:0:\n" + // One mm good qual
            /////TCGGCCGTGTATCTCTGTGCCAGCAGCCCGGACAGGCCTGGGGGGGACAGTATCTACAATGAGCAGTTCTTCGGGCCAGGGACACGGCTCACCGTGCTAG
            /////                   |
            /**/"TCGGCCGTGTATCTCTGTGTCAGCAGCCCGGACAGGCCTGGGGGGGACAGTATCTACAATGAGCAGTTCTTCGGGCCAGGGACACGGCTCACCGTGCTAG\n+\n" +
            /////                   |
            /**/"BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB\n" +

            /**/"@HWI-ST383:166:C0P9TACXX:8:1101:1229:1985 1:N:0:\n" + // Good qual both sides
            /////GTGCCCATCCTGAAGACAGCAGCTTCTACATCTGCAGTGGGCTGGGATCTACAGGGCCTCCCAATGAAAAACTGTTTTTTGGCAGTGGAACCCAGCTCTC
            /////                       |||||||||___|||
            /**/"GTGCCCATCCTGAAGACAGCAGCTTCTACATCTGCAGTGGGCTGGGATCTACAGGGCCTCCCAATGAAAAACTGTTTTTTGGCAGTGGAACCCAGCTCTC\n+\n" +
            ////
            /**/"BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB\n";
    protected static SSequencingRead[] reads;


    //protected static SequenceWrapper sequence1VMM, sequence1VMMMarked, sequenceGood, sequence1VInsertion, sequence1VInsertionMarked;

    @BeforeClass
    public static void createSequences() throws Exception {
        File segmentsFile = new File(ClassLoader.getSystemResource("segments_test.gsl").toURI());
        library = SegmentLibrary.readFromFile(segmentsFile);

        SFastqReader reader = new SFastqReader(new ByteArrayInputStream(readsForTest.getBytes()),
                QualityFormat.Phred33, CompressionType.None);

        SSequencingRead read;
        List<SSequencingRead> readsList = new ArrayList<>();
        while ((read = reader.take()) != null)
            readsList.add(read);

        reads = readsList.toArray(new SSequencingRead[readsList.size()]);

        /*sequence1VMMMarked = getWrapper("1VMMSeq.fastq", true);
        sequence1VMM = getWrapper("1VMMSeq.fastq", false);
        sequence1VInsertion = getWrapper("1VInsertionSeq.fastq", false);
        sequence1VInsertionMarked = getWrapper("1VInsertionSeq.fastq", true);
        sequenceGood = getWrapper("goodSeq.fastq", false);*/
    }

    /*private static SequenceWrapper getWrapper(String fileName, boolean mark) {
        try {
            FASTQReaderOld reader = new FASTQReaderOld(fileName, QualityFormat.Illumina15);
            NucleotideSQPair pair = reader.next().getData();
            BitArray ba = new BitArray(pair.size());
            ba.clearAll();
            if (mark)
                for (int i = 0; i < pair.size(); ++i)
                    if (pair.getQuality().value(i) < 10)
                        ba.set(i);
            SequenceWrapper wrapper = new SequenceWrapper(ba, pair);
            reader.close();
            return new SequenceWrapper(wrapper, wrapper.getData().getRC());
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }*/
}
