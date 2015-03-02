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
package com.milaboratory.mitcr.clonegenerator;

import cc.redberry.pipe.CUtils;
import cc.redberry.pipe.OutputPort;
import com.milaboratory.core.clone.Clone;
import com.milaboratory.core.clone.CloneSet;
import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.Species;
import com.milaboratory.core.sequence.quality.QualityFormat;
import com.milaboratory.core.sequencing.io.SSequencingDataReader;
import com.milaboratory.core.sequencing.io.fastq.SFastqReader;
import com.milaboratory.core.sequencing.read.SSequencingRead;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractorFromSRead;
import com.milaboratory.mitcr.qualitystrategy.IlluminaQualityInterpretationStrategy;
import com.milaboratory.mitcr.vdjmapping.AbstractMapperTest;
import com.milaboratory.util.CompressionType;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.milaboratory.mitcr.MiTCRTestUtils.getFlexCDR3ExtractionParameters;
import static com.milaboratory.mitcr.MiTCRTestUtils.getReaderFromString;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class LQMappingCloneGeneratorTest extends AbstractMapperTest {
    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void test0() throws InterruptedException, IOException, URISyntaxException {
        File sample = new File(ClassLoader.getSystemResource("cdr3_sample.fastq.gz").toURI());
        SSequencingDataReader reader = new SFastqReader(sample, QualityFormat.Phred33, CompressionType.GZIP);

        CDR3ExtractorFromSRead extractor = new CDR3ExtractorFromSRead(Species.HomoSapiens, Gene.TRB,
                getFlexCDR3ExtractionParameters(), library,
                new IlluminaQualityInterpretationStrategy((byte) 25));

        OutputPort<CDR3ExtractionResult<SSequencingRead>> results = CUtils.wrap(reader, extractor);

        CDR3ExtractionResult<SSequencingRead> result;

        TestCGListener listener = new TestCGListener();

        CloneGenerator generator = new LQMappingCloneGenerator(AccumulatorCloneMaxStrict.FACTORY,
                0.7f, false, new IlluminaQualityInterpretationStrategy((byte) 25),
                3, true, listener);

        int count = 0;
        while ((result = results.take()) != null) {
            if (result.getCDR3() == null)
                continue;
            count++;

            generator.put(result);
        }

        generator.put(null);

        CloneSet cloneSet = generator.getCloneSet();

        assertEquals(46, cloneSet.getClones().size());

        assertEquals(46, listener.getClonesCreated());

        assertEquals(193, count);

        assertEquals(193, listener.getReadsAssignedTotal() + listener.getReadsDropped());

        double sum = 0.0;
        for (Clone c : cloneSet.getClones()) {
            count -= c.getCount();
            sum += c.getPart();
        }

        //Some reads not mapped
        assertTrue(0 < count);

        assertEquals(sum, 1.0, 1E-10);
    }

    //TGCAGCGCACACATGAACACTGAAGCTTTCTTT
    public static final String centralCloneRead1 = "@HWUSI-EAS1814:40:1:2:112:4576:3769 1:N:0:CGATGT\n" +
            //                                                  |--------------CDR3-------------|
            //                                                  |           |                   |
            //                                                  AAAGAAAGCTTCTGTGTTCATGTGTGCGCTGCA
            "TAGTCACACCTTGTTCAGGTCCTCTACAACTGTGAGTCTGGTGCCTTGTCCAAAGAAAGCTTCTGTGTTCATGTGTGCGCTGCAGAGATATATGCTGCTGTCTTCAGGGCTCATGTTGCTCACAGTCAGAGTTGAGAATGTTAGGTTTGGG\n" +
            "+\n" +
            "HIIIIIIIIIIIIIIIIIIIIIIIIGG4GGGGDGGIIIIIIIIIIDIIIIIIIHDIFIIGIIIIIIIHIIIIIIIHIIFIIIIFIBIBIIFIIGEIHEDIIHIIGB832DDGDBDEEHEBC@EBCDDBE:?4AA=?7;2?9548<6>??80\n";
    public static final String centralCloneRead2 = "@HWUSI-EAS1814:40:1:2:112:4576:3769 1:N:0:CGATGT\n" +
            //                                                  |--------------CDR3-------------|
            //                                                  |                       |       |
            //                                                  AAAGAAAGCTTCAGTGTTCATGTGAGCGCTGCA
            "TAGTCACACCTTGTTCAGGTCCTCTACAACTGTGAGTCTGGTGCCTTGTCCAAAGAAAGCTTCAGTGTTCATGTGAGCGCTGCAGAGATATATGCTGCTGTCTTCAGGGCTCATGTTGCTCACAGTCAGAGTTGAGAATGTTAGGTTTGGG\n" +
            "+\n" +
            "HIIIIIIIIIIIIIIIIIIIIIIIIGG4GGGGDGGIIIIIIIIIIDIIIIIIIHDIFIIGIIIIIIIHIIIIIIIHIIFIIIIFIBIBIIFIIGEIHEDIIHIIGB832DDGDBDEEHEBC@EBCDDBE:?4AA=?7;2?9548<6>??80\n";

    public static final String centralCloneRead3 = "@HWUSI-EAS1814:40:1:2:112:4576:3769 1:N:0:CGATGT\n" +
            //                                                  |--------------CDR3-------------|
            //                                                  |               |       |       |
            //                                                  AAAGAAAGCTTCAGTGGTCATGTGAGCGCTGCA
            "TAGTCACACCTTGTTCAGGTCCTCTACAACTGTGAGTCTGGTGCCTTGTCCAAAGAAAGCTTCAGTGGTCATGTGAGCGCTGCAGAGATATATGCTGCTGTCTTCAGGGCTCATGTTGCTCACAGTCAGAGTTGAGAATGTTAGGTTTGGG\n" +
            "+\n" +
            "HIIIIIIIIIIIIIIIIIIIIIIIIGG4GGGGDGGIIIIIIIIIIDIIIIIIIHDIFIIGIIIIIIIHIIIIIIIHIIFIIIIFIBIBIIFIIGEIHEDIIHIIGB832DDGDBDEEHEBC@EBCDDBE:?4AA=?7;2?9548<6>??80\n";


    public static final String mm1Read1 = "@HWUSI-EAS1814:40:1:2:112:4576:3769 1:N:0:CGATGT\n" +
            //                                                              |
            "TAGTCACACCTTGTTCAGGTCCTCTACAACTGTGAGTCTGGTGCCTTGTCCAAAGAAAGCTTCTGTGTTCATGTGTGCGCTGCAGAGATATATGCTGCTGTCTTCAGGGCTCATGTTGCTCACAGTCAGAGTTGAGAATGTTAGGTTTGGG\n" +
            "+\n" +
            "HIIIIIIIIIIIIIIIIIIIIIIIIGG4GGGGDGGIIIIIIIIIIDIIIIIIIHDIFIIGIII.IIIHIIIIIII.IIFIIIIFIBIBIIFIIGEIHEDIIHIIGB832DDGDBDEEHEBC@EBCDDBE:?4AA=?7;2?9548<6>??80\n";
    public static final String mm1Read2 = "@HWUSI-EAS1814:40:1:2:112:4576:3769 1:N:0:CGATGT\n" +
            //                                                                          |
            "TAGTCACACCTTGTTCAGGTCCTCTACAACTGTGAGTCTGGTGCCTTGTCCAAAGAAAGCTTCAGTGTTCATGTGAGCGCTGCAGAGATATATGCTGCTGTCTTCAGGGCTCATGTTGCTCACAGTCAGAGTTGAGAATGTTAGGTTTGGG\n" +
            "+\n" +
            "HIIIIIIIIIIIIIIIIIIIIIIIIGG4GGGGDGGIIIIIIIIIIDIIIIIIIHDIFIIGIII.IIIHIIIIIII.IIFIIIIFIBIBIIFIIGEIHEDIIHIIGB832DDGDBDEEHEBC@EBCDDBE:?4AA=?7;2?9548<6>??80\n";
    public static final String mm2Read = "@HWUSI-EAS1814:40:1:2:112:4576:3769 1:N:0:CGATGT\n" +
            //                                                              |           |
            "TAGTCACACCTTGTTCAGGTCCTCTACAACTGTGAGTCTGGTGCCTTGTCCAAAGAAAGCTTCCGTGTTCATGTGGGCGCTGCAGAGATATATGCTGCTGTCTTCAGGGCTCATGTTGCTCACAGTCAGAGTTGAGAATGTTAGGTTTGGG\n" +
            "+\n" +
            "HIIIIIIIIIIIIIIIIIIIIIIIIGG4GGGGDGGIIIIIIIIIIDIIIIIIIHDIFIIGIII.IIIHIIIIIII.IIFIIIIFIBIBIIFIIGEIHEDIIHIIGB832DDGDBDEEHEBC@EBCDDBE:?4AA=?7;2?9548<6>??80\n";


    @Test
    public void testProportional() throws Exception {
        //File sample = new File(ClassLoader.getSystemResource("cdr3_sample.fastq.gz").toURI());
        //SSequencingDataReader reader = new SFastqReader(sample, QualityFormat.Illumina18, CompressionType.GZIP);
        //        |           |
        //TGCAGCGCACACATGAACACAGAAGCTTTCTTT - clone 1
        //TGCAGCGCTCACATGAACACTGAAGCTTTCTTT - clone 2
        assertArrayEquals(new long[]{1, 1}, createCountsArray(centralCloneRead1 + centralCloneRead2));
        assertArrayEquals(new long[]{1, 2}, createCountsArray(centralCloneRead1 + centralCloneRead2 + centralCloneRead2));
        assertArrayEquals(new long[]{2, 1}, createCountsArray(centralCloneRead1 + centralCloneRead2 + mm1Read1));
        assertArrayEquals(new long[]{1, 3}, createCountsArray(centralCloneRead1 + centralCloneRead2 + mm1Read2 + mm1Read2));
        assertArrayEquals(new long[]{3, 0}, createCountsArray(centralCloneRead1 + mm1Read2 + mm1Read2));
        assertArrayEquals(new long[]{4, 0}, createCountsArray(centralCloneRead1 + mm1Read2 + mm1Read2 + mm2Read));
        assertArrayEquals(new long[]{0, 3}, createCountsArray(centralCloneRead2 + mm1Read1 + mm1Read1));
        long[] acc = new long[2], result;
        for (int i = 0; i < 100; ++i) {
            result = createCountsArray(centralCloneRead1 + centralCloneRead2 + mm1Read1 + mm1Read2 + mm2Read);
            assertThat(result, anyOf(is(new long[]{3, 2}), is(new long[]{2, 3})));
            acc[0] += result[0];
            acc[1] += result[1];
        }
        assertEquals(500, acc[0] + acc[1]);
        assertTrue(acc[0] > 210);
        assertTrue(acc[0] < 290);
        assertTrue(acc[1] > 210);
        assertTrue(acc[1] < 290);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; ++i)
            sb.append(centralCloneRead1 + centralCloneRead2 + mm1Read1 + mm1Read2 + mm2Read);

        String reads = sb.toString();
        acc = createCountsArray(reads);
        assertEquals(500, acc[0] + acc[1]);
        assertTrue(acc[0] > 210);
        assertTrue(acc[0] < 290);
        assertTrue(acc[1] > 210);
        assertTrue(acc[1] < 290);
    }

    @Ignore
    @Test
    public void testReMapping() throws Exception {
        for (int k = 0; k < 5; ++k) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 50; ++i)
                sb.append(mm1Read1 + mm1Read2 + mm2Read);

            for (int i = 0; i < 50; ++i)
                sb.append(mm1Read1 + mm1Read2 + mm2Read);

            for (int i = 0; i < 100; ++i)
                sb.append(centralCloneRead1 + centralCloneRead2);

            for (int i = 0; i < 100; ++i)
                sb.append(mm1Read1 + mm1Read2 + mm2Read);

            String reads = sb.toString();

            long[] acc = createCountsArray(reads, 1);
            assertEquals(800, acc[0] + acc[1]);
            if (acc[0] > 360 && acc[0] < 440 && acc[1] > 360 && acc[1] < 440)
                return;
        }
        assertTrue(false);
    }

    private final long[] createCountsArray(String readContent) throws Exception {
        return createCountsArray(readContent, LQMappingCloneGenerator.DEFAULT_RE_MAPPING_PERIOD);
    }

    private final long[] createCountsArray(String readContent, int remappingPeriod) throws Exception {
        HashMap<String, Long> counts = createCounts(readContent, remappingPeriod);
        long[] arr = new long[2];
        arr[0] = counts.get("TGCAGCGCACACATGAACACAGAAGCTTTCTTT") == null ? 0 : counts.get("TGCAGCGCACACATGAACACAGAAGCTTTCTTT");
        arr[1] = counts.get("TGCAGCGCTCACATGAACACTGAAGCTTTCTTT") == null ? 0 : counts.get("TGCAGCGCTCACATGAACACTGAAGCTTTCTTT");
        return arr;
    }

    private final HashMap<String, Long> createCounts(String readContent, int remappingPeriod) throws Exception {
        CloneSet cs = createCloneSet(readContent, remappingPeriod);
        HashMap<String, Long> counts = new HashMap<>();
        for (Clone c : cs.getClones())
            counts.put(c.getCDR3().getSequence().toString(), c.getCount());

        return counts;
    }

    private final CloneSet createCloneSet(String readContent, int remappingPeriod) throws Exception {
        ExecutorService es = Executors.newCachedThreadPool();

        SSequencingDataReader reader = getReaderFromString(readContent);

        CDR3ExtractorFromSRead extractor = new CDR3ExtractorFromSRead(Species.HomoSapiens, Gene.TRB,
                getFlexCDR3ExtractionParameters(), library,
                new IlluminaQualityInterpretationStrategy((byte) 25));

        OutputPort<CDR3ExtractionResult<SSequencingRead>> results = CUtils.wrap(reader, extractor);

        CDR3ExtractionResult<SSequencingRead> result;

        LQMappingCloneGenerator generator = new LQMappingCloneGenerator(AccumulatorCloneMaxStrict.FACTORY,
                0.7f, false, new IlluminaQualityInterpretationStrategy((byte) 25), 3, true, es);
        generator.setReMappingCounter(remappingPeriod);

        int count = 0;
        while ((result = results.take()) != null) {
            if (result.getCDR3() == null)
                continue;
            count++;

            generator.put(result);
        }

        generator.put(null);

        es.shutdown();
        assertTrue(es.awaitTermination(1, TimeUnit.SECONDS));
        return generator.getCloneSet();
    }
}
