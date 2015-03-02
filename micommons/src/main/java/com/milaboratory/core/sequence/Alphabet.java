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
package com.milaboratory.core.sequence;

/**
 * An interface for sequence letters alphabet. (Amino acid, nucleotide, etc...)
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public interface Alphabet {
    /**
     * Gets a char from binary code
     *
     * @param code binary code of segment
     * @return corresponding char
     */
    public char symbolFromCode(byte code);

    /**
     * Gets the number of letters in the alphabet TODO: rename?
     *
     * @return the number of letters in the alphabet
     */
    public byte codesCount();

    /**
     * Gets the code corresponding to given symbol
     *
     * @param symbol symbol to convert
     * @return binary code of the symbol
     */
    public byte codeFromSymbol(char symbol);

    public SequenceBuilderFactory getBuilderFactory();

    /**
     * Id of alphabet
     */
    public int alphabetCode();
}
