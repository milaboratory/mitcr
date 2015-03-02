package com.milaboratory.mitcr.statistics;

import com.milaboratory.core.clone.Clone;
import com.milaboratory.core.clone.CloneSet;
import com.milaboratory.core.clone.CloneSetClustered;
import com.milaboratory.core.segment.SegmentLibrary;
import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequencing.read.SSequencingRead;
import com.milaboratory.mitcr.pipeline.AnalysisListener;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractorListener;
import com.milaboratory.mitcr.clonegenerator.CloneGeneratorListener;
import com.milaboratory.mitcr.clusterization.ClusterizationListener;
import com.milaboratory.mitcr.pipeline.Parameters;
import com.milaboratory.mitcr.vdjmapping.VJMapperListener;
import com.milaboratory.mitcr.vdjmapping.VJSegmentMappingResult;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class AnalysisStatisticsAggregator implements AnalysisListener {
    private long extractionStartTimestamp, extractionEndTimestamp;
    private int threads;
    private final AtomicLong analysedBases = new AtomicLong();
    private final VJMapperListenerImpl vListener = new VJMapperListenerImpl(),
            jListener = new VJMapperListenerImpl();
    private final CDR3ExtractorListenerImpl cdr3ExtractorListener = new CDR3ExtractorListenerImpl();
    private final CloneGeneratorListenerImpl cloneGeneratorListener = new CloneGeneratorListenerImpl();
    private final ClusterizationListenerImpl clusterizationListener = new ClusterizationListenerImpl();

    @Override
    public VJMapperListener getVListener() {
        return vListener;
    }

    @Override
    public VJMapperListener getJListener() {
        return jListener;
    }

    @Override
    public CDR3ExtractorListener getCDR3ExtractorListener() {
        return cdr3ExtractorListener;
    }

    @Override
    public CloneGeneratorListener getCloneGeneratorListener() {
        return cloneGeneratorListener;
    }

    @Override
    public ClusterizationListener getClusterizationListener() {
        return clusterizationListener;
    }

    @Override
    public void beforeClusterization(CloneSet cloneSet) {
    }

    @Override
    public void afterClusterization(CloneSetClustered clusterizedCloneSet) {
    }

    public long getTotalReadsProcessed() {
        return cdr3ExtractorListener.notExtracted.get() + cdr3ExtractorListener.extracted.get();
    }

    public long getTotalBasesProcessed() {
        return analysedBases.get();
    }

    public double getReadsPerSecond() {
        return 1.0E9 * getTotalReadsProcessed() / (extractionEndTimestamp - extractionStartTimestamp);
    }

    public double getReadsPerSecondPerThread() {
        return getReadsPerSecond() / threads;
    }

    public double getBasesPerSecond() {
        return 1.0E9 * getTotalBasesProcessed() / (extractionEndTimestamp - extractionStartTimestamp);
    }

    public double getBasesPerSecondPerThread() {
        return getBasesPerSecond() / threads;
    }


    public long getVMappingsFound() {
        return vListener.found.get();
    }

    public long getVMappingsNotFound() {
        return vListener.notFound.get();
    }

    public long getVMappingsDropped() {
        return vListener.dropped.get();
    }

    public long getJMappingsFound() {
        return jListener.found.get();
    }

    public long getJMappingsNotFound() {
        return jListener.notFound.get();
    }

    public long getJMappingsDropped() {
        return jListener.dropped.get();
    }

    public long getCDR3Extracted() {
        return cdr3ExtractorListener.extracted.get();
    }

    public long getCDR3NotExtracted() {
        return cdr3ExtractorListener.notExtracted.get();
    }

    public long getClonesCreated() {
        return cloneGeneratorListener.createdClones.get();
    }

    public long getGoodCDR3Assigned() {
        return cloneGeneratorListener.goodReadsAssigned.get();
    }

    public long getBadCDR3Assigned() {
        return cloneGeneratorListener.badReadsAssigned.get();
    }

    public long getBadCDR3Dropped() {
        return cloneGeneratorListener.readsDropped.get();
    }

    public long getClustersCreated() {
        return clusterizationListener.createdClusters.get();
    }

    public long getClonesClusterized() {
        return clusterizationListener.clusterizedClones.get();
    }

    public long getReadsClusterized() {
        return clusterizationListener.clusterizedReads.get();
    }

    public long getClustersBroken() {
        return clusterizationListener.brokenClusters.get();
    }

    public long getClonesDeClusterized() {
        return clusterizationListener.deClusterizedClones.get();
    }

    public long getReadsDeClusterized() {
        return clusterizationListener.deClusterizedReads.get();
    }

    public double getReClusterizationRatio() {
        return 1.0 * clusterizationListener.brokenClusters.get() /
                (clusterizationListener.createdClusters.get() - clusterizationListener.brokenClusters.get());
    }

    public double getReClusterizationRatioClones() {
        return 1.0 * clusterizationListener.deClusterizedClones.get() /
                (clusterizationListener.clusterizedClones.get() - clusterizationListener.deClusterizedClones.get());
    }

    public double getReClusterizationRatioReads() {
        return 1.0 * clusterizationListener.deClusterizedReads.get() /
                (clusterizationListener.clusterizedReads.get() - clusterizationListener.deClusterizedReads.get());
    }

    @Override
    public void analysisStarted(Parameters parameters, SegmentLibrary library, int threads) {
        this.threads = threads;
        extractionStartTimestamp = System.nanoTime();
    }

    @Override
    public void cdr3ExtractionFinished() {
        extractionEndTimestamp = System.nanoTime();
    }

    private final class VJMapperListenerImpl implements VJMapperListener {
        final AtomicLong found = new AtomicLong(),
                dropped = new AtomicLong(),
                notFound = new AtomicLong();

        @Override
        public void mappingFound(VJSegmentMappingResult result, Object source) {
            found.incrementAndGet();
        }

        @Override
        public void mappingDropped(VJSegmentMappingResult result, Object source) {
            dropped.incrementAndGet();
        }

        @Override
        public void noMapping(Object source) {
            notFound.incrementAndGet();
        }
    }

    private final class CDR3ExtractorListenerImpl implements CDR3ExtractorListener {
        final AtomicLong extracted = new AtomicLong(),
                notExtracted = new AtomicLong();

        @Override
        public void cdr3Extracted(CDR3ExtractionResult result, Object input) {
            extracted.incrementAndGet();
            if (input instanceof NucleotideSQPair)
                analysedBases.addAndGet(((NucleotideSQPair) input).size());
            else if (input instanceof SSequencingRead)
                analysedBases.addAndGet(((SSequencingRead) input).getData().size());
        }

        @Override
        public void cdr3NotExtracted(CDR3ExtractionResult result, Object input) {
            notExtracted.incrementAndGet();
            if (input instanceof NucleotideSQPair)
                analysedBases.addAndGet(((NucleotideSQPair) input).size());
            else if (input instanceof SSequencingRead)
                analysedBases.addAndGet(((SSequencingRead) input).getData().size());
        }
    }

    private final class CloneGeneratorListenerImpl implements CloneGeneratorListener {
        final AtomicLong createdClones = new AtomicLong(),
                goodReadsAssigned = new AtomicLong(),
                badReadsAssigned = new AtomicLong(),
                readsDropped = new AtomicLong();

        @Override
        public void newCloneCreated(Clone clone, CDR3ExtractionResult cdr3ExtractionResult) {
            createdClones.incrementAndGet();
        }

        @Override
        public void assignedToClone(Clone clone, CDR3ExtractionResult cdr3ExtractionResult, boolean isAdditional) {
            if (isAdditional)
                badReadsAssigned.incrementAndGet();
            else
                goodReadsAssigned.incrementAndGet();
        }

        @Override
        public void cdr3Dropped(CDR3ExtractionResult cdr3ExtractionResult) {
            readsDropped.incrementAndGet();
        }
    }

    private final class ClusterizationListenerImpl implements ClusterizationListener {
        final AtomicLong createdClusters = new AtomicLong(), clusterizedClones = new AtomicLong(),
                clusterizedReads = new AtomicLong(), brokenClusters = new AtomicLong(),
                deClusterizedClones = new AtomicLong(), deClusterizedReads = new AtomicLong();

        @Override
        public void clusterCenterCreated(Clone center) {
            createdClusters.incrementAndGet();
        }

        @Override
        public void pairClusterized(Clone center, Clone leaf, List<Clone> otherLeafs) {
            clusterizedClones.incrementAndGet();
            clusterizedReads.addAndGet(leaf.getCount());
        }

        @Override
        public void clusterBroken(Clone center, List<Clone> leafs) {
            brokenClusters.decrementAndGet();
            deClusterizedClones.addAndGet(-leafs.size());
            long sum = 0;
            for (Clone c : leafs)
                sum -= c.getCount();
            deClusterizedReads.addAndGet(sum);
        }
    }
}
