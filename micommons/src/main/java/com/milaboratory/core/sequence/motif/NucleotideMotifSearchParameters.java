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
import com.milaboratory.core.sequencing.read.SSequencingReadImpl;

public class NucleotideMotifSearchParameters {
    private NucleotideMotif motif;
    private int maxHighQualityMismatches = 0, maxMismatches = 1, maxLowQualityPoints = Integer.MAX_VALUE;
    private byte lowQualityThreshold = 20;
    private SequencingErrorType sequencingErrorType = SequencingErrorType.Mismatch;

    public NucleotideMotifSearchParameters() {
    }

    public NucleotideMotifSearchParameters(NucleotideMotif motif) {
        this.motif = motif;
    }

    public NucleotideMotifSearchParameters(String motif) {
        this.motif = new NucleotideMotif(motif);
    }

    public NucleotideMotifSearchParameters(String motif, NucleotideMotifSearchParameters paramsToCopyFrom) {
        this.motif = new NucleotideMotif(motif);
        loadFrom(paramsToCopyFrom);
    }

    private void loadFrom(NucleotideMotifSearchParameters params) {
        this.maxHighQualityMismatches = params.maxHighQualityMismatches;
        this.maxMismatches = params.maxMismatches;
        this.maxLowQualityPoints = params.maxLowQualityPoints;
        this.lowQualityThreshold = params.lowQualityThreshold;
        this.sequencingErrorType = params.sequencingErrorType;
    }

    public NucleotideMotif getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = new NucleotideMotif(motif);
    }

    public void setMotif(NucleotideMotif motif) {
        this.motif = motif;
    }

    public int getMaxHighQualityMismatches() {
        return maxHighQualityMismatches;
    }

    public void setMaxHighQualityMismatches(int maxHighQualityMismatches) {
        this.maxHighQualityMismatches = maxHighQualityMismatches;
    }

    public int getMaxMismatches() {
        return maxMismatches;
    }

    public void setMaxMismatches(int maxMismatches) {
        this.maxMismatches = maxMismatches;
    }

    public int getMaxLowQualityPoints() {
        return maxLowQualityPoints;
    }

    public void setMaxLowQualityPoints(int maxLowQualityPoints) {
        this.maxLowQualityPoints = maxLowQualityPoints;
    }

    public byte getLowQualityThreshold() {
        return lowQualityThreshold;
    }

    public void setLowQualityThreshold(byte lowQualityThreshold) {
        this.lowQualityThreshold = lowQualityThreshold;
    }

    public SequencingErrorType getSequencingErrorType() {
        return sequencingErrorType;
    }

    public void setSequencingErrorType(SequencingErrorType sequencingErrorType) {
        this.sequencingErrorType = sequencingErrorType;
    }

    public NucleotideMotifSearchAdvanced create() {
        return new NucleotideMotifSearchAdvanced(motif, maxHighQualityMismatches, maxMismatches, maxLowQualityPoints, sequencingErrorType);
    }

    public NucleotideMotifSearchAdvancedWrapper<SSequencingReadImpl> createWrapperForSRead() {
        return new NucleotideMotifSearchAdvancedWrapper<SSequencingReadImpl>(create(), SSequencingReadBindings.sqProvider(), SSequencingReadBindings.lqProvider(lowQualityThreshold));
    }

    public static NucleotideMotifSearchParameters getSTDParameters(String motif) {
        return new NucleotideMotifSearchParameters(motif, std);
    }

    public static NucleotideMotifSearchParameters getFUZZYParameters(String motif) {
        return new NucleotideMotifSearchParameters(motif, fuzzy);
    }

    private static final NucleotideMotifSearchParameters std, fuzzy;

    static {
        std = new NucleotideMotifSearchParameters((NucleotideMotif) null);
        std.sequencingErrorType = SequencingErrorType.Mismatch;
        std.lowQualityThreshold = 20;
        std.maxLowQualityPoints = Integer.MAX_VALUE;
        std.maxMismatches = 1;
        std.maxHighQualityMismatches = 0;

        fuzzy = new NucleotideMotifSearchParameters((NucleotideMotif) null);
        fuzzy.sequencingErrorType = SequencingErrorType.Mismatch;
        fuzzy.lowQualityThreshold = 20;
        fuzzy.maxLowQualityPoints = Integer.MAX_VALUE;
        fuzzy.maxMismatches = 3;
        fuzzy.maxHighQualityMismatches = 1;
    }
}
