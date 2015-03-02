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
package com.milaboratory.mitcr.statistics;

import com.milaboratory.core.clone.Clone;
import com.milaboratory.core.clone.CloneCluster;
import com.milaboratory.core.clone.CloneSet;
import com.milaboratory.core.clone.CloneSetClustered;
import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.SegmentGroupType;
import com.milaboratory.core.segment.SegmentLibrary;
import com.milaboratory.core.segment.Species;
import com.milaboratory.util.BitArray;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that performs several quality controls on a clone set
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class CloneSetQualityControl {
    //private AtomicInteger perSeqVNotDetermined = new AtomicInteger(0), perSeqJNotDetermined = new AtomicInteger(0);
    private final long totalGoodReads, totalReads;
    private final long readsClusterized;
    private final int clonesClusterized, coreClones;
    private final int clones;
    private final SegmentLibrary library;
    private final Species species;
    private final Gene gene;
    private final Set<BitArray> goodVCombinations;
    private final long oofSequences;
    private final int oofClones;
    private final long jNotDetermined;
    private final int jNotDeterminedClones;
    private final long vNotDetermined;
    private final int vNotDeterminedClones;
    private final int withStopsClones;
    private final long withStopsSequences;
    private final long m1, p1;
    private final int m1c, p1c;

    /**
     * Creates an instance of a class that performs several quality controls on a clone set
     *
     * @param library  parent library of segments
     * @param species  parent species
     * @param gene     parent gene
     * @param cloneSet clone set to check
     */
    public CloneSetQualityControl(SegmentLibrary library, Species species, Gene gene, CloneSet cloneSet, long totalReads) {
        this.totalReads = totalReads;
        this.library = library;
        this.species = species;
        this.gene = gene;
        if (gene == Gene.TRB && species == Species.HomoSapiens) {
            BitArray good12 = new BitArray(library.getGroup(species, gene, SegmentGroupType.Variable).getSegmentCount());
            good12.set(library.getGroup(species, gene, SegmentGroupType.Variable).getSegmentByName("TRBV12-3").getIndex());
            good12.set(library.getGroup(species, gene, SegmentGroupType.Variable).getSegmentByName("TRBV12-4").getIndex());
            BitArray good6 = new BitArray(library.getGroup(species, gene, SegmentGroupType.Variable).getSegmentCount());
            good6.set(library.getGroup(species, gene, SegmentGroupType.Variable).getSegmentByName("TRBV6-2").getIndex());
            good6.set(library.getGroup(species, gene, SegmentGroupType.Variable).getSegmentByName("TRBV6-3").getIndex());
            goodVCombinations = new HashSet<>();
            goodVCombinations.add(good12);
            goodVCombinations.add(good6);
        } else {
            goodVCombinations = Collections.EMPTY_SET;
        }

        long oofSequences = 0;
        int oofClones = 0;
        long jNotDetermined = 0;
        int jNotDeterminedClones = 0;
        long vNotDetermined = 0;
        int vNotDeterminedClones = 0;
        int withStopsClones = 0;
        long withStopsSequences = 0;
        long m1 = 0, p1 = 0;
        int m1c = 0, p1c = 0;
        this.clones = cloneSet.getClones().size();
        this.totalGoodReads = cloneSet.getTotalCount();
        for (Clone clone : cloneSet.getClones()) {
            int rem = clone.getCDR3().size() % 3;
            if (rem != 0) {
                oofClones++;
                oofSequences += clone.getCount();
            }
            if (rem == 1) {
                p1 += clone.getCount();
                p1c++;
            }
            if (rem == 2) {
                m1 += clone.getCount();
                m1c++;
            }
            if (clone.getCDR3AA().containStops()) {
                withStopsClones++;
                withStopsSequences += clone.getCount();
            }
            if (clone.getVSegments().size() > 1 && !goodVCombinations.contains(clone.getVSegments().getBitArrayCopy())) {
                vNotDetermined += clone.getCount();
                ++vNotDeterminedClones;
            }
            if (clone.getJSegments().size() > 1) {
                jNotDetermined += clone.getCount();
                ++jNotDeterminedClones;
            }
        }
        this.oofClones = oofClones;
        this.oofSequences = oofSequences;
        this.jNotDetermined = jNotDetermined;
        this.jNotDeterminedClones = jNotDeterminedClones;
        this.vNotDetermined = vNotDetermined;
        this.vNotDeterminedClones = vNotDeterminedClones;
        this.withStopsClones = withStopsClones;
        this.withStopsSequences = withStopsSequences;
        this.m1 = m1;
        this.p1 = p1;
        this.m1c = m1c;
        this.p1c = p1c;

        if (cloneSet instanceof CloneSetClustered) {
            int cc = 0;
            long rc = 0;
            for (CloneCluster cluster : ((CloneSetClustered) cloneSet).getClones()) {
                cc += cluster.getChildClones().size();
                for (Clone clone : cluster.getChildClones())
                    rc += clone.getCount();
            }
            clonesClusterized = cc;
            readsClusterized = rc;
        } else {
            clonesClusterized = 0;
            readsClusterized = 0;
        }
        coreClones = clones + clonesClusterized;
    }

    /**
     * Gets number of sequences with frame shift in CDR3
     */
    public long getOutOfFrameSequences() {
        return oofSequences;
    }

    /**
     * Gets number of clones with frame shift in CDR3
     */
    public int getOutOfFrameClones() {
        return oofClones;
    }

    /**
     * Gets number of sequences with J not determined
     */
    public long getJNotDeterminedSequences() {
        return jNotDetermined;
    }

    /**
     * Gets number of clones with J not determined
     */
    public int getJNotDeterminedClones() {
        return jNotDeterminedClones;
    }

    /**
     * Gets number of sequences with V not determined
     */
    public long getVNotDeterminedSequences() {
        return vNotDetermined;
    }

    /**
     * Gets number of clones with V not determined
     */
    public int getVNotDeterminedClones() {
        return vNotDeterminedClones;
    }

    /**
     * Gets number of clones with stop codon in CDR3
     */
    public int getWithStopsClones() {
        return withStopsClones;
    }

    /**
     * Gets number of sequences with stop codon in CDR3
     */
    public long getWithStopsSequences() {
        return withStopsSequences;
    }

    /**
     * Gets number of sequences with -1 frame shift in CDR3
     */
    public long getM1() {
        return m1;
    }

    /**
     * Gets number of sequences with +1 frame shift in CDR3
     */
    public long getP1() {
        return p1;
    }

    /**
     * Gets number of clones with -1 frame shift in CDR3
     */
    public int getM1Clones() {
        return m1c;
    }

    /**
     * Gets number of clones with +1 frame shift in CDR3
     */
    public int getP1Clones() {
        return p1c;
    }

    public long getTotalGoodReads() {
        return totalGoodReads;
    }

    public long getTotalReads() {
        return totalReads;
    }

    public int getClones() {
        return clones;
    }

    public long getReadsClusterized() {
        return readsClusterized;
    }

    public int getClonesClusterized() {
        return clonesClusterized;
    }

    //TODO rename?
    public int getCoreClones() {
        return coreClones;
    }
    /*public void process(CloneSet input) {

    input.getMetadataContainer().add("qual.outOfFrameSequences", oofSequences);
        input.getMetadataContainer().add("qual.outOfFrameClones", oofClones);
        input.getMetadataContainer().add("qual.p1", p1);
        input.getMetadataContainer().add("qual.m1", m1);
        input.getMetadataContainer().add("qual.withStopsClones", withStopsClones);
        input.getMetadataContainer().add("qual.withStopsSequences", withStopsSequences);
        input.getMetadataContainer().add("qual.vNotDeterminedInClones", vNotDetermined);
        input.getMetadataContainer().add("qual.jNotDeterminedInClones", jNotDetermined);
        input.getMetadataContainer().add("qual.vNotDetermined", perSeqVNotDetermined.get());
        input.getMetadataContainer().add("qual.jNotDetermined", perSeqJNotDetermined.get());
    }*/

    /*@Override
    public void put(SExtractorResult object) throws InterruptedException {
        if (object != null && object.isGood()) { //Such clones will be accepted by GoodCDR3Filter on next step (in SAnalyser).
            BitArray vBarcodeSegments = library.getGroup(species, gene, SegmentGroupType.Variable).converToSegments(object.barcode(SegmentGroupType.Variable));
            if (vBarcodeSegments.bitCount() > 1) {
                boolean combFound = false;
                for (BitArray good : goodVCombinations)
                    if (vBarcodeSegments.equals(good)) {
                        combFound = true;
                        break;
                    }
                if (!combFound)
                    perSeqVNotDetermined.incrementAndGet();
            }
            BitArray jBarcodeSegments = library.getGroup(species, gene, SegmentGroupType.Joining).converToSegments(object.barcode(SegmentGroupType.Joining));
            if (jBarcodeSegments.bitCount() > 1)
                perSeqJNotDetermined.incrementAndGet();
        }
    }*/
}
