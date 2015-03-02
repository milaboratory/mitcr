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

import com.milaboratory.core.clone.*;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.util.CanReportProgress;

import java.util.*;

/**
 * Penalization-based clustering algorithm. Organizes clones in {@link MonolayerCluster}s so that the penalty is
 * minimized in a heuristic way
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class PenaltyBasedFastClusterizer implements CloneClusterizer, CanReportProgress {
    private final Node root = new Node((byte) -1, null);
    private final int maxMismatches;
    private volatile double donePart = 0.0;
    private boolean finished = false;
    private final float maxPenalty;
    private final float maxRatio;
    private final Deque<Clone> unclustered = new ArrayDeque<>();
    private final List<Slider> currentGenerationHolder = new ArrayList<>(), nextGenerationHolder = new ArrayList<>();
    private final List<Node> foundNodesHolder = new ArrayList<>();
    private final ClusterizationListener listener;
    private final PenaltyCalculator penaltyCalculator;

    public PenaltyBasedFastClusterizer(float maxRatio, CloneClusterizationType clusterizationType) {
        this(maxRatio, clusterizationType.getPenaltyCalculator(), null);
    }

    public PenaltyBasedFastClusterizer(float maxRatio, CloneClusterizationType clusterizationType,
                                       ClusterizationListener listener) {
        this(maxRatio, clusterizationType.getPenaltyCalculator(), listener);
    }

    public PenaltyBasedFastClusterizer(float maxRatio, PenaltyCalculator penaltyCalculator) {
        this(maxRatio, penaltyCalculator, null);
    }

    public PenaltyBasedFastClusterizer(float maxRatio, PenaltyCalculator penaltyCalculator, ClusterizationListener listener) {
        if (penaltyCalculator == null)
            throw new NullPointerException();
        this.maxRatio = maxRatio;
        this.penaltyCalculator = penaltyCalculator;
        this.maxMismatches = penaltyCalculator.getMaxMismatches();
        this.maxPenalty = penaltyCalculator.getMaxPenaltyValue();
        this.listener = listener;
    }

    @Override
    public CloneSetClustered cluster(CloneSet cloneSet) {
        unclustered.addAll(cloneSet.getClones());

        cluster();

        List<MonolayerCluster<Clone>> mlclusters = getClusters();
        List<CloneCluster> clusters = new ArrayList<>(mlclusters.size());
        for (MonolayerCluster<Clone> cluster : mlclusters)
            clusters.add(new CloneClusterImpl(cluster.center, cluster.getLeaves()));

        Collections.sort(clusters, CloneComparator.INSTANCE);

        finished = true;
        return new CloneSetClusteredImpl(cloneSet, clusters);
    }

    private void cluster() {
        final ClusterizationListener listener = this.listener;
        List<Slider> currentGeneration = currentGenerationHolder;
        List<Slider> nextGeneration = nextGenerationHolder;
        final List<Node> foundNodes = foundNodesHolder;
        final int totalCount = unclustered.size();

        while (!unclustered.isEmpty()) {
            //Updating progress information
            donePart = 1.0 * (totalCount - unclustered.size()) / totalCount;

            Clone element = unclustered.pop();
            NucleotideSequence sequence = element.getCDR3().getSequence();
            List<Slider> temp;

            //First stage filtering (mismatches only)
            currentGeneration.clear();
            currentGeneration.add(new Slider(root));
            for (int coord = 0; coord < sequence.size(); ++coord) {
                //Forming nextGeneration
                slideGenerationWithMismatches(currentGeneration, nextGeneration, sequence.codeAt(coord), maxMismatches);

                //Swap generations
                temp = nextGeneration;
                nextGeneration = currentGeneration;
                currentGeneration = temp;

                //Clear next generation
                nextGeneration.clear();

                //Early termination
                if (currentGeneration.isEmpty())
                    break;
            }

            //Searching for clone with lowest penalty, having maximal count
            //In parallel collecting connected clusters (second stage filtering [penalty])
            Node maxN = null;
            float pTemp;
            foundNodes.clear();  //Nodes to add if max cluster will be reformed (see below)
            Node n;
            for (Slider slider : currentGeneration)
                if ((n = slider.node).value != null) { //Not all sliders are really connected to the element
                    pTemp = penaltyCalculator.getTotalPenalty(element, n.value.center);

                    if (pTemp <= maxPenalty) {
                        foundNodes.add(n); //Collecting really connected nodes
                        if (maxN == null || maxN.value.center.getCount() < n.value.center.getCount())
                            maxN = n;
                    }

                    //Double goals maximization (1-st => min <- penalty; 2-nd => max <- count)
                    //if (pTemp < penalty) { //Better
                    //    maxN = n;
                    //    penalty = pTemp;
                    //} else if (pTemp == penalty && (maxN == null || maxN.value.center.getCount() < n.value.center.getCount())) //Equals but bigger cluster center or having max allowed penalty
                    //    maxN = n;
                }

            //Create new cluster
            if (maxN == null) { //No connected clusters found
                //Firing create event
                if (listener != null)
                    listener.clusterCenterCreated(element);
                put(sequence, new MonolayerCluster<>(element));
                continue;
            }

            //Here is max cluster
            final MonolayerCluster<Clone> max = maxN.value;

            //If element is less then center add new leaf
            if (max.center.getCount() >= element.getCount()) {
                if (max.center.getCount() * maxRatio >= element.getCount()) {
                    //Firing clusterized event
                    if (listener != null)
                        listener.pairClusterized(max.center, element, max.getLeaves());
                    max.addLeaf(element);
                } else {
                    //Firing create event
                    if (listener != null)
                        listener.clusterCenterCreated(element);
                    put(sequence, new MonolayerCluster<>(element)); //New cluster
                }
                continue;
            }

            //Reform max cluster, remove minor clusters and drain all elements to unclustered
            if (listener != null)
                listener.clusterBroken(max.center, max.getLeaves());

            if (max.leaves != null) {
                for (Clone majorOldLeaf : max.leaves)
                    unclustered.push(majorOldLeaf);
                max.leaves.clear();
            }

            if (listener != null) {
                listener.clusterCenterCreated(element);
                listener.pairClusterized(element, max.center, Collections.EMPTY_LIST);
            }

            max.addLeaf(max.center);
            max.center = element;

            //Processing other found clusters
            MonolayerCluster<Clone> minorCluster;
            for (Node node : foundNodes)
                if ((minorCluster = node.value) != max) {
                    if (listener != null) {
                        listener.clusterBroken(minorCluster.center, minorCluster.getLeaves());
                        listener.pairClusterized(max.center, minorCluster.center, max.getLeaves());
                    }
                    max.addLeaf(minorCluster.center);
                    if (minorCluster.leaves != null) //Drain child clusters leaves to unclustered
                        for (Clone minorLeaf : minorCluster.leaves)
                            unclustered.push(minorLeaf);
                    node.value = null; //Remove from tree (leaving this branch [is it good?])
                }


            //Moving to the correct branch of the tree
            maxN.value = null;
            put(sequence, max);
        }
    }

    List<MonolayerCluster<Clone>> getClusters() {
        List<MonolayerCluster<Clone>> clusters = new ArrayList<>();
        int i = 0;
        Node node = root; //new Node((byte) -1, root);
        while (node != null) {
            if (i == 4) {
                if (node.value != null)
                    clusters.add(node.value);
                i = node.nucleotide + 1;
                node = node.parent;
                continue;
            }
            if (node.next[i] != null) {
                node = node.next[i];
                i = 0;
            } else
                ++i;
        }
        return clusters;
    }

    @Override
    public double getProgress() {
        return donePart;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    public void put(NucleotideSequence key, MonolayerCluster<Clone> value) {
        Node node = root;
        for (int i = 0; i < key.size(); ++i)
            node = node.getOrCreateNode(key.codeAt(i));
        node.value = value;
    }

    private static <T> void slideGenerationWithMismatches(List<Slider> currentGeneration, List<Slider> nextGeneration,
                                                          byte nucleotide, int maxMismatches) {
        for (Slider slider : currentGeneration)
            for (int i = 0; i < 4; ++i) {
                Node node = slider.node.next[i];
                if (node == null)
                    continue;
                if (i != nucleotide && slider.mismatches == maxMismatches)
                    continue;
                nextGeneration.add(new Slider(node, i == nucleotide
                        ? slider.mismatches : slider.mismatches + 1));
            }
    }

    protected static class Slider {
        public Node node;
        public int mismatches;

        public Slider(Node currentNode) {
            this.node = currentNode;
            this.mismatches = 0;
        }

        public Slider(Node currentNode, int mismatches) {
            this.node = currentNode;
            this.mismatches = mismatches;
        }

        public boolean tryMove(byte nucleotide) {
            node = node.next[nucleotide];
            return node != null;
        }
    }

    protected static class Node {
        public final byte nucleotide;
        public final Node parent;
        public final Node[] next = new Node[4];
        public MonolayerCluster<Clone> value;

        public Node(byte nucleotide, Node parent) {
            this.nucleotide = nucleotide;
            this.parent = parent;
        }

        public Node getOrCreateNode(byte nucleotide) {
            if (next[nucleotide] == null)
                return next[nucleotide] = new Node(nucleotide, this);
            return next[nucleotide];
        }

        public boolean isFree() {
            if (value != null)
                return false;
            for (int i = 0; i < 4; ++i)
                if (next[i] != null)
                    return false;
            return true;
        }
    }
}
