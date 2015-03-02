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
package com.milaboratory.core.sequencing.io.sff;

import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class SFFHeader {
    private char[] version;
    private long indexOffset;
    private int indexLength;
    private int numberOfReads;
    private int keyLength;
    private int flowgramFormatCode;
    private int numberOfFlows;
    private char[] flowChars;
    private char[] keySequence;
    private NucleotideSequence flowsSequence;

    public SFFHeader(char[] version, long indexOffset, int indexLength, int numberOfReads, int keyLength, int flowgramFormatCode, int numberOfFlows, char[] flowChars, char[] keySequence) {
        this.version = version;
        this.indexOffset = indexOffset;
        this.indexLength = indexLength;
        this.numberOfReads = numberOfReads;
        this.keyLength = keyLength;
        this.flowgramFormatCode = flowgramFormatCode;
        this.numberOfFlows = numberOfFlows;
        this.flowChars = flowChars;
        this.keySequence = keySequence;
        this.flowsSequence = new NucleotideSequence(flowChars);
    }

    public char[] getFlowChars() {
        return flowChars;
    }

    public int getFlowgramFormatCode() {
        return flowgramFormatCode;
    }

    public int getIndexLength() {
        return indexLength;
    }

    public long getIndexOffset() {
        return indexOffset;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public char[] getKeySequence() {
        return keySequence;
    }

    public int getNumberOfFlows() {
        return numberOfFlows;
    }

    public int getNumberOfReads() {
        return numberOfReads;
    }

    public char[] getVersion() {
        return version;
    }

    public NucleotideSequence getFlowsSequence() {
        return flowsSequence;
    }

    @Override
    public String toString() {
        return "SFFHeader{" +
                "version=" + version +
                ", indexOffset=" + indexOffset +
                ", indexLength=" + indexLength +
                ", numberOfReads=" + numberOfReads +
                ", keyLength=" + keyLength +
                ", flowgramFormatCode=" + flowgramFormatCode +
                ", numberOfFlows=" + numberOfFlows +
                ", flowChars=" + flowChars +
                ", keySequence=" + keySequence +
                ", flowsSequence=" + flowsSequence +
                '}';
    }
}
