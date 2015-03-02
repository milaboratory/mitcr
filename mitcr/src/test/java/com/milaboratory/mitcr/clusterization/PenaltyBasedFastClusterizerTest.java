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
package com.milaboratory.mitcr.clusterization;

import cc.redberry.pipe.CUtils;
import cc.redberry.pipe.OutputPort;
import com.milaboratory.core.clone.Clone;
import com.milaboratory.core.clone.CloneCluster;
import com.milaboratory.core.clone.CloneSet;
import com.milaboratory.core.clone.CloneSetClustered;
import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.Species;
import com.milaboratory.core.sequence.util.SequencesUtils;
import com.milaboratory.core.sequencing.io.SSequencingDataReader;
import com.milaboratory.core.sequencing.io.fastq.SFastqReader;
import com.milaboratory.core.sequence.quality.QualityFormat;
import com.milaboratory.core.sequencing.read.SSequencingRead;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractorFromSRead;
import com.milaboratory.mitcr.clonegenerator.AccumulatorType;
import com.milaboratory.mitcr.clonegenerator.BasicCloneGenerator;
import com.milaboratory.mitcr.clonegenerator.CloneGenerator;
import com.milaboratory.mitcr.clusterization.penalty.OneMismatchPenaltyCalculator;
import com.milaboratory.mitcr.qualitystrategy.DummyQualityInterpretationStrategy;
import com.milaboratory.mitcr.vdjmapping.AbstractMapperTest;
import com.milaboratory.util.CompressionType;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.milaboratory.mitcr.MiTCRTestUtils.getFlexCDR3ExtractionParameters;
import static org.junit.Assert.assertEquals;

public class PenaltyBasedFastClusterizerTest extends AbstractMapperTest {
    @Test
    public void firstTest() throws InterruptedException, URISyntaxException, IOException {
        File sample = new File(ClassLoader.getSystemResource("cdr3_sample.fastq.gz").toURI());
        SSequencingDataReader reader = new SFastqReader(sample, QualityFormat.Phred33, CompressionType.GZIP);

        CDR3ExtractorFromSRead extractor = new CDR3ExtractorFromSRead(Species.HomoSapiens, Gene.TRB,
                getFlexCDR3ExtractionParameters(), library,
                new DummyQualityInterpretationStrategy());
        //new IlluminaQualityInterpretationStrategy((byte) 25));

        OutputPort<CDR3ExtractionResult<SSequencingRead>> results = CUtils.wrap(reader, extractor);

        CDR3ExtractionResult<SSequencingRead> result;

        CloneGenerator generator = new BasicCloneGenerator(AccumulatorType.getFactory(AccumulatorType.MaxStrict),
                .7f, false);

        int count = 0;
        while ((result = results.take()) != null) {
            if (result.getCDR3() == null)
                continue;
            count++;

            generator.put(result);
        }

        generator.put(null);

        CloneSet cloneSet = generator.getCloneSet();

        TestListener listener = new TestListener();
        CloneSetClustered cloneSetClustered = (new PenaltyBasedFastClusterizer(1.0f,
                OneMismatchPenaltyCalculator.INSTANCE, listener))
                .cluster(cloneSet);

        int sequencesClusterized = 0;
        for (CloneCluster cluster : cloneSetClustered.getClones())
            if (!cluster.getChildClones().isEmpty())
                for (Clone leaf : cluster.getChildClones()) {
                    assertEquals(1, SequencesUtils.mismatchCount(cluster.getCDR3().getSequence(), leaf.getCDR3().getSequence()));
                    sequencesClusterized += leaf.getCount();
                }

        assertEquals(194, count);

        assertEquals(sequencesClusterized, listener.sequencesClusterized.get());
        assertEquals(128, cloneSetClustered.getClones().size());
        assertEquals(128, listener.clusters.get());

        double sum = 0.0;
        for (Clone c : cloneSetClustered.getClones()) {
            count -= c.getCount();
            sum += c.getPart();
        }

        //Some reads are not mapped
        assertEquals(0, count);

        assertEquals(sum, 1.0, 1E-10);
    }

    private static final class TestListener implements ClusterizationListener {
        public final AtomicInteger clusters = new AtomicInteger(),
                sequencesClusterized = new AtomicInteger();

        @Override
        public void clusterCenterCreated(Clone center) {
            clusters.getAndIncrement();
        }

        @Override
        public void pairClusterized(Clone center, Clone leaf, List<Clone> otherLeafs) {
            sequencesClusterized.getAndIncrement();
        }

        @Override
        public void clusterBroken(Clone center, List<Clone> leafs) {
            clusters.decrementAndGet();
            sequencesClusterized.addAndGet(-leafs.size());
        }
    }
}
