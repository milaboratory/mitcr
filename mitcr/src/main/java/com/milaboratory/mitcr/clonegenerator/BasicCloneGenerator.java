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

import cc.redberry.pipe.ThreadSafe;
import com.milaboratory.core.clone.CloneComparator;
import com.milaboratory.core.clone.CloneSet;
import com.milaboratory.core.clone.CloneSetImpl;
import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.SegmentGroupContainer;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult;
import com.milaboratory.mitcr.vdjmapping.ntree.NTreeNode;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * A basic clone generator that ignores read quality. No mismatches are allowed during mapping. This class is
 * tread-safe
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class BasicCloneGenerator implements CloneGenerator, ThreadSafe {
    //Tree structure needed for clone generator with low quality cdr3s mapping
    protected final Node root = new Node(null, (byte) -1);
    protected final AtomicInteger clonesCount = new AtomicInteger();
    private final AccumulatorCloneFactory cloneFactory;
    private final float barcodeAggregationFactor;
    private final boolean saveBackwardLinks;
    private final AtomicReference<Gene> gene = new AtomicReference<>();
    private AtomicBoolean initialized = new AtomicBoolean(false);
    protected final CloneGeneratorListener listener;
    //For clone set creation
    private final AtomicReferenceArray<SegmentGroupContainer> segmentContainers = new AtomicReferenceArray<SegmentGroupContainer>(3);

    /*
    * Creates a parallel basic clone generator that ignores read quality. No mismatches are allowed
    *
    * @param barcodeAggregationFactor a factor telling which alleles to include when building CDR3 (from 0 to 1). If
    *                                 equals to 0, only the most frequent allele will be included, if equals to 1 all
    *                                 alleles aligned at least once are included. For values between 0 and 1 it is
    *                                 interpreted as (1 - (allele alignment frequency) / (max allele alignment
    *                                 frequency)) threshold.
    * @param saveBackwardLinks        tells the generator to save backward links to reads used for assembly of every
    *                                 CDR3
    * @param listener                 provides event listener for this clone generator
    */
    public BasicCloneGenerator(AccumulatorCloneFactory cloneFactory, float barcodeAggregationFactor, boolean saveBackwardLinks) {
        this(cloneFactory, barcodeAggregationFactor, saveBackwardLinks, null);
    }

    /**
     * Creates a parallel basic clone generator that ignores read quality. No mismatches are allowed
     *
     * @param barcodeAggregationFactor a factor telling which alleles to include when building CDR3 (from 0 to 1). If
     *                                 equals to 0, only the most frequent allele will be included, if equals to 1 all
     *                                 alleles aligned at least once are included. For values between 0 and 1 it is
     *                                 interpreted as (1 - (allele alignment frequency) / (max allele alignment
     *                                 frequency)) threshold.
     * @param saveBackwardLinks        tells the generator to save backward links to reads used for assembly of every
     *                                 CDR3
     * @param listener                 provides event listener for this clone generator
     */
    public BasicCloneGenerator(AccumulatorCloneFactory cloneFactory, float barcodeAggregationFactor,
                               boolean saveBackwardLinks, CloneGeneratorListener listener) {
        this.cloneFactory = cloneFactory;
        this.barcodeAggregationFactor = barcodeAggregationFactor;
        this.saveBackwardLinks = saveBackwardLinks;
        this.listener = listener;
    }

    @Override
    public CloneSet getCloneSet() {
        for (AccumulatorClone clone : COLLECTION_INSTANCE)
            clone.compile(barcodeAggregationFactor);
        ArrayList<AccumulatorClone> clones = new ArrayList<>(COLLECTION_INSTANCE);
        Collections.sort(clones, CloneComparator.INSTANCE);
        return new CloneSetImpl(clones, gene.get(), segmentContainers.get(0).getSpecies(),
                segmentContainers.get(0), segmentContainers.get(1), segmentContainers.get(2));
    }

    @Override
    public void put(CDR3ExtractionResult cdr3ExtractionResult) {
        if (cdr3ExtractionResult == null)
            //Some closing handling
            return;

        if (cdr3ExtractionResult.getCDR3() == null)
            return; //TODO some statistics collection here... and in other places... (???) May be "StatisticsAggregator or something"...

        putResult(cdr3ExtractionResult);
    }

    /**
     * Processes one {@link CDR3ExtractionResult}. Returns true it new clone entry was created.
     *
     * @return true it new clone entry was created
     */
    protected boolean putResult(CDR3ExtractionResult cdr3ExtractionResult) {
        boolean created = false;

        //Traverse tree...
        Node node = root;
        final NucleotideSequence cdr3Sequence = cdr3ExtractionResult.getCDR3().getSequence();
        final int size = cdr3Sequence.size();
        for (int i = 1; i <= size; ++i)
            node = node.createOrGet(cdr3Sequence.codeAt(((i & 1) == 1) ? i >> 1 : size - (i >> 1)));

        if (node.clone == null)
            synchronized (node) {
                //Double checked assignment
                if (node.clone == null) {

                    if (!initialized.get())
                        initializeFromResult(cdr3ExtractionResult);

                    node.clone = cloneFactory.create(this.clonesCount.getAndIncrement(),
                            cdr3ExtractionResult.getCDR3(), saveBackwardLinks);

                    //The new clone was created
                    created = true;
                }
            }

        //Transmitting result to the found clone
        node.clone.include(cdr3ExtractionResult, false);

        if (listener != null) {
            //Firing corresponding event
            if (created)
                listener.newCloneCreated(node.clone, cdr3ExtractionResult);
            //Firing read to clone assignment event
            listener.assignedToClone(node.clone, cdr3ExtractionResult, false);
        }

        return created;
    }

    @Override
    public final void preInitialize(SegmentGroupContainer v, SegmentGroupContainer j, SegmentGroupContainer d) {
        segmentContainers.compareAndSet(0, null, v);
        segmentContainers.compareAndSet(1, null, j);
        gene.compareAndSet(null, v.getGroup().getGene());
        if (gene.get().hasDSegment())
            segmentContainers.compareAndSet(2, null, d);

        initialized.set(segmentContainers.get(0) != null &&
                segmentContainers.get(1) != null &&
                (!gene.get().hasDSegment() || segmentContainers.get(2) != null));
    }

    private void initializeFromResult(CDR3ExtractionResult result) {
        if (segmentContainers.get(0) == null) {
            segmentContainers.compareAndSet(0, null, result.getVMappingResult().getContainer());
            segmentContainers.compareAndSet(1, null, result.getJMappingResult().getContainer());
            gene.compareAndSet(null, result.getVMappingResult().getContainer().getGroup().getGene());
            if (!gene.get().hasDSegment()) {
                initialized.set(true);
                return;
            }
        }
        if (result.getDMappingResult() != null) {
            segmentContainers.compareAndSet(2, null, result.getDMappingResult().getContainer());
            initialized.set(true);
        }
    }

    private static final class Iterator implements java.util.Iterator<AccumulatorClone> {
        private Node node;
        private AccumulatorClone next;

        public Iterator(Node root) {
            this.node = new Node(root, (byte) -1);
            this.next = _next();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public AccumulatorClone next() {
            AccumulatorClone ret = next;
            next = _next();
            return ret;
        }

        public final AccumulatorClone _next() {
            byte i = (byte) (node.nucleotide + 1);
            node = node.parent;
            while (node != null) {
                if (i == 4)
                    if (node.clone != null)
                        return node.clone;
                    else {
                        i = (byte) (node.nucleotide + 1);
                        node = node.parent;
                        continue;
                    }
                if (node.next[i] == null)
                    ++i;
                else {
                    node = node.next[i];
                    i = 0;
                }
            }
            return null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private final CloneCollection COLLECTION_INSTANCE = new CloneCollection();

    private final class CloneCollection extends AbstractCollection<AccumulatorClone> {
        @Override
        public int size() {
            return clonesCount.intValue();
        }

        @Override
        public java.util.Iterator<AccumulatorClone> iterator() {
            return new Iterator(root);
        }
    }

    private TreePackingInfo getPackingInfo() {
        return getPackingInfo(root);
    }

    public static TreePackingInfo getPackingInfo(CloneGenerator generator) {
        if (generator instanceof BasicCloneGenerator)
            return ((BasicCloneGenerator) generator).getPackingInfo();
        return null;
    }

    private static TreePackingInfo getPackingInfo(Node root) {
        TreePackingInfo info = new TreePackingInfo(0, 0);
        calculatePackingInfo(root, info);
        return info;
    }

    private static void calculatePackingInfo(Node node, TreePackingInfo info) {
        if (node.clone != null)
            info.objects++;
        for (Node n : node.next)
            if (n != null) {
                info.nodes++;
                calculatePackingInfo(n, info);
            }
    }

    protected static class Node extends NTreeNode<Node> {
        public AccumulatorClone clone;

        public Node(Node parent, byte nucleotide) {
            super(parent, nucleotide);
            next = new Node[4];
        }

        public synchronized Node createOrGet(byte nucleotide) {
            if (next[nucleotide] == null)
                return next[nucleotide] = new Node(this, nucleotide);
            return next[nucleotide];
        }
    }
}
