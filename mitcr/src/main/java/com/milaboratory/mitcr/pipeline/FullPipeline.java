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
package com.milaboratory.mitcr.pipeline;

import cc.redberry.pipe.CUtils;
import cc.redberry.pipe.OutputPort;
import cc.redberry.pipe.blocks.Merger;
import cc.redberry.pipe.blocks.ParallelProcessor;
import cc.redberry.pipe.util.CountingOutputPort;
import com.milaboratory.core.clone.CloneSet;
import com.milaboratory.core.clone.CloneSetClustered;
import com.milaboratory.core.segment.DefaultSegmentLibrary;
import com.milaboratory.core.segment.SegmentGroupType;
import com.milaboratory.core.segment.SegmentLibrary;
import com.milaboratory.core.sequencing.read.SSequencingRead;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractorFactoryFromSRead;
import com.milaboratory.mitcr.clonegenerator.CloneGenerator;
import com.milaboratory.mitcr.clonegenerator.CloneGeneratorFactory;
import com.milaboratory.mitcr.clusterization.CloneClusterizer;
import com.milaboratory.mitcr.clusterization.CloneClusterizerFactory;
import com.milaboratory.mitcr.statistics.CloneSetQualityControl;
import com.milaboratory.util.CanReportProgress;
import com.milaboratory.util.CanReportProgressAndStage;
import com.milaboratory.util.ProgressReporterFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class implementing basic miTCR analysis pipeline in a multi-threaded way
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class FullPipeline implements Runnable, CanReportProgressAndStage {
    private final Parameters parameters;
    private final SegmentLibrary library;
    private CloneSetQualityControl qc;
    private boolean saveBackwardLinks, inputBuffering = true;
    private AnalysisListener listener;
    private final OutputPort<SSequencingRead> input;
    private int threads = Runtime.getRuntime().availableProcessors();
    private long total;
    private CloneSetClustered result;
    private ExecutorService executorService;
    private boolean finished = false;
    private volatile CanReportProgress progressReporter;
    private volatile String stage = "Initialisation";

    /**
     * Creates a basic miTCR analysis pipeline
     *
     * @param input             input stream of reads
     * @param parameters        bulk parameters for pipeline
     * @param saveBackwardLinks save backward links from assembled clones to their parent reads
     * @param library           a custom receptor segment library
     */
    public FullPipeline(OutputPort<SSequencingRead> input, Parameters parameters, boolean saveBackwardLinks, SegmentLibrary library) {
        this.input = input;
        this.parameters = parameters;
        this.library = library;
        this.saveBackwardLinks = saveBackwardLinks;
    }

    /**
     * Creates a basic miTCR analysis pipeline,  uses default segment library
     *
     * @param input             input stream of reads
     * @param parameters        bulk parameters for pipeline
     * @param saveBackwardLinks save backward links from assembled clones to their parent reads
     */
    public FullPipeline(OutputPort<SSequencingRead> input, Parameters parameters, boolean saveBackwardLinks) {
        this(input, parameters, saveBackwardLinks, DefaultSegmentLibrary.load());
    }

    /**
     * Creates a basic miTCR analysis pipeline, uses default segment library and doesn't save backward links from
     * assembled clones to to reads
     *
     * @param input      input stream of reads
     * @param parameters bulk parameters for pipeline
     */
    public FullPipeline(OutputPort<SSequencingRead> input, Parameters parameters) {
        this(input, parameters, false);
    }

    @Override
    public void run() {
        //Copy executor service to local variable
        ExecutorService executorService = this.executorService;

        //User did not provide executorService so create simple newCachedThreadPool as a
        if (executorService == null)
            executorService = Executors.newCachedThreadPool();

        try {
            OutputPort<SSequencingRead> inputReads;

            if (inputBuffering) {
                //Buffering reads in separate thread
                final Merger<SSequencingRead> bufferedInput = new Merger<>();
                bufferedInput.merge(input, executorService);
                bufferedInput.start();
                inputReads = bufferedInput;
            } else
                inputReads = input;

            //To count input sequences
            final CountingOutputPort<SSequencingRead> countingInput = new CountingOutputPort<SSequencingRead>(inputReads);

            //Extraction of CDR3s
            final CDR3ExtractorFactoryFromSRead extractorFactory =
                    new CDR3ExtractorFactoryFromSRead(parameters.getSpecies(), parameters.getGene(),
                            parameters.getCDR3ExtractorParameters(), library,
                            parameters.getQualityInterpretationStrategy(), listener);

            //Setting up cdr3 extraction results port
            final OutputPort<CDR3ExtractionResult<SSequencingRead>> extractionResults =
                    new ParallelProcessor<>(countingInput, extractorFactory,
                            threads, executorService);

            //This event is mainly used for performance measurements
            if (listener != null)
                listener.analysisStarted(parameters, library, threads);

            //Instantiate clone generator
            final CloneGenerator generator = CloneGeneratorFactory.create(parameters.getCloneGeneratorParameters(),
                    parameters.getQualityInterpretationStrategy(), saveBackwardLinks, executorService,
                    listener == null ? null : listener.getCloneGeneratorListener());

            //Initialization of CloneGenerator
            generator.preInitialize(library.getGroup(parameters.getSpecies(), parameters.getGene(), SegmentGroupType.Variable),
                    library.getGroup(parameters.getSpecies(), parameters.getGene(), SegmentGroupType.Joining),
                    library.getGroup(parameters.getSpecies(), parameters.getGene(), SegmentGroupType.Diversity));

            //Setting progress reporter
            stage = "Individual sequence analysis & clone generation";

            progressReporter = ProgressReporterFactory.create(input);

            //Generate clones (in current thread)
            CUtils.drain(extractionResults, generator);

            //Just in case (exceptions will be thrown here if something goes wrong)
            //[architecture of redberry-pipe will be change to avoid such patterns]
            if (inputReads instanceof Merger)
                ((Merger) inputReads).join();
            ((ParallelProcessor) extractionResults).join();

            //This event is mainly used for performance measurements
            if (listener != null)
                listener.cdr3ExtractionFinished();

            stage = "Retrieving core clonotypes";
            progressReporter = null;

            //Getting calculated clone set
            final CloneSet cloneSet = generator.getCloneSet();

            //Firing before clusterization event
            if (listener != null)
                listener.beforeClusterization(cloneSet);

            //Setting progress reporter
            stage = "Clones clusterization";

            //Creating clone clusterizer
            final CloneClusterizer clusterizer =
                    CloneClusterizerFactory.create(parameters.getClusterizationType(),
                            parameters.getMaxClusterizationRatio(),
                            listener == null ? null : listener.getClusterizationListener());


            if (clusterizer instanceof CanReportProgress)
                progressReporter = (CanReportProgress) clusterizer;
            else
                progressReporter = null;

            //Clusterizing clones
            result = clusterizer.cluster(cloneSet);

            //Firing after clusterization event
            if (listener != null)
                listener.afterClusterization(result);

            if (result.getTotalCount() != cloneSet.getTotalCount())
                throw new RuntimeException("Clusterization assertion failed.");

            //Setting
            total = countingInput.getCount();

        } catch (InterruptedException ie) {
        } finally {
            executorService.shutdownNow();
            finished = true;
        }
    }

    @Override
    public String getStage() {
        return stage;
    }

    @Override
    public double getProgress() {
        final CanReportProgress pr = progressReporter;
        if (pr == null)
            return Double.NaN;
        return pr.getProgress();
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    /**
     * If {@code true} FullPipeline starts a thread for input data buffering while processing data
     *
     * <p>Default value is {@code true}.</p>
     *
     * @param inputBuffering
     */
    public void setInputBuffering(boolean inputBuffering) {
        this.inputBuffering = inputBuffering;
    }

    /**
     * Gets total number of sequences from which CDR3 was successfully extracted
     *
     * @return total number of sequences from which CDR3 was successfully extracted
     */
    public long getTotal() {
        return total;
    }

    /**
     * Gets the resulting set of clustered clones
     *
     * @return the resulting set of clustered clones
     */
    public CloneSetClustered getResult() {
        return result;
    }

    /**
     * Gets the segment library
     *
     * @return the segment library
     */
    public SegmentLibrary getLibrary() {
        return library;
    }

    /**
     * Sets the listener (for collection of statistics) TODO: not implemented
     *
     * @param listener the listener (for collection of statistics)
     */
    public void setAnalysisListener(AnalysisListener listener) {
        this.listener = listener;
    }

    /**
     * Set the executor service used to create threads for pipeline
     *
     * @param executorService executor service used to create threads for pipeline
     */
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * Get the number of threads used to run the pipeline
     *
     * @return number of threads used to run the pipeline
     */
    public int getThreads() {
        return threads;
    }

    /**
     * Set the number of threads used to run the pipeline
     *
     * @param threads the number of threads used to run the pipeline
     */
    public void setThreads(int threads) {
        this.threads = threads;
    }

    public CloneSetQualityControl getQC() {
        if (qc == null)
            qc = new CloneSetQualityControl(library, parameters.getSpecies(), parameters.getGene(), result, total);
        return qc;
    }
}