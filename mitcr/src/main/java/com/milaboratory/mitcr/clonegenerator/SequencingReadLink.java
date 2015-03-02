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
package com.milaboratory.mitcr.clonegenerator;

/**
 * A backward link to reads used to assemble a given CDR3 sequence
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class SequencingReadLink {
    private final int from, to;
    private final long id;
    private final boolean additional, foundInReverseComplement;

    /**
     * Creates a backward link to reads used to assemble a given CDR3 sequence
     *
     * @param from                     CDR3 start in read coordinates (inclusive) TODO: dima check
     * @param to                       CDR3 end in read coordinates (inclusive)
     * @param id                       id of the read
     * @param additional               re-assigned to this CDR3 (e.g. via low-quality re-assignment)
     * @param foundInReverseComplement true if CDR3 found in reverse complement
     */
    public SequencingReadLink(int from, int to, long id,
                              boolean additional, boolean foundInReverseComplement) {
        this.from = from;
        this.to = to;
        this.id = id;
        this.additional = additional;
        this.foundInReverseComplement = foundInReverseComplement;
    }

    /**
     * Tells if read was re-assigned to this CDR3 (e.g. via low-quality re-assignment)
     *
     * @return {@code true} if read was re-assigned to this CDR3 (e.g. via low-quality re-assignment)
     */
    public boolean isAdditional() {
        return additional;
    }

    /**
     * Gets CDR3 start in read coordinates (inclusive)
     *
     * @return CDR3 start in read coordinates (inclusive)
     */
    public int getCDR3StartPosition() {
        return from;
    }

    /**
     * Gets the ID of read
     *
     * @return read ID
     */
    public long getId() {
        return id;
    }

    /**
     * Gets CDR3 end in read coordinates (inclusive)
     *
     * @return CDR3 end in read coordinates (inclusive)
     */
    public int getCDR3EndPosition() {
        return to;
    }

    /**
     * Tells if CDR3 found in reverse complement
     *
     * @return {@code true} if CDR3 is found in reverse complement
     */
    public boolean isFoundInReverseComplement() {
        return foundInReverseComplement;
    }
}
