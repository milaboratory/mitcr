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
 * An implementation of {@link ReadInfo}
 */
public class ReadInfoImpl implements ReadInfo {
    private final String pairId;
    private final byte readNumber;
    private final boolean isFiltered;

    /**
     * Creates a container holding read metadata
     *
     * @param pairId     id of read pair
     * @param readNumber number of the read
     * @param filtered   is the read filtered
     */
    public ReadInfoImpl(String pairId, byte readNumber, boolean filtered) {
        this.pairId = pairId;
        this.readNumber = readNumber;
        this.isFiltered = filtered;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public byte getReadNumber() {
        return readNumber;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public boolean isPairOf(ReadInfo other) {
        if (other.getClass() != ReadInfoImpl.class)
            return false;
        ReadInfoImpl impl = (ReadInfoImpl) other;
        return impl.pairId.equals(this.pairId) && (1 - this.readNumber) == (impl.readNumber);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public boolean isFiltered() {
        return isFiltered;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public String pDescription() {
        return pairId;
    }
}
