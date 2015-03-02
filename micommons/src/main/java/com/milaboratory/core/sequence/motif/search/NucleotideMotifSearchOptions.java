package com.milaboratory.core.sequence.motif.search;

public class NucleotideMotifSearchOptions {
    public static NucleotideMotifSearchOptions NORMAL_WITH_TRUNCATION = new NucleotideMotifSearchOptions(2, 1, 1, 2, 2);
    public static NucleotideMotifSearchOptions NORMAL_WITHOUT_TRUNCATION = new NucleotideMotifSearchOptions(2, 1, 1, 2, 0);

    public final int maxMismatches, maxDeletions, maxInsertions, maxTotalErrors;
    public final int maxLeftTruncation;

    public NucleotideMotifSearchOptions(int maxMismatches, int maxDeletions, int maxInsertions, int maxTotalErrors, int maxLeftTruncation) {
        this.maxMismatches = maxMismatches;
        this.maxDeletions = maxDeletions;
        this.maxInsertions = maxInsertions;
        this.maxTotalErrors = maxTotalErrors;
        this.maxLeftTruncation = maxLeftTruncation;
    }
}
