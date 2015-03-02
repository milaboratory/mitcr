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

import cc.redberry.pipe.InputPort;
import cc.redberry.pipe.OutputPort;
import cc.redberry.pipe.ThreadSafe;
import cc.redberry.pipe.blocks.Buffer;
import cc.redberry.pipe.util.FlatteningOutputPort;
import com.milaboratory.core.clone.CloneSet;
import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResultUtils;
import com.milaboratory.mitcr.qualitystrategy.*;
import com.milaboratory.mitcr.util.evolver.Reactor;
import com.milaboratory.mitcr.vdjmapping.ntree.NTreeSlider;
import com.milaboratory.mitcr.vdjmapping.ntree.NucleotideInfo;
import com.milaboratory.mitcr.vdjmapping.ntree.NucleotideInfoProvider;
import com.milaboratory.util.LongArrayList;
import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.random.Well44497a;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A parallel clone generation with low-quality read (has low-quality points in CDR3 region) assigned back to assembled
 * CDR3s thus increasing their count. No mismatches are allowed in good quality regions and a fixed number of mismatches
 * is allowed in bad-quality regions
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class LQMappingCloneGenerator extends BasicCloneGenerator implements ThreadSafe {
    //TODO optimization:
    /*
        Optimization hints:
          - guide mapping thread. Send some information about newly
            created clones (eg. (1) length, (2) some kind of patterns ...)
          - mapping triggering strategy
     */

    /**
     * Where to store sequences before secondary mapping
     */
    private static boolean useOnHddAccumulator = true;
    private final ResultsAccumulator notMappedPool = useOnHddAccumulator ?
            new OnHDDResultAccumulator() :
            new InMemoryResultAccumulator();
    private final InputPort<OutputPort<CDR3ExtractionResult>> toSecondaryMapping;

    /**
     * Entrance to the mapping infrastructure
     */
    private final InputPort<CDR3ExtractionResult> toMapping;

    //Secondary mapping trigger fields
    static final int DEFAULT_RE_MAPPING_PERIOD = 100000;
    //private int initialReMappingPeriod = DEFAULT_RE_MAPPING_PERIOD;
    private float reMappingFactor = 0.8f;
    private final AtomicInteger reMappingCounter = new AtomicInteger(DEFAULT_RE_MAPPING_PERIOD);

    /**
     * Winner clone selection strategy
     */
    //TODO false is not supported
    private final boolean proportionalMapping;

    /**
     * How to interpret the quality
     */
    private final QualityInterpretationStrategy strategy;

    /**
     * For fast access
     */
    private final GoodBadNucleotideSequenceProvider<NucleotideSQPair> goodBadNucleotideSequenceProvider;

    /**
     * Thread controlling objects. Only one used: or Thread, or Future.
     */
    private final List<Thread> threads;
    private final List<Future<?>> futures;
    private final ExecutorService executorService;

    //TODO sense of this parameter should be tested!!!
    /**
     * Maximal allowed mismatches in bad points
     */
    private final int maxBadSlides;
    private final AtomicBoolean finished = new AtomicBoolean(false);

    /**
     * Creates a parallel clone generation with low-quality read assignment. Note that no mismatches are allowed in good
     * quality nucleotides of CDR3 and up to maxMismatches are allowed in bad quality nucleotides
     *
     * @param barcodeAggregationFactor a factor telling which alleles to include when building CDR3 (from 0 to 1). If
     *                                 equals to 0, only the most frequent allele will be included, if equals to 1 all
     *                                 alleles aligned at least once are included. For values between 0 and 1 it is
     *                                 interpreted as (1 - (allele alignment frequency) / (max allele alignment
     *                                 frequency)) threshold.
     * @param saveBackwardLinks        tells the generator to save backward links to reads used for assembly of every
     *                                 CDR3
     * @param strategy                 a strategy to interpret read quality
     * @param maxMismatches            maximum number of allowed mismatches in bad quality regions
     * @param proportionalMapping      determines LQ mapping target selection strategy (false - map to the biggest
     *                                 possible clone, true - choose target with weight equal to clone sequence count)
     */
    public LQMappingCloneGenerator(AccumulatorCloneFactory cloneFactory, float barcodeAggregationFactor,
                                   boolean saveBackwardLinks, QualityInterpretationStrategy strategy,
                                   int maxMismatches, boolean proportionalMapping) {
        this(cloneFactory, barcodeAggregationFactor, saveBackwardLinks, strategy, maxMismatches, proportionalMapping, null, null);
    }

    /**
     * Creates a parallel clone generation with low-quality read assignment. Note that no mismatches are allowed in good
     * quality nucleotides of CDR3 and up to maxMismatches are allowed in bad quality nucleotides
     *
     * @param barcodeAggregationFactor a factor telling which alleles to include when building CDR3 (from 0 to 1). If
     *                                 equals to 0, only the most frequent allele will be included, if equals to 1 all
     *                                 alleles aligned at least once are included. For values between 0 and 1 it is
     *                                 interpreted as (1 - (allele alignment frequency) / (max allele alignment
     *                                 frequency)) threshold.
     * @param saveBackwardLinks        tells the generator to save backward links to reads used for assembly of every
     *                                 CDR3
     * @param strategy                 a strategy to interpret read quality
     * @param maxMismatches            maximum number of allowed mismatches in bad quality regions
     * @param proportionalMapping      determines LQ mapping target selection strategy (false - map to the biggest
     *                                 possible clone, true - choose target with weight equal to clone sequence count)
     * @param executorService          an executor service to create inner thread
     */
    public LQMappingCloneGenerator(AccumulatorCloneFactory cloneFactory, float barcodeAggregationFactor, boolean saveBackwardLinks,
                                   QualityInterpretationStrategy strategy, int maxMismatches,
                                   boolean proportionalMapping, ExecutorService executorService) {
        this(cloneFactory, barcodeAggregationFactor, saveBackwardLinks, strategy, maxMismatches, proportionalMapping, executorService, null);
    }

    /**
     * Creates a parallel clone generation with low-quality read assignment. Note that no mismatches are allowed in good
     * quality nucleotides of CDR3 and up to maxMismatches are allowed in bad quality nucleotides
     *
     * @param barcodeAggregationFactor a factor telling which alleles to include when building CDR3 (from 0 to 1). If
     *                                 equals to 0, only the most frequent allele will be included, if equals to 1 all
     *                                 alleles aligned at least once are included. For values between 0 and 1 it is
     *                                 interpreted as (1 - (allele alignment frequency) / (max allele alignment
     *                                 frequency)) threshold.
     * @param saveBackwardLinks        tells the generator to save backward links to reads used for assembly of every
     *                                 CDR3
     * @param strategy                 a strategy to interpret read quality
     * @param maxMismatches            maximum number of allowed mismatches in bad quality regions
     * @param proportionalMapping      determines LQ mapping target selection strategy (false - map to the biggest
     *                                 possible clone, true - choose target with weight equal to clone sequence count)
     * @param listener                 clone generator event listener
     */
    public LQMappingCloneGenerator(AccumulatorCloneFactory cloneFactory, float barcodeAggregationFactor, boolean saveBackwardLinks,
                                   QualityInterpretationStrategy strategy, int maxMismatches,
                                   boolean proportionalMapping, CloneGeneratorListener listener) {
        this(cloneFactory, barcodeAggregationFactor, saveBackwardLinks, strategy, maxMismatches, proportionalMapping, null, listener);
    }


    /**
     * Creates a parallel clone generation with low-quality read assignment. Note that no mismatches are allowed in good
     * quality nucleotides of CDR3 and up to maxMismatches are allowed in bad quality nucleotides
     *
     * @param barcodeAggregationFactor a factor telling which alleles to include when building CDR3 (from 0 to 1). If
     *                                 equals to 0, only the most frequent allele will be included, if equals to 1 all
     *                                 alleles aligned at least once are included. For values between 0 and 1 it is
     *                                 interpreted as (1 - (allele alignment frequency) / (max allele alignment
     *                                 frequency)) threshold.
     * @param saveBackwardLinks        tells the generator to save backward links to reads used for assembly of every
     *                                 CDR3
     * @param strategy                 a strategy to interpret read quality
     * @param maxMismatches            maximum number of allowed mismatches in bad quality regions
     * @param proportionalMapping      determines LQ mapping target selection strategy (false - map to the biggest
     *                                 possible clone, true - choose target with weight equal to clone sequence count)
     * @param executorService          an executor service to create inner thread
     * @param listener                 clone generator event listener
     */
    public LQMappingCloneGenerator(AccumulatorCloneFactory cloneFactory, float barcodeAggregationFactor, boolean saveBackwardLinks, QualityInterpretationStrategy strategy, int maxMismatches,
                                   boolean proportionalMapping, ExecutorService executorService, CloneGeneratorListener listener) {
        super(cloneFactory, barcodeAggregationFactor, saveBackwardLinks, listener);
        if (strategy instanceof DummyQualityInterpretationStrategy)
            throw new IllegalArgumentException("Using of DummyQualityInterpretationStrategy in LQMappingCloneGenerator is senseless. " +
                    "Use BasicCloneGenerator or use IlluminaQualityInterpretationStrategy as quality interpretation strategy instead.");

        this.maxBadSlides = maxMismatches;
        this.strategy = strategy;
        this.goodBadNucleotideSequenceProvider = this.strategy.getProviderForNucleotideSQPair();
        this.proportionalMapping = proportionalMapping;

        //Initializing thread infrastructure
        this.executorService = executorService;
        if (executorService == null) {
            this.threads = new ArrayList<>();
            this.futures = null;
        } else {
            this.futures = new ArrayList<>();
            this.threads = null;
        }

        //  ====   First mapping   ====

        //Creating buffer for results awaiting mapping
        final Buffer<CDR3ExtractionResult> toMapping = new Buffer<>();

        //Creating entrance point to mapping infrastructure
        this.toMapping = toMapping.createInputPort();

        //Starting mapping threads
        createNewThread(createMapper(toMapping));

        //  ====   Secondary mapping   ====

        //Creating buffer for chunks of results awaiting secondary mapping
        final Buffer<OutputPort<CDR3ExtractionResult>> toSecondaryMapping = new Buffer<>();

        //Create an entrance
        this.toSecondaryMapping = toSecondaryMapping.createInputPort();

        createNewThread(createMapper(new FlatteningOutputPort<CDR3ExtractionResult>(toSecondaryMapping)));
    }

    //Utility methods

    private void createNewThread(Runnable runnable) {
        if (executorService == null) {
            Thread t = new Thread(runnable);
            t.start();
            threads.add(t);
        } else
            futures.add(executorService.submit(runnable));
    }

    private void joinThread(int id) throws InterruptedException {
        try {
            if (futures == null)
                threads.get(id).join();
            else
                futures.get(id).get();
        } catch (ExecutionException ee) {
            throw new RuntimeException(ee);
        }
    }

    private Runnable createMapper(OutputPort<CDR3ExtractionResult> results) {
        if (proportionalMapping)
            return new MappingProcessProportionalWinnerSelection(results);
        else
            throw new RuntimeException("Not supported!");
    }

    /**
     * Used only in unit tests.
     */
    //TODO add correct tests
    void setReMappingCounter(int reMappingPeriod) {
        this.reMappingCounter.set(reMappingPeriod);
    }

    @Override
    public void put(CDR3ExtractionResult cdr3ExtractionResult) {
        try {

            if (cdr3ExtractionResult == null) {
                if (finished.compareAndSet(false, true)) {
                    //Stop initial mapping process
                    toMapping.put(null);

                    //Wait for initial mapper to stop
                    joinThread(0);

                    //Start final mapping
                    toSecondaryMapping.put(notMappedPool.getBack());

                    //Stopping secondary mapper
                    toSecondaryMapping.put(null);

                    //Closing super
                    super.put(null);
                }
                return;
            }

            if (cdr3ExtractionResult.getCDR3() == null) {
                //For statistics aggregation
                super.put(cdr3ExtractionResult);

                return;
            }

            //Wrapping sequence to G/B nucleotide sequence
            GoodBadNucleotideSequence sequence = goodBadNucleotideSequenceProvider.process(cdr3ExtractionResult.getCDR3());

            //Looking for bad points
            if (GBNSUtils.hasBadNucleotides(sequence)) {
                //The CDR3 is bad

                //Put this sequence to the mapping queue
                toMapping.put(cdr3ExtractionResult);

            } else {
                //To the good
                boolean isNewClone = super.putResult(cdr3ExtractionResult);

                //Good sequence was assigned to some clone or new clone was created
                if (isNewClone && reMappingCounter.decrementAndGet() == 0) {
                    //Extracting results from accumulator and pass them to the secondary mapper
                    toSecondaryMapping.put(notMappedPool.getBack());

                    //Resetting counter
                    final int newPeriod = (int) (clonesCount.get() * reMappingFactor);
                    reMappingCounter.addAndGet(newPeriod);
                }
            }
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        }
    }

    @Override
    public CloneSet getCloneSet() {
        try {
            //Wait for secondary mapper to finish its job
            joinThread(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return super.getCloneSet();
    }

    private NucleotideInfoProvider createInfoProvider(CDR3ExtractionResult result) {
        return new GBNSBothSidesNucleotideInfoProvider(goodBadNucleotideSequenceProvider.process(result.getCDR3()));
    }

    /**
     * {@code true} tells all newly created {@link LQMappingCloneGenerator} to store all unmapped sequences in the
     * temporary file, {@code false} tells to use only RAM.
     *
     * <p>Default value is {@code true}.</p>
     *
     * <p>This option is useful to decrease needed amount of memory wen analysing big data sets with very diverse clonal
     * content.</p>
     *
     * @param useOnHddAccumulator determines weather "on HDD" or "in memory" storage will be used to store unmapped
     *                            sequences
     */
    public static void setUseOnHddAccumulator(boolean useOnHddAccumulator) {
        LQMappingCloneGenerator.useOnHddAccumulator = useOnHddAccumulator;
    }

    private final class MappingProcessProportionalWinnerSelection implements Runnable {
        final OutputPort<CDR3ExtractionResult> resultsToMap;

        private MappingProcessProportionalWinnerSelection(OutputPort<CDR3ExtractionResult> resultsToMap) {
            this.resultsToMap = resultsToMap;
        }

        @Override
        public void run() {
            try {
                //Reactor
                final Reactor<NTreeSlider<Node>, NucleotideInfo> reactor = new Reactor(strategy.getGenerator());

                //Pool for target clones
                final List<AccumulatorClone> targets = new ArrayList<>();
                final LongArrayList targetCounts = new LongArrayList();
                //Total target seq. count
                long sum = 0, count;

                int minMismatches, mismatches;
                AccumulatorClone winnerClone, clone;
                CDR3ExtractionResult result;

                final RandomData random = new RandomDataImpl(new Well44497a());

                while ((result = resultsToMap.take()) != null) {
                    //Reset holder for mapping targets
                    targets.clear();
                    targetCounts.clear();
                    //Sum to be used in random selection routine
                    sum = 0;

                    //Try to find exact match
                    Node node = root;
                    final NucleotideSequence cdr3Sequence = result.getCDR3().getSequence();
                    final int size = cdr3Sequence.size();
                    for (int i = 1; i <= size; ++i)
                        if ((node = node.next[cdr3Sequence.codeAt(((i & 1) == 1) ? i >> 1 : size - (i >> 1))]) == null)
                            break;
                    if (node != null && (winnerClone = node.clone) != null) {
                        if (listener != null)
                            listener.assignedToClone(winnerClone, result, false);
                        winnerClone.include(result, false);
                        continue;
                    }

                    //Perform tree traversal
                    final List<NTreeSlider<Node>> sliders = reactor.toLastCondition(new NTreeSlider<>(root),
                            createInfoProvider(result));

                    //Calculating minimal mismatch count
                    minMismatches = maxBadSlides;
                    for (NTreeSlider<Node> slider : sliders)
                        if (slider.node.clone != null &&
                                (mismatches = slider.badSlides) < minMismatches)
                            minMismatches = mismatches;

                    //Creating pool of target clones
                    for (NTreeSlider<Node> slider : sliders)
                        if ((clone = slider.node.clone) != null &&
                                slider.badSlides <= minMismatches) {
                            targets.add(clone);
                            sum += count = (clone.getCount() - clone.additionalCount);
                            targetCounts.add(count);
                        }

                    if (sum == 0 || targets.isEmpty()) {
                        //Mapping is not found

                        if (!finished.get())
                            //To the secondary mapping
                            notMappedPool.put(CDR3ExtractionResultUtils.makeSerializable(result));
                        else if (listener != null)
                            listener.cdr3Dropped(result);

                        continue;
                    }

                    // < --- Mapping found

                    //Selecting random winner
                    sum = (sum == 1 ? 0 : random.nextLong(0, sum - 1));
                    winnerClone = null;
                    for (int i = targetCounts.size() - 1; i >= 0; --i)
                        if ((sum -= targetCounts.get(i)) < 0) {
                            winnerClone = targets.get(i);
                            break;
                        }
                    assert winnerClone != null;

                    if (listener != null)
                        listener.assignedToClone(winnerClone, result, minMismatches > 0);

                    //Passing extraction result to the winner clone
                    winnerClone.include(result, minMismatches > 0);
                }
            } catch (InterruptedException ie) {
            }
        }
    }

    //private final class MappingProcessWinnerIsMax implements Runnable {
    //    @Override
    //    public void run() {
    //        try {
    //
    //            //Reactor
    //            final Reactor<NTreeSlider<Node>, NucleotideInfo> reactor = new Reactor(strategy.getGenerator());
    //            AccumulatorClone winnerClone, clone;
    //            ResultWrapper resultWrapper;
    //
    //            do {
    //                mappingSemaphore.acquireUninterruptibly();
    //
    //                //Used only by one thread...
    //                resultWrapper = mappingQueue.takeFirst();
    //
    //                List<NTreeSlider<Node>> sliders = reactor.toLastCondition(new NTreeSlider<>(root), resultWrapper.createInfoProvider());
    //
    //                //Holder for the winner clone
    //                winnerClone = null;
    //
    //                boolean mismatches = false;
    //                for (NTreeSlider<Node> slider : sliders)
    //                    if ((clone = slider.node.clone) != null && slider.badSlides <= maxBadSlides)
    //                        if (winnerClone == null || winnerClone.getCount() < clone.getCount()) {
    //                            mismatches = (slider.badSlides != 0);
    //                            winnerClone = clone;
    //                        }
    //
    //                if (winnerClone != null) //Mapping found
    //                    //Passing extraction result to clone
    //                    winnerClone.include(resultWrapper.result, mismatches);
    //                else if (!finished.get()) {
    //                    //Mapping is not found,
    //                    //so return this clone to queue
    //                    mappingQueue.put(resultWrapper);
    //                    //Incrementing corresponding counter
    //                    awaitingRemapping.incrementAndGet();
    //                }
    //            } while (!mappingQueue.isEmpty() || !finished.get());
    //
    //        } catch (InterruptedException ie) {
    //        }
    //    }
    //}
}
