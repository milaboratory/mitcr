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
package com.milaboratory.mitcr.qualitystrategy;

/**
 * Nucleotide sequence with additional information about each nucleotide status (good / bad). The internal input type
 * for several tree-based alignment algorithms.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public interface GoodBadNucleotideSequence {
    /**
     * Return nucleotide code at specified position (see {@link com.milaboratory.core.sequence.nucleotide.NucleotideAlphabet}).
     *
     * @param position position in sequence
     * @return nucleotide code at specified position
     */
    byte codeAt(int position);

    /**
     * Length of the sequence.
     *
     * @return size of sequence
     */
    int size();

    /**
     * Return status of specified nucleotide (bad = true / good = false).
     *
     * @return true if specified nucleotide is bad
     */
    boolean isBad(int position);
}
