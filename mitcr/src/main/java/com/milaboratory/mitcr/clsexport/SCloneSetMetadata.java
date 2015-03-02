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
package com.milaboratory.mitcr.clsexport;

import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.Species;

import java.util.Map;
import java.util.UUID;

public class SCloneSetMetadata extends AbstractCloneSetMetadata {
    private final Species species;
    private final Gene gene;
    private byte[][] segmentGroupsMD5;

    /**
     * Constructor for use before writing
     */
    public SCloneSetMetadata(Map<String, Object> properties, DataSetMetadata dataSetMetadata, UUID uuid, int clonesOffset, int clonesCount, int clustersOffset, int clustersCount,
                             Species species, Gene gene, byte[][] segmentGroupsMD5) {
        this(properties, dataSetMetadata, uuid, clonesOffset, clonesCount, clustersOffset, clustersCount, species, gene, segmentGroupsMD5, 1);
    }

    /**
     * Private constructor. ))
     */
    private SCloneSetMetadata(Map<String, Object> properties, DataSetMetadata dataSetMetadata, UUID uuid, int clonesOffset, int clonesCount, int clustersOffset, int clustersCount,
                              Species species, Gene gene, byte[][] segmentGroupsMD5,
                              int format) {
        super(properties, dataSetMetadata, uuid, clonesOffset, clonesCount, clustersOffset, clustersCount, format);
        this.species = species;
        this.gene = gene;
        this.segmentGroupsMD5 = segmentGroupsMD5;
    }

    /**
     * Constructor to use in reading from file.
     */
    public SCloneSetMetadata(AbstractCloneSetMetadata metadata,
                             Species species, Gene gene, byte[][] segmentGroupsMD5,
                             int format) {
        super(metadata, format);
        this.species = species;
        this.gene = gene;
        this.segmentGroupsMD5 = segmentGroupsMD5;
    }

    public Gene getGene() {
        return gene;
    }

    public Species getSpecies() {
        return species;
    }

    public byte[][] getSegmentGroupsMD5() {
        return segmentGroupsMD5;
    }
}
