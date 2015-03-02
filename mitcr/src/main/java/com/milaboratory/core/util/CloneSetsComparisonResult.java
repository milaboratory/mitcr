package com.milaboratory.core.util;

public class CloneSetsComparisonResult {
    public final int newClones, missedClones, newSequences, missedSequences;
    public final double difference;

    public CloneSetsComparisonResult(int newClones, int missedClones, int newSequences, int missedSequences, double difference) {
        this.newClones = newClones;
        this.missedClones = missedClones;
        this.newSequences = newSequences;
        this.missedSequences = missedSequences;
        this.difference = difference;
    }
}
