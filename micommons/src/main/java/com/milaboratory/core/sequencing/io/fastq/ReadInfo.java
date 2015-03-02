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
package com.milaboratory.core.sequencing.io.fastq;

/**
 * An interface for read metadata in Illumina format
 */
public interface ReadInfo {
    /**
     * Order of the read in paired data
     *
     * @return {@code 0} for first read, {@code 1} for second read
     */
    byte getReadNumber();

    /**
     * Checks if this read is a mate of another one
     *
     * @param other other read to compare
     * @return {@code true} if reads are paired, {@code false} otherwise
     */
    boolean isPairOf(ReadInfo other);

    /**
     * Checks if read is filtered (i.e. should be omitted)
     *
     * @return {@code true} if read is filtered, {@code false} otherwise
     */
    boolean isFiltered();

    /**
     * Gets the description for read
     *
     * @return the description for read
     */
    String pDescription();
}
