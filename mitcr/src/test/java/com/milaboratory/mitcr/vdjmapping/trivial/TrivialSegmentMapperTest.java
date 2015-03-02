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
package com.milaboratory.mitcr.vdjmapping.trivial;

import com.milaboratory.core.segment.SegmentGroup;
import com.milaboratory.core.segment.SegmentGroupContainer;
import com.milaboratory.core.segment.Species;
import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.mitcr.pipeline.ParameterPresets;
import com.milaboratory.mitcr.pipeline.Parameters;
import com.milaboratory.mitcr.qualitystrategy.DummyQualityInterpretationStrategy;
import com.milaboratory.mitcr.qualitystrategy.GoodBadNucleotideSequence;
import com.milaboratory.mitcr.qualitystrategy.IlluminaQualityInterpretationStrategy;
import com.milaboratory.mitcr.vdjmapping.AlignmentDirection;
import com.milaboratory.mitcr.vdjmapping.VJSegmentMappingResult;
import com.milaboratory.mitcr.vdjmapping.VJSegmentMappingResultFormatter;
import com.milaboratory.mitcr.vdjmapping.tree.AbstractSingleMapperTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class TrivialSegmentMapperTest extends AbstractSingleMapperTest {
    @Test
    public void testFirst() throws Exception {
        NucleotideSQPair testSequence = new NucleotideSQPair(
                new NucleotideSequence("TCTGAGTCAACAGTCTCCAGAATAAGGACGGAGCATTTTCCCCTGACCCTGGAG" +
                        //                                TGTGCCAGCAGCGAGAGACAGGGGACGCAGTATTTT
                        "TCTGCCAGGCCCTCACATACCTCTCAGTCCCTCTGTGCCAGCAGCGAGAGACAGGGGACGCAGTATTTTGGCCCAGGCACCCGGCTGACAGTGCTC"));


        Parameters params = ParameterPresets.getFlex();
        IlluminaQualityInterpretationStrategy ill = new IlluminaQualityInterpretationStrategy((byte) 25);
        //CDR3ExtractorFromSQPair extractor = new CDR3ExtractorFromSQPair(Species.HomoSapiens, Gene.TRB, params.getCDR3ExtractorParameters(), library, ill);
        //CDR3ExtractionResult result = extractor.process(testSequence);

        GoodBadNucleotideSequence sequence = ill.getProviderForNucleotideSQPair().process(testSequence);
        //new VJSegmentMapperParameters(AlignmentDirection.Both, -1, 4, 12, -2);
        TrivialSegmentMapper mapper = new TrivialSegmentMapper(library.getGroup(Species.HomoSapiens, SegmentGroup.TRBJ),
                -1, 4, 12, 2, AlignmentDirection.Both);
        VJSegmentMappingResult resultVJ = mapper.map(sequence);
        int i = 0;
    }

    @Test
    public void testVBorder1() throws Exception {
        NucleotideSQPair vCDR3Norm = new NucleotideSQPair(
                //                                   |0        |10       |20       |30       |40       |50       |60
                //                                                               VVVVVVVVVVVVV.............................
                //                          GGAGTTGGCTGCTCCCTCCCAGACATCTGTGTACTTCTGTGCCAGCAGT       -  TRBV6-6*02
                //                          GCTGTCGGCTGCTCCCTCCCAGACATCTGTGTACTTCTGTGCCAGCAGTTactc  -  TRBV6-5*01
                new NucleotideSequence("TGCTCCCTCCCAGACATCTGTGTACTTCTGTGCCAGCAGTTCCCTAGCGGGAGAGCACGAGCAGTACTTC"));

        IlluminaQualityInterpretationStrategy ill = new IlluminaQualityInterpretationStrategy((byte) 25);
        VJSegmentMappingResult resultVJ;
        TrivialSegmentMapper mapper = new TrivialSegmentMapper(library.getGroup(Species.HomoSapiens, SegmentGroup.TRBV),
                -1, 4, 12, 2, AlignmentDirection.Both);
        resultVJ = mapper.map(ill.getProviderForNucleotideSQPair().process(vCDR3Norm));
        assertThat(resultVJ.getRefPoint(), is(31));
        assertThat(resultVJ.getSegmentBorderFrom(), is(0));
        assertThat(resultVJ.getSegmentBorderTo(), is(40));

        mapper = new TrivialSegmentMapper(library.getGroup(Species.HomoSapiens, SegmentGroup.TRBV),
                -1, 4, 7, 1, AlignmentDirection.InsideCDR3);
        resultVJ = mapper.map(ill.getProviderForNucleotideSQPair().process(vCDR3Norm));
        assertThat(resultVJ.getRefPoint(), is(31));
        assertThat(resultVJ.getSegmentBorderTo(), is(40));
    }

    @Test
    public void testVBorder2mm() throws Exception {
        NucleotideSQPair vCDR3Norm = new NucleotideSQPair(
                //                                   |0        |10       |20       |30       |40       |50       |60
                //                                                               VVVVVVVVVVVVV.............................
                //                          GGAGTTGGCTGCTCCCTCCCAGACATCTGTGTACTTCTGTGCCAGCAGT       -  TRBV6-6*02
                //                          GCTGTCGGCTGCTCCCTCCCAGACATCTGTGTACTTCTGTGCCAGCAGTTactc  -  TRBV6-5*01
                new NucleotideSequence("TGCTCCCTCCCAGACATCTGTGTACTTCTGTGCCAGAAGTTCCCTAGCGGGAGAGCACGAGCAGTACTTC"));

        IlluminaQualityInterpretationStrategy ill = new IlluminaQualityInterpretationStrategy((byte) 25);
        VJSegmentMappingResult resultVJ;
        TrivialSegmentMapper mapper = new TrivialSegmentMapper(library.getGroup(Species.HomoSapiens, SegmentGroup.TRBV),
                -1, 4, 12, 2, AlignmentDirection.Both);
        resultVJ = mapper.map(ill.getProviderForNucleotideSQPair().process(vCDR3Norm));
        assertThat(resultVJ.getRefPoint(), is(31));
        assertThat(resultVJ.getSegmentBorderFrom(), is(0));
        assertThat(resultVJ.getSegmentBorderTo(), is(40));

        mapper = new TrivialSegmentMapper(library.getGroup(Species.HomoSapiens, SegmentGroup.TRBV),
                -1, 4, 7, 1, AlignmentDirection.InsideCDR3);
        resultVJ = mapper.map(ill.getProviderForNucleotideSQPair().process(vCDR3Norm));
        assertThat(resultVJ.getRefPoint(), is(31));
        assertThat(resultVJ.getSegmentBorderTo(), is(40));
    }

    @Test
    public void testVBorder3mm() throws Exception {
        NucleotideSQPair vCDR3Norm = new NucleotideSQPair(
                //                                   |0        |10       |20       |30       |40       |50       |60
                //                                                               VVVVVVVVVVVVV.............................
                //                          GGAGTTGGCTGCTCCCTCCCAGACATCTGTGTACTTCTGTGCCAGCAGT       -  TRBV6-6*02
                //                          GCTGTCGGCTGCTCCCTCCCAGACATCTGTGTACTTCTGTGCCAGCAGTTactc  -  TRBV6-5*01
                new NucleotideSequence("TGCTCCCTCCCAGACATCTGTGTACTTCTGTGCCAGCACTTCCCTAGCGGGAGAGCACGAGCAGTACTTC"));

        IlluminaQualityInterpretationStrategy ill = new IlluminaQualityInterpretationStrategy((byte) 25);
        VJSegmentMappingResult resultVJ;
        TrivialSegmentMapper mapper = new TrivialSegmentMapper(library.getGroup(Species.HomoSapiens, SegmentGroup.TRBV),
                -1, 4, 12, 2, AlignmentDirection.Both);
        resultVJ = mapper.map(ill.getProviderForNucleotideSQPair().process(vCDR3Norm));
        assertThat(resultVJ.getRefPoint(), is(31));
        assertThat(resultVJ.getSegmentBorderFrom(), is(0));
        assertThat(resultVJ.getSegmentBorderTo(), is(37));

        mapper = new TrivialSegmentMapper(library.getGroup(Species.HomoSapiens, SegmentGroup.TRBV),
                -1, 4, 7, 1, AlignmentDirection.InsideCDR3);
        resultVJ = mapper.map(ill.getProviderForNucleotideSQPair().process(vCDR3Norm));
        //System.out.print(VJSegmentMappingResultFormatter.format(vCDR3Norm.getSequence(), resultVJ));
        assertThat(resultVJ.getRefPoint(), is(31));
        assertThat(resultVJ.getSegmentBorderTo(), is(38));
    }

    @Test
    public void testFindV() throws Exception {
        NucleotideSQPair vCDR3Norm = new NucleotideSQPair(
                //TRBV6-6*02
                //                                   |0        |10       |20       |30       |40       |50       |60
                //                                                               VVVVVVVVVVVVV.............................
                //                          GGAGTTGGCTGCTCCCTCCCAGACATCTGTGTACTTCTGTGCCAGCAGT       -
                //                          GCTGTCGGCTGCTCCCTCCCAGACATCTGTGTACTTCTGTGCCAGCAGTTactc  -  TRBV6-5*01
                new NucleotideSequence("TGCTCCCTCCCAGACATCTGTGTACTTCTGTGCCAGCAGTTCTCTAGCGGGAGAGCACGAGCAGTACTTC")),
                vCDR3Chimera1 = new NucleotideSQPair(
                        //                                   ??????????                            VVVVVVVVVVVV..............................
                        new NucleotideSequence("GTACTTCTGTTGCTCCCTCCCAGACATCTGTGTACTTCTGTGCCAGCAGTTCTCTAGCGGGAGAGCACGAGCAGTACTTC")),
                vCDR3Chimera2 = new NucleotideSQPair(
                        //                                   ????????????                            VVVVVVVVVVVV..............................
                        new NucleotideSequence("TGTGCCAGCAGTTGCTCCCTCCCAGACATCTGTGTACTTCTGTGCCAGCAGTTCTCTAGCGGGAGAGCACGAGCAGTACTTC")),
                vCDR3Chimera3 = new NucleotideSQPair(
                        //                                   ???????????????????                            VVVVVVVVVVVV..............................
                        new NucleotideSequence("TGTGTACTTCTGTGCCAGCTGCTCCCTCCCAGACATCTGTGTACTTCTGTGCCAGCAGTTCTCTAGCGGGAGAGCACGAGCAGTACTTC")),
                vCDR3Chimera4 = new NucleotideSQPair(
                        //                                   ???????????????????             *         *    VVVVVVVVVVVV..............................
                        new NucleotideSequence("TGTGTACTTCTGTGCCAGCTGCTCCCTCCCAGTCATCTGTGTTCTTCTGTGCCAGCAGTTCTCTAGCGGGAGAGCACGAGCAGTACTTC"));


        IlluminaQualityInterpretationStrategy ill = new IlluminaQualityInterpretationStrategy((byte) 25);
        TrivialSegmentMapper mapper = new TrivialSegmentMapper(library.getGroup(Species.HomoSapiens, SegmentGroup.TRBV),
                -1, 4, 12, 2, AlignmentDirection.Both);

        //mapAndPrint(mapper, vCDR3Norm);
        //mapAndPrint(mapper, vCDR3Chimera1);
        //mapAndPrint(mapper, vCDR3Chimera2);
        //mapAndPrint(mapper, vCDR3Chimera3);
        //mapAndPrint(mapper, vCDR3Chimera4);

        VJSegmentMappingResult resultVJ;
        resultVJ = mapper.map(ill.getProviderForNucleotideSQPair().process(vCDR3Norm));

        //System.out.print(VJSegmentMappingResultFormatter.format(vCDR3Norm.getSequence(), resultVJ));
        //System.out.println(container.getAllele(resultVJ.getBarcode().getBits()[0]).getSequence());
        assertThat(resultVJ.getRefPoint(), is(31));
        assertThat(resultVJ.getSegmentBorderFrom(), is(0));
        assertThat(resultVJ.getSegmentBorderTo(), is(40));

        resultVJ = mapper.map(ill.getProviderForNucleotideSQPair().process(vCDR3Chimera1));
        assertEquals(50, resultVJ.getSegmentBorderTo());
        resultVJ = mapper.map(ill.getProviderForNucleotideSQPair().process(vCDR3Chimera2));
        assertEquals(52, resultVJ.getSegmentBorderTo());
        resultVJ = mapper.map(ill.getProviderForNucleotideSQPair().process(vCDR3Chimera3));
        //System.out.print(VJSegmentMappingResultFormatter.format(vCDR3Chimera3.getSequence(), resultVJ));
        assertEquals(59, resultVJ.getSegmentBorderTo());
        resultVJ = mapper.map(ill.getProviderForNucleotideSQPair().process(vCDR3Chimera4));
        assertEquals(59, resultVJ.getSegmentBorderTo());
    }

    public static String seqInt = "AGATCCAGCCCTCAGAACCCAGGGACTCAGCTGTGTACTTCTGTGCCAGCACCGTGGACAGTCTGAACACTGAAGTTTTCTTTGGACAAGGCACCAGCGT";

    @Test
    public void testInt() throws Exception {
        IlluminaQualityInterpretationStrategy ill = new IlluminaQualityInterpretationStrategy((byte) 25);
        SegmentGroupContainer container = library.getGroup(Species.HomoSapiens, SegmentGroup.TRBJ);
        TrivialSegmentMapper mapper = new TrivialSegmentMapper(container,
                -1, 4, 12, 2, AlignmentDirection.Both);

        NucleotideSQPair sequence = new NucleotideSQPair(new NucleotideSequence(seqInt));

        VJSegmentMappingResult resultVJ;

        resultVJ = mapper.map(ill.getProviderForNucleotideSQPair().process(sequence));
        //System.out.print(VJSegmentMappingResultFormatter.format(sequence.getSequence(), resultVJ));
    }

    private static void mapAndPrint(TrivialSegmentMapper mapper, NucleotideSQPair pair) {
        VJSegmentMappingResult resultVJ = mapper.map(DummyQualityInterpretationStrategy.createForNucleotideSQPair().process(pair));
        System.out.println(VJSegmentMappingResultFormatter.format(pair.getSequence(), resultVJ));
    }

    @Test
    public void testFindJ() throws Exception {
        NucleotideSQPair jCDR3Norm = new NucleotideSQPair(
                //                                                               |28       |38                           |69
                //                                   |0        |10       |20       |30       |40       |50       |60
                //                                   ............................JJJJJJJJJJJJJJJ
                //                                                          CTCCTACGAGCAGTACTTCGGGCCGGGCACCAGGCTCACGGTCACAG  -  TRBJ2-7*01
                new NucleotideSequence("TGTGCCAGCAGTTCTCTAGCGGGAGAGCACGAGCAGTACTTCGGGCCGGGCACCAGGCTCACGGTCACAG")),
                jCDR3Chimera1 = new NucleotideSQPair(
                        //                                   |0        |10       |20       |30       |40       |50       |60       |70       |80
                        //                                                     ...........................JJJJJJJJJJJJJJJ
                        new NucleotideSequence("CCAGGCTCACGGTCACAGTGTGCCAGCAGTTCTCTAGCGGGAGAGCACGAGCAGTACTTCGGGCCGGGCACCAGGCTCACGGTCACAG")),
                jCDR3MM1 = new NucleotideSQPair(
                        //                                   |0        |10       |20       |30       |40       |50       |60
                        //                                   ...........................JJJ*JJJJJJJJJJJ
                        new NucleotideSequence("TGTGCCAGCAGTTCTCTAGCGGGAGAGCACAAGCAGTACTTCGGGCCGGGCACCAGGCTCACGGTCACAG")),
                jCDR3MM2 = new NucleotideSQPair(
                        //                                   ...........................JJJ*JJJJJJ*JJJJ
                        new NucleotideSequence("TGTGCCAGCAGTTCTCTAGCGGGAGAGCACGAGCAGTGCTTCGGGCCGGGCACCAGGCTCACGGTCACAG")),
                jCDR3MM3 = new NucleotideSQPair(
                        //                                   ...........................JJJ*JJJJJJJJJJJ
                        new NucleotideSequence("TGTGCCAGCAGTTCTCTAGCGGGAGAGCACGAGGAGTACTTCGGGCCGGGCACCAGGCTCACGGTCACAG")),
                jCDR3Chimera4 = new NucleotideSQPair(
                        //                                   ??????????????????...........................JJJJJJJJJJJJJJJ
                        new NucleotideSequence("CTTCGGGCCGGGCGCCAATGTGCCAGCAGTTCTCTAGCGGGAGAGCACGAGCAGTACTTCGGG"));


        IlluminaQualityInterpretationStrategy ill = new IlluminaQualityInterpretationStrategy((byte) 25);
        SegmentGroupContainer container = library.getGroup(Species.HomoSapiens, SegmentGroup.TRBJ);
        TrivialSegmentMapper mapper = new TrivialSegmentMapper(container,
                -1, 4, 12, 2, AlignmentDirection.Both);
        VJSegmentMappingResult resultVJ;
        resultVJ = mapper.map(ill.getProviderForNucleotideSQPair().process(jCDR3Norm));
        //System.out.println(container.getAllele(resultVJ.getBarcode().getBits()[0]).getFullName());
        assertEquals(69, resultVJ.getSegmentBorderTo());
        assertEquals(28, resultVJ.getSegmentBorderFrom());
        assertEquals(38, resultVJ.getRefPoint());
        resultVJ = mapper.map(ill.getProviderForNucleotideSQPair().process(jCDR3Chimera1));
        assertEquals(46, resultVJ.getSegmentBorderFrom());
        resultVJ = mapper.map(ill.getProviderForNucleotideSQPair().process(jCDR3MM1));
        //assertEquals(28, resultVJ.getSegmentBorderFrom());
        assertEquals(31, resultVJ.getSegmentBorderFrom());
        resultVJ = mapper.map(ill.getProviderForNucleotideSQPair().process(jCDR3MM2));
        assertEquals(null, resultVJ);
        resultVJ = mapper.map(ill.getProviderForNucleotideSQPair().process(jCDR3MM3));
        //System.out.print(VJSegmentMappingResultFormatter.format(jCDR3MM3.getSequence(), resultVJ));
        assertEquals(28, resultVJ.getSegmentBorderFrom());
        resultVJ = mapper.map(ill.getProviderForNucleotideSQPair().process(jCDR3Chimera4));
        assertEquals(46, resultVJ.getSegmentBorderFrom());
    }
}
