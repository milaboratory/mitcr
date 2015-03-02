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
import com.milaboratory.core.segment.DefaultSegmentLibrary;
import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.Species;
import com.milaboratory.core.sequencing.io.SSequencingDataReader;
import com.milaboratory.core.sequencing.read.SSequencingRead;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractorFromSRead;
import com.milaboratory.mitcr.qualitystrategy.IlluminaQualityInterpretationStrategy;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

import static com.milaboratory.mitcr.MiTCRTestUtils.getFlexCDR3ExtractionParameters;
import static com.milaboratory.mitcr.MiTCRTestUtils.getReaderFromString;
import static com.milaboratory.mitcr.clonegenerator.LQMappingCloneGeneratorTest.*;

public class LQFilteringOffCloneGeneratorTest {
    @Test
    public void test1() throws Exception {
        Assert.assertArrayEquals(new long[]{1, 1}, createCountsArray(centralCloneRead1 +
                centralCloneRead2));
        Assert.assertArrayEquals(new long[]{1, 1}, createCountsArray(centralCloneRead1 +
                centralCloneRead2 + mm1Read2 + mm1Read1));
    }

    private final long[] createCountsArray(String readContent) throws Exception {
        HashMap<String, Long> counts = createCounts(readContent);
        long[] arr = new long[2];
        arr[0] = counts.get("TGCAGCGCACACATGAACACAGAAGCTTTCTTT") == null ? 0 : counts.get("TGCAGCGCACACATGAACACAGAAGCTTTCTTT");
        arr[1] = counts.get("TGCAGCGCTCACATGAACACTGAAGCTTTCTTT") == null ? 0 : counts.get("TGCAGCGCTCACATGAACACTGAAGCTTTCTTT");
        return arr;
    }

    private final HashMap<String, Long> createCounts(String readContent) throws Exception {
        CloneSet cs = createCloneSet(readContent);
        HashMap<String, Long> counts = new HashMap<>();
        for (Clone c : cs.getClones())
            counts.put(c.getCDR3().getSequence().toString(), c.getCount());

        return counts;
    }

    private final CloneSet createCloneSet(String readContent) throws Exception {
        SSequencingDataReader reader = getReaderFromString(readContent);

        CDR3ExtractorFromSRead extractor = new CDR3ExtractorFromSRead(Species.HomoSapiens, Gene.TRB,
                getFlexCDR3ExtractionParameters(), DefaultSegmentLibrary.load(),
                new IlluminaQualityInterpretationStrategy((byte) 25));

        OutputPort<CDR3ExtractionResult<SSequencingRead>> results = CUtils.wrap(reader, extractor);

        CDR3ExtractionResult<SSequencingRead> result;

        TestCGListener listener = new TestCGListener();

        LQFilteringOffCloneGenerator generator = new LQFilteringOffCloneGenerator(AccumulatorCloneMaxStrict.FACTORY,
                .15f, false, new IlluminaQualityInterpretationStrategy((byte) 25), listener);

        int count = 0;
        while ((result = results.take()) != null) {
            if (result.getCDR3() == null)
                continue;
            count++;

            generator.put(result);
        }

        generator.put(null);
        CloneSet cs = generator.getCloneSet();

        Assert.assertEquals(count, listener.getReadsDropped() + listener.getReadsAssignedTotal());

        return cs;
    }
}
