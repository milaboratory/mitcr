package com.milaboratory.core.sequence.motif.search;

import com.milaboratory.core.sequence.motif.NucleotideMotif;

public class NucleotideMotifSearch {
    private final NucleotideMotif motif;
    private final NucleotideMotifSearchOptions options;
    private final int exactRegionFrom, exactRegionTo;

    public NucleotideMotifSearch(NucleotideMotif motif, NucleotideMotifSearchOptions options, int exactRegionFrom, int exactRegionTo) {
        this.motif = motif;
        this.options = options;
        this.exactRegionFrom = exactRegionFrom;
        this.exactRegionTo = exactRegionTo;
    }

//    public int nextMatch(int from, NucleotideSequence sequence) {
//        if (exactRegionFrom >= 0) { //If exact region exists
//            for (int i = 0; i < sequence.size(); ++i) {
//
//            }
//        }
//    }
}
