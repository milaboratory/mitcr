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

import cc.redberry.pipe.OutputPort;
import cc.redberry.pipe.blocks.ParallelProcessor;
import com.milaboratory.core.segment.DefaultSegmentLibrary;
import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.Species;
import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequence.aminoacid.AminoAcidAlphabet;
import com.milaboratory.core.sequence.aminoacid.CDRAminoAcidSequence;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.core.sequence.quality.QualityFormat;
import com.milaboratory.core.sequencing.io.SSequencingDataReader;
import com.milaboratory.core.sequencing.io.fastq.SFastqReader;
import com.milaboratory.core.sequencing.read.SSequencingRead;
import com.milaboratory.mitcr.pipeline.ParameterPresets;
import com.milaboratory.mitcr.qualitystrategy.DummyQualityInterpretationStrategy;
import com.milaboratory.mitcr.vdjmapping.AbstractMapperTest;
import com.milaboratory.util.CompressionType;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.milaboratory.mitcr.MiTCRTestUtils.getFlexCDR3ExtractionParameters;
import static org.junit.Assert.assertEquals;

public class CDR3ExtractorFactoryTest extends AbstractMapperTest {
    @Test
    public void testEmptyBarcode() throws Exception {
        String sequenceString = "TTTTGATTGCTGGCACAGAAGTACACAGATGTCTGGGAGGGAGCAGCCGACTCCAGCCTGAGCGAGAACTCC";
        NucleotideSequence sequence = new NucleotideSequence(sequenceString);
        NucleotideSQPair pair = new NucleotideSQPair(sequence);
        CDR3ExtractorFromSQPair extractor = new CDR3ExtractorFromSQPair(Species.HomoSapiens, Gene.TRB, ParameterPresets.getFlex().getCDR3ExtractorParameters(),
                DefaultSegmentLibrary.load(), new DummyQualityInterpretationStrategy());

        CDR3ExtractionResult result = extractor.process(pair);

        Assert.assertTrue(result.getVMappingResult().getBarcode().bitCount() > 0);
    }

    @Test
    public void testExactMatch() throws URISyntaxException, IOException, InterruptedException {
        SSequencingDataReader reads =
                new SFastqReader(ClassLoader.getSystemResourceAsStream("cdr3_sample.fastq.gz"),
                        QualityFormat.Phred33, CompressionType.GZIP);

        CDR3ExtractorFactory<SSequencingRead> extractorFactory = new CDR3ExtractorFactoryFromSRead(Species.HomoSapiens, Gene.TRB,
                getFlexCDR3ExtractionParameters(), library,
                new DummyQualityInterpretationStrategy());
        //new IlluminaQualityInterpretationStrategy((byte) 25));

        OutputPort<CDR3ExtractionResult<SSequencingRead>> results = new ParallelProcessor(reads, extractorFactory, 10);

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
}
