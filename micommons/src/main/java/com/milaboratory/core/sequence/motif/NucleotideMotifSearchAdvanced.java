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
package com.milaboratory.core.sequence.motif;

import com.milaboratory.core.sequence.SequencingErrorType;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;

/**
 * Class for advanced motif search in real sequencing data. Allows errors in matches (PCR with good quality or
 * sequencing marked with low quality value).<br/><br/>
 *
 * Main purpose of this class is location of primers in PCR product sequences. It is very useful in barcode extraction
 * from barcoded cDNA library sequences.<br/><br/>
 *
 * The search method is thread-safe.
 *
 * @author Bolotin Dmitriy <bolotin.dmitriy@gmail.com>
 */
public class NucleotideMotifSearchAdvanced {
    //Nucleotide motif to search
    private final NucleotideMotif motif;
    //Max allowed count of mismatches in high quality points
    private final int maxHighQualityMismatches;
    //Max total number of mismatches
    private final int maxMismatches;
    //Max count of low quality points in matched sub-sequence
    private final int maxLowQualityPoints;
    //Sequencing error type
    private final SequencingErrorType errorType;

    public NucleotideMotifSearchAdvanced(NucleotideMotif motif, int maxHighQualityMismatches,
                                         int maxMismatches, int maxLowQualityPoints, SequencingErrorType errorType) {
        if (errorType == null || motif == null)
            throw new NullPointerException();
        if (errorType == SequencingErrorType.Insertion)
            throw new IllegalArgumentException("Insertions does not supported yet.");
        this.motif = motif;
        this.maxHighQualityMismatches = maxHighQualityMismatches;
        this.maxMismatches = maxMismatches;
        this.maxLowQualityPoints = maxLowQualityPoints;
        this.errorType = errorType;
    }

    public NucleotideMotif getMotif() {
        return motif;
    }

    public final int search(NucleotideSequence sequence, LowQualityIndicator lowQualityIndicator) {
        return search(sequence, lowQualityIndicator, 0, sequence.size());
    }

    public final int search(NucleotideSequence sequence, LowQualityIndicator lowQualityIndicator,
                            int from, int to) {
        //Parameter checking
        if (from < 0 || from >= sequence.size() || to < 0
                || to > sequence.size()
                || from > to)
            throw new IllegalArgumentException();

        //Try to find exact match
        int hit = motif.findMatch(sequence, from, to);
        //If found
        if (hit >= 0) {
            //maxLowQualityPoints checking
            int lowPoints = 0;
            for (int i = 0; i < motif.size(); ++i) {
                if (lowQualityIndicator.isLowQuality(hit + i))
                    lowPoints++;

                //Test fail algorithm
                //Recurrent search starting
                if (lowPoints > maxLowQualityPoints)
                    return search(sequence, lowQualityIndicator, hit + 1, to);
            }
            return hit;
        }

        //Advanced search
        switch (errorType) {
            case Mismatch:
                return mismatchedSearch(sequence, lowQualityIndicator, from, to);
            default:
                throw new RuntimeException();
        }
    }

    //--------Mismatched Search-----------
    //
    private int mismatchedSearch(NucleotideSequence sequence, LowQualityIndicator lowQualityIndicator,
                                 int from_, int to_) {
        final int to = to_ - motif.size();
        SearchResult bestResult = null, result;
        for (int i = from_; i < to; ++i) {
            result = mismatchedSearch(i, sequence, lowQualityIndicator);
            if (result != null
                    && (bestResult == null || bestResult.compareTo(result) > 0))
                bestResult = result;
        }
        if (bestResult == null)
            return -1;
        else
            return bestResult.coord;
    }

    private SearchResult mismatchedSearch(int from, NucleotideSequence sequence, LowQualityIndicator lowQualityIndicator) {
        int highMismatches = 0, lowMismatches = 0, lowPoints = 0;
        boolean isLowQuality;
        for (int i = 0; i < motif.size(); ++i) {
            if (isLowQuality = lowQualityIndicator.isLowQuality(from + i))
                ++lowPoints;
            if (!motif.get(i, sequence.codeAt(from + i)))
                if (isLowQuality)
                    ++lowMismatches;
                else
                    ++highMismatches;
            if (highMismatches + lowMismatches > maxMismatches
                    || highMismatches > maxHighQualityMismatches
                    || lowPoints > maxLowQualityPoints)
                return null;
        }
        return new SearchResult(highMismatches, lowMismatches + highMismatches, lowPoints, from);
    }

    //Intermediate result
    private final class SearchResult implements Comparable<SearchResult> {
        public final int highQualityMismatches;
        public final int mismatches;
        public final int lowQualityPoints;
        public final int coord;

        public SearchResult(int highQualityMismatches, int mismatches, int lowQualityPoints, int coord) {
            this.highQualityMismatches = highQualityMismatches;
            this.mismatches = mismatches;
            this.lowQualityPoints = lowQualityPoints;
            this.coord = coord;
        }

        /**
         * o1.compareTo(o2) &lt; 0 means o1 is better then o2
         */
        @Override
        public int compareTo(SearchResult o) {
            int val;
            if ((val = Integer.compare(highQualityMismatches, o.highQualityMismatches)) != 0)
                return val;
            if ((val = Integer.compare(mismatches, o.mismatches)) != 0)
                return val;
            if ((val = Integer.compare(lowQualityPoints, o.lowQualityPoints)) != 0)
                return val;
            return 0;
        }
    }
}
