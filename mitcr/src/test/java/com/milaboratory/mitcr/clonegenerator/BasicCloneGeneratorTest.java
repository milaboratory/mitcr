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
import com.milaboratory.core.segment.SegmentGroupType;
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
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static com.milaboratory.mitcr.MiTCRTestUtils.getFlexCDR3ExtractionParameters;
import static org.junit.Assert.assertEquals;

public class BasicCloneGeneratorTest extends AbstractMapperTest {
    //Many tests
    @Test
    public void testExactMatch() throws URISyntaxException, IOException, InterruptedException {
        File sample = new File(ClassLoader.getSystemResource("cdr3_sample.fastq.gz").toURI());
        SSequencingDataReader reader = new SFastqReader(sample, QualityFormat.Phred33, CompressionType.GZIP);

        TestCGListener listener = new TestCGListener();

        CDR3ExtractorFromSRead extractor = new CDR3ExtractorFromSRead(Species.HomoSapiens, Gene.TRB,
                getFlexCDR3ExtractionParameters(), library,
                //new DummyQualityInterpretationStrategy());
                new IlluminaQualityInterpretationStrategy((byte) 25));

        OutputPort<CDR3ExtractionResult<SSequencingRead>> results = CUtils.wrap(reader, extractor);

        CDR3ExtractionResult<SSequencingRead> result;

        BasicCloneGenerator generator = new BasicCloneGenerator(AccumulatorCloneMaxStrict.FACTORY, 0.7f, false, listener);

        int count = 0;
        while ((result = results.take()) != null) {
            if (result.getCDR3() == null)
                continue;
            count++;

            generator.put(result);
        }

        generator.put(null);

        CloneSet cloneSet = generator.getCloneSet();

        assertEquals(131, cloneSet.getClones().size());

        assertEquals(131, listener.getClonesCreated());

        assertEquals(193, count);

        assertEquals(193, listener.getReadsAssignedTotal());

        long additionalTotal = 0;
        for (Clone c : cloneSet.getClones()) {
            count -= c.getCount();
            additionalTotal += ((AccumulatorClone) c).additionalCount;
        }

        assertEquals(0, additionalTotal);

        assertEquals(0, count);
    }

    @Test
    public void testCompressed() throws URISyntaxException, IOException, InterruptedException {
        CloneSet csStrict = getCloneSet(AccumulatorCloneMaxStrict.FACTORY),
                csCompressed = getCloneSet(AccumulatorCloneMaxCompressed.FACTORY);

        for (int i = 0; i < csStrict.getClones().size(); ++i) {
            assertEquals(csStrict.getClones().get(i).getAlleles(SegmentGroupType.Variable),
                    csCompressed.getClones().get(i).getAlleles(SegmentGroupType.Variable));
        }
    }

    @Test
    public void testAvrg() throws URISyntaxException, IOException, InterruptedException {
        getCloneSet(AccumulatorCloneAvrgStrict.FACTORY);
        getCloneSet(AccumulatorCloneAvrgCompressed.FACTORY);

        //for (int i = 0; i < csStrict.getClones().size(); ++i) {
        //    assertEquals(csStrict.getClones().get(i).getAlleles(SegmentGroupType.Variable),
        //            csCompressed.getClones().get(i).getAlleles(SegmentGroupType.Variable));
        //}
    }

    private CloneSet getCloneSet(AccumulatorCloneFactory factory) throws URISyntaxException, IOException, InterruptedException {
        File sample = new File(ClassLoader.getSystemResource("cdr3_sample.fastq.gz").toURI());
        SSequencingDataReader reader = new SFastqReader(sample, QualityFormat.Phred33, CompressionType.GZIP);

        TestCGListener listener = new TestCGListener();

        CDR3ExtractorFromSRead extractor = new CDR3ExtractorFromSRead(Species.HomoSapiens, Gene.TRB,
                getFlexCDR3ExtractionParameters(), library,
                //new DummyQualityInterpretationStrategy());
                new IlluminaQualityInterpretationStrategy((byte) 25));

        OutputPort<CDR3ExtractionResult<SSequencingRead>> results = CUtils.wrap(reader, extractor);

        CDR3ExtractionResult<SSequencingRead> result;

        BasicCloneGenerator generator = new BasicCloneGenerator(factory, 0.7f, false, listener);

        int count = 0;
        while ((result = results.take()) != null) {
            if (result.getCDR3() == null)
                continue;
            count++;

            generator.put(result);
        }

        generator.put(null);

        CloneSet cloneSet = generator.getCloneSet();

        assertEquals(131, cloneSet.getClones().size());

        assertEquals(131, listener.getClonesCreated());

        assertEquals(193, count);

        assertEquals(193, listener.getReadsAssignedTotal());

        long additionalTotal = 0;
        for (Clone c : cloneSet.getClones()) {
            count -= c.getCount();
            additionalTotal += ((AccumulatorClone) c).additionalCount;
        }

        assertEquals(0, additionalTotal);

        assertEquals(0, count);

        return cloneSet;
    }
}
