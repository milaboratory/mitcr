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
package com.milaboratory.mitcr.cdrextraction;

import cc.redberry.pipe.CUtils;
import cc.redberry.pipe.OutputPort;
import com.milaboratory.core.segment.DefaultSegmentLibrary;
import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.Species;
import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequence.aminoacid.AminoAcidAlphabet;
import com.milaboratory.core.sequence.aminoacid.CDRAminoAcidSequence;
import com.milaboratory.core.sequence.quality.QualityFormat;
import com.milaboratory.core.sequencing.io.SSequencingDataReader;
import com.milaboratory.core.sequencing.io.fastq.SFastqReader;
import com.milaboratory.core.sequencing.read.SSequencingRead;
import com.milaboratory.mitcr.qualitystrategy.DummyQualityInterpretationStrategy;
import com.milaboratory.mitcr.qualitystrategy.IlluminaQualityInterpretationStrategy;
import com.milaboratory.mitcr.vdjmapping.AbstractMapperTest;
import com.milaboratory.util.CompressionType;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static com.milaboratory.mitcr.MiTCRTestUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CDR3ExtractorTest extends AbstractMapperTest {
    @Test
    public void testExactMatch() throws Exception {
        SSequencingDataReader reader = getSampleFReader();

        CDR3Extractor<SSequencingRead> extractor = new CDR3ExtractorFromSRead(Species.HomoSapiens, Gene.TRB,
                getFlexCDR3ExtractionParameters(), library,
                new DummyQualityInterpretationStrategy());

        OutputPort<CDR3ExtractionResult<SSequencingRead>> results = CUtils.wrap(reader, extractor);

        CDR3ExtractionResult<SSequencingRead> result;

        int count = 0;
        while ((result = results.take()) != null) {
            if (result.getCDR3() == null)
                continue;
            count++;
            CDRAminoAcidSequence aaSequence = new CDRAminoAcidSequence(result.getCDR3().getSequence());
            assertEquals(aaSequence.codeAt(0), AminoAcidAlphabet.C);
            assertEquals(aaSequence.codeAt(aaSequence.size() - 1), AminoAcidAlphabet.F);
            //System.out.println(result.getCDR3() != null ? new CDRAminoAcidSequence(result.getCDR3().getSequence()) : null);
        }

        assertEquals(194, count);
    }

    @Test
    public void testIllumina() throws URISyntaxException, IOException, InterruptedException {
        File sample = new File(ClassLoader.getSystemResource("cdr3_sample.fastq.gz").toURI());
        SSequencingDataReader reader = new SFastqReader(sample, QualityFormat.Phred33, CompressionType.GZIP);

        CDR3Extractor<SSequencingRead> extractor;
        extractor = new CDR3ExtractorFromSRead(Species.HomoSapiens, Gene.TRB,
                getFlexCDR3ExtractionParameters(), library,
                new IlluminaQualityInterpretationStrategy((byte) 25));

        OutputPort<CDR3ExtractionResult<SSequencingRead>> results = CUtils.wrap(reader, extractor);

        CDR3ExtractionResult<SSequencingRead> result;

        int count = 0;
        TLongArrayList list = new TLongArrayList();
        while ((result = results.take()) != null) {
            if (result.getCDR3() == null)
                continue;
            count++;
            list.add(result.getSource().id());
            //CDRAminoAcidSequence aaSequence = new CDRAminoAcidSequence(result.getCDR3().getSequence());
            //assertEquals(aaSequence.codeAt(0), AminoAcidAlphabet.C);
            //assertEquals(aaSequence.codeAt(aaSequence.size() - 1), AminoAcidAlphabet.F);
            //System.out.println(result.getCDR3() != null ? new CDRAminoAcidSequence(result.getCDR3().getSequence()) : null);
        }

        System.out.println(list);
        assertEquals(193, count);
    }

    @Test
    public void testDifference() throws Exception {
        File sample = new File(ClassLoader.getSystemResource("cdr3_sample.fastq.gz").toURI());
        SSequencingDataReader reader = new SFastqReader(sample, QualityFormat.Phred33, CompressionType.GZIP);

        CDR3Extractor<SSequencingRead> extractor = new CDR3ExtractorFromSRead(Species.HomoSapiens, Gene.TRB,
                getFlexCDR3ExtractionParameters(), library,
                new IlluminaQualityInterpretationStrategy((byte) 25));

        OutputPort<CDR3ExtractionResult<SSequencingRead>> results = CUtils.wrap(reader, extractor);

        CDR3ExtractionResult<SSequencingRead> result;

        TLongSet set = new TLongHashSet(new long[]{0, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12, 13, 14, 15, 16, 17, 18, 20, 21, 22,
                23, 26, 28, 29, 31, 32, 34, 35, 36, 37, 39, 40, 43, 44, 45, 46, 47, 48, 49, 52, 53, 55, 56, 57,
                58, 59, 60, 61, 62, 66, 68, 69, 71, 74, 75, 77, 78, 79, 80, 81, 84, 85, 86, 87, 88, 89, 90, 92, 93,
                94, 95, 96, 97, 98, 99, 100, 103, 104, 106, 107, 108, 109, 110, 112, 113, 115, 116, 117, 118, 119, 120,
                121, 122, 123, 127, 128, 129, 130, 131, 132, 133, 134, 135, 139, 140, 141, 142, 143, 144, 146, 147, 148,
                151, 153, 154, 155, 157, 158, 159, 160, 162, 163, 164, 165, 166, 167, 169, 170, 171, 172, 174, 175,
                176, 177, 178, 179, 180, 182, 183, 184, 185, 186, 187, 188, 189, 190, 192, 193, 194, 197, 198, 199,
                200, 201, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 218, 219, 220, 221, 222, 224, 225, 226,
                227, 228, 229, 230, 231, 232, 233, 234, 236, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249}
        );

        int count = 0;

        //SFastqWriter writer = new SFastqWriter(System.out, QualityFormat.Illumina18, CompressionType.None);

        while ((result = results.take()) != null) {
            if (result.getCDR3() == null)
                continue;
            count++;

            if (!set.remove(result.getSource().id())) {
                NucleotideSQPair sequence = result.getSource().getData();
                if (result.isFoundInReverseComplement())
                    sequence = sequence.getRC();

                String seq = sequence.getSequence().toString().toLowerCase();
                CDRAminoAcidSequence aaSequence = new CDRAminoAcidSequence(result.getCDR3().getSequence());
                System.out.println(aaSequence);
                System.out.println(seq.substring(0, result.getVMappingResult().getRefPoint() - 10) +
                        seq.substring(result.getVMappingResult().getRefPoint() - 10, result.getVMappingResult().getRefPoint() + 10).toUpperCase() +
                        seq.substring(result.getVMappingResult().getRefPoint() + 10));
                System.out.println(sequence.getQuality());
            }

        }

        assertTrue(set.isEmpty());
        assertEquals(193, count);
    }

    @Test
    public void test2() throws Exception {
        SSequencingDataReader reader = getSampleTReader();

        CDR3Extractor<SSequencingRead> extractor = new CDR3ExtractorFromSRead(Species.HomoSapiens, Gene.TRB,
                getFlexCDR3ExtractionParameters(), DefaultSegmentLibrary.load(),
                new IlluminaQualityInterpretationStrategy((byte) 25));
        //new DummyQualityInterpretationStrategy());

        OutputPort<CDR3ExtractionResult<SSequencingRead>> results = CUtils.wrap(reader, extractor);

        CDR3ExtractionResult<SSequencingRead> result;

        int count = 0;
        while ((result = results.take()) != null) {
            if (result.getCDR3() != null)
                ++count;
        }

        Assert.assertEquals(163, count);
    }

    @Test
    public void test1() throws Exception {
        SSequencingDataReader reader = getSampleFReader();

        CDR3Extractor<SSequencingRead> extractor = new CDR3ExtractorFromSRead(Species.HomoSapiens, Gene.TRB,
                getFlexCDR3ExtractionParameters(), DefaultSegmentLibrary.load(),
                new IlluminaQualityInterpretationStrategy((byte) 20));
        //new DummyQualityInterpretationStrategy());

        OutputPort<CDR3ExtractionResult<SSequencingRead>> results = CUtils.wrap(reader, extractor);

        CDR3ExtractionResult<SSequencingRead> result;

        int count = 0;
        while ((result = results.take()) != null) {
            if (result.getCDR3() != null)
                ++count;
        }

        Assert.assertEquals(193, count);
    }
}
