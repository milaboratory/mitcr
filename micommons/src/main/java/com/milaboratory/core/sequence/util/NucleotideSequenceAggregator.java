package com.milaboratory.core.sequence.util;

import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.util.Bit2Array;

public class NucleotideSequenceAggregator {
    final long[] observations;
    long sequences = 0;
    final int length, delta;

    public NucleotideSequenceAggregator(int length, int delta) {
        this.observations = new long[(length + 2 * delta) * 4];
        this.length = length;
        this.delta = delta;
    }

    public long getObservations(int position, int code) {
        if (code < 0 || code >= 4 || position < 0 || position >= length + 2 * delta)
            throw new IllegalArgumentException();

        return observations[position * 4 + code];
    }

    private long getScore(NucleotideSequence sequence, int d) {
        long result = 0;

        for (int i = sequence.size() - 1; i >= 0; --i)
            result += observations[(delta + i + d) * 4 + sequence.codeAt(i)];

        return result;
    }

    public long getSequences() {
        return sequences;
    }

    private void putSequence(NucleotideSequence sequence, int d) {
        ++sequences;
        for (int i = sequence.size() - 1; i >= 0; --i)
            ++observations[(delta + i + d) * 4 + sequence.codeAt(i)];
    }

    public void putSequence(NucleotideSequence sequence) {
        if (sequence.size() != length)
            throw new IllegalArgumentException();

        if (sequences == 0) {
            putSequence(sequence, 0);
            return;
        }

        int maxD = 0;
        long maxScore = getScore(sequence, 0), score;

        for (int d = 1; d <= delta; ++d) {
            if ((score = getScore(sequence, d)) > maxScore) {
                maxScore = score;
                maxD = d;
            }
            if ((score = getScore(sequence, -d)) > maxScore) {
                maxScore = score;
                maxD = -d;
            }
        }

        putSequence(sequence, maxD);
    }

    public NucleotideSequence getSequence(double minPercent) {
        int start = 0, end = 0, lastBad = -1;
        byte maxCode, code;
        long maxCount, sum;
        double percent;

        for (int i = 0; i < length + delta * 2; ++i) {
            //maxCode = -1;
            maxCount = 0;
            sum = 0;

            for (code = 0; code < 4; ++code) {
                sum += observations[i * 4 + code];
                if (maxCount < observations[i * 4 + code]) {
                    maxCount = observations[i * 4 + code];
                    //maxCode = code;
                }
            }

            if (sum == 0) {
                lastBad = i;
                continue;
            }

            percent = 1.0 * maxCount / sum;

            if (percent < minPercent)
                lastBad = i;
            else if (end - start < i - lastBad) {
                end = i + 1;
                start = lastBad + 1;
            }
        }

        Bit2Array st = new Bit2Array(end - start);

        for (int i = start; i < end; ++i) {
            maxCode = -1;
            maxCount = 0;

            for (code = 0; code < 4; ++code)
                if (maxCount < observations[i * 4 + code]) {
                    maxCount = observations[i * 4 + code];
                    maxCode = code;
                }

            st.set(i - start, maxCode);
        }

        return NucleotideSequence.fromStorage(st);
    }
}
