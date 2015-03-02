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
package com.milaboratory.mitcr.cdrextraction;

import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.mitcr.vdjmapping.SegmentMappingResult;
import com.milaboratory.mitcr.vdjmapping.VJSegmentMappingResult;
import com.milaboratory.util.BitArray;

import java.io.Serializable;

/**
 * A class to hold CDR3 extraction result
 *
 * @param <I> Type of source read from which CDR3 was extracted
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class CDR3ExtractionResult<I> implements Serializable {
    private final boolean foundInReverseComplement;
    private final byte readIndex;
    private final VJSegmentMappingResult[] vjResults;
    private final SegmentMappingResult dResult;
    private final NucleotideSQPair cdr3;
    private final I source;

    /**
     * A CDR3 extraction result
     *
     * @param source                   read from which CDR was extracted
     * @param vjResults                results for V and J segments mapping
     * @param dResult                  result for D segment mapping
     * @param cdr3                     CDR3 sequence with quality
     * @param foundInReverseComplement is CDR3 found in reverse complement of read
     */
    protected CDR3ExtractionResult(I source, VJSegmentMappingResult[] vjResults,
                                   SegmentMappingResult dResult, NucleotideSQPair cdr3,
                                   boolean foundInReverseComplement) {
        this(source, vjResults, dResult, cdr3, foundInReverseComplement, (byte) 0);
    }

    /**
     * A CDR3 extraction result
     *
     * @param source                   read from which CDR was extracted
     * @param vjResults                results for V and J segments mapping
     * @param dResult                  result for D segment mapping
     * @param cdr3                     CDR3 sequence with quality
     * @param foundInReverseComplement is CDR3 found in reverse complement of read
     * @param readIndex                index of read where CDR3 found (0 or 1)
     */
    protected CDR3ExtractionResult(I source, VJSegmentMappingResult[] vjResults,
                                   SegmentMappingResult dResult, NucleotideSQPair cdr3,
                                   boolean foundInReverseComplement, byte readIndex) {
        this.foundInReverseComplement = foundInReverseComplement;
        this.vjResults = vjResults;
        this.dResult = dResult;
        this.cdr3 = cdr3;
        this.source = source;
        this.readIndex = readIndex;
    }

    /**
     * Is CDR3 found in reverse complement of read
     *
     * @return {@code true} if CDR3 was extracted from reverse complement
     */
    public boolean isFoundInReverseComplement() {
        return foundInReverseComplement;
    }

    /**
     * Gets results for V segment mapping
     *
     * @return results for V segment mapping or {@code null}  if not mapped
     */
    public VJSegmentMappingResult getVMappingResult() {
        return vjResults[0];
    }

    /**
     * Gets results for J segment mapping
     *
     * @return results for J segment mapping or {@code null} if not mapped
     */
    public VJSegmentMappingResult getJMappingResult() {
        return vjResults[1];
    }

    /**
     * Gets results for V and J segment mapping
     *
     * @return results for V and J segment mapping
     */
    public VJSegmentMappingResult[] getVJMappingResults() {
        return vjResults;
    }

    /**
     * Gets results for D segment mapping
     *
     * @return results for D segment mapping or {@code null}  if not mapped
     */
    public SegmentMappingResult getDMappingResult() {
        return dResult;
    }

    /**
     * Gets CDR3 sequence with quality
     *
     * @return CDR3 sequence with quality
     */
    public NucleotideSQPair getCDR3() {
        return cdr3;
    }

    /**
     * Gets the source read for which mapping was performed
     *
     * @return source read for which mapping was performed
     */
    public I getSource() {
        return source;
    }

    /**
     * Gets index of read where CDR3 found (0 or 1)
     *
     * @return index of read where CDR3 found
     */
    public byte getReadIndex() {
        return readIndex;
    }

    public boolean isExtracted() {
        return cdr3 != null;
    }

    public <T> CDR3ExtractionResult<T> substituteSource(T newSource) {
        return new CDR3ExtractionResult<T>(newSource, vjResults, dResult, cdr3, foundInReverseComplement, readIndex);
    }

    public float getAlignmentsScore() {
        float sum = 0;
        if (vjResults[0] != null)
            sum += vjResults[0].getScore();
        if (vjResults[1] != null)
            sum += vjResults[1].getScore();
        return sum;
    }

    /**
     * Internally used method.
     *
     * 0 = V; 1 = J; 2 = D
     */
    public BitArray getBarcode(int index) {
        if (index < 2)
            return vjResults[index].getBarcode();
        if (index == 2)
            if (dResult == null)
                return null;
            else
                return dResult.getBarcode();
        throw new IndexOutOfBoundsException();
    }
}
