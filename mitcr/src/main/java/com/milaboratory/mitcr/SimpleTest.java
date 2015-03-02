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
package com.milaboratory.mitcr;

import com.milaboratory.core.clone.Clone;
import com.milaboratory.core.clone.CloneSetClustered;
import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.Species;
import com.milaboratory.core.sequence.quality.QualityFormat;
import com.milaboratory.core.sequencing.io.fastq.SFastqReader;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractorParameters;
import com.milaboratory.mitcr.cdrextraction.Strand;
import com.milaboratory.mitcr.clonegenerator.LQMappingCloneGeneratorParameters;
import com.milaboratory.mitcr.clonegenerator.SequencingReadLink;
import com.milaboratory.mitcr.clsexport.ClsExporter;
import com.milaboratory.mitcr.clusterization.CloneClusterizationType;
import com.milaboratory.mitcr.pipeline.FullPipeline;
import com.milaboratory.mitcr.pipeline.Parameters;
import com.milaboratory.mitcr.qualitystrategy.IlluminaQualityInterpretationStrategy;
import com.milaboratory.mitcr.vdjmapping.*;
import com.milaboratory.util.CompressionType;
import com.milaboratory.util.ProgressReporter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicLongArray;

public class SimpleTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        final String input = args[0];
        CompressionType compressionType = CompressionType.None;
        if (input.endsWith(".gz"))
            compressionType = CompressionType.GZIP;

        SFastqReader reads = new SFastqReader(input,
                args.length > 1 ?
                        QualityFormat.Phred33 : QualityFormat.Phred64, compressionType);

        new Thread(new ProgressReporter("Processing sequences: ", reads)).start();

        Parameters parameters = new Parameters(Gene.TRB, Species.HomoSapiens,
                new CDR3ExtractorParameters(new VJSegmentMapperParameters(AlignmentDirection.Both, -4, 1, 12, 3),
                new VJSegmentMapperParameters(AlignmentDirection.InsideCDR3, -3, 2, 7, -2),
                //new VJSegmentMapperParameters(-1, 4, 12, -2, AlignmentDirection.TrivialAlignment),
                new DSegmentMapperParameters(6), Strand.Both, true));
        parameters.setQualityInterpretationStrategy(new IlluminaQualityInterpretationStrategy((byte) 30));
        parameters.setCloneGeneratorParameters(new LQMappingCloneGeneratorParameters(3));
        parameters.setClusterizationType(CloneClusterizationType.OneMismatch, .2f);

        FullPipeline pipeline = new FullPipeline(reads, parameters, true);


        //Merger<SSequencingRead> readsB = new Merger<>();
        //readsB.merge(new CountLimitingOutputPort<>(reads, 100000L));
        //readsB.merge(reads);
        //readsB.start();

        /*CountingOutputPort<SSequencingRead> countingPort;

        final QualityInterpretationStrategy qStrategy = new IlluminaQualityInterpretationStrategy((byte) 20);

        AnalysisListenerImpl listener = new AnalysisListenerImpl(new Collector(), new Collector());

        //Default parameters
        CDR3ExtractorParameters parameters = new CDR3ExtractorParameters(new VJSegmentMapperParameters(-4, 1, 12, 3, AlignmentDirection.Both),
                new VJSegmentMapperParameters(-3, 2, 7, -2, AlignmentDirection.InsideCDR3),
                //new VJSegmentMapperParameters(-1, 4, 12, -2, AlignmentDirection.TrivialAlignment),
                new DSegmentMapperParameters(6), Strand.Both, true);

        SegmentLibrary library = DefaultSegmentLibrary.load();

        CDR3ExtractorFactoryFromSRead extractorFactory = new CDR3ExtractorFactoryFromSRead(Species.HomoSapiens, Gene.TRB, parameters, library,
                qStrategy, listener);

        OutputPort<CDR3ExtractionResult<SSequencingRead>> results = new ParallelProcessor<>(countingPort = new CountingOutputPort<SSequencingRead>(readsB), extractorFactory, Runtime.getRuntime().availableProcessors());

        CloneGenerator generator = new LQMappingCloneGenerator(.15f, true, qStrategy, 3);

        CUtils.drain(results, generator);

        readsB.join();
        ((ParallelProcessor) results).join();

        CloneClusterizer clusterizer = new PenaltyBasedFastClusterizer(.1f, CloneClusterizationType.OneMismatch);

        System.out.println("Clusterization.");
        long start = System.currentTimeMillis();*/

        pipeline.run();

        CloneSetClustered clusteredClones = pipeline.getResult();

        //System.out.println("Done in " + (System.currentTimeMillis() - start) + "ms.");

        long total = pipeline.getTotal();

        //CloneSetClustered cloneSet = clusteredClones;

        ClsExporter.export(pipeline, "Test analysis", "TestDS", "tst.cls");

        try (PrintWriter pw = new PrintWriter("out.txt")) {
            long processed = 0, added = 0, s = 0;

            for (Clone clone : clusteredClones.getClones()) {
                pw.println(clone.getCDR3AA() + "\t" + clone.getCount());
                processed += clone.getCount();
                s += clone.getCount();
                for (SequencingReadLink links : clone.getBackwardLinks()) {
                    s--;
                    if (links.isAdditional())
                        added++;
                }
            }

            /*System.out.println("V");
            ((Collector) listener.getVListener()).sout();
            System.out.println("J");
            ((Collector) listener.getJListener()).sout();

            System.out.println(total);*/
            System.out.println(processed);
            System.out.println(added);
            System.out.println(s);
        }
    }

    public static class Collector implements VJMapperListener {
        private final AtomicLongArray scores = new AtomicLongArray(200);

        @Override
        public void mappingFound(VJSegmentMappingResult result, Object source) {
            int score = (int) result.getScore();
            if (score < 0 || score >= 200)
                return;
            scores.getAndIncrement(score);
        }

        @Override
        public void mappingDropped(VJSegmentMappingResult result, Object source) {
            int score = (int) result.getScore();
            if (score < 0 || score >= 200)
                return;
            scores.getAndIncrement(score);
        }

        public void sout() {
            long sum = 0;
            for (int i = 0; i < scores.length(); ++i) {
                sum += scores.get(i);
                if (scores.get(i) > 0)
                    System.out.println(i + ": " + scores.get(i));
            }
            System.out.println("Sum: " + sum);
        }

        @Override
        public void noMapping(Object source) {
        }
    }
}
