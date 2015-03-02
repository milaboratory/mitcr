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
package com.milaboratory.mitcr.vdjmapping;

import com.milaboratory.core.sequence.NucleotideSQPair;

/**
 * A joint mapper for V, J and D(optional)  segments.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class VDJSegmentsMapper {
    private final VJSegmentMapper<NucleotideSQPair>[] mappers = new VJSegmentMapper[2];
    private final DSegmentMapper dMapper;

    /**
     * Creates a joint mapper for V, J and D(optional) segments
     *
     * @param v v segment mapper
     * @param j j segment mapper
     * @param d d segment mapper, could be null if no need to search fo D gene
     */
    public VDJSegmentsMapper(VJSegmentMapper<NucleotideSQPair> v,
                             VJSegmentMapper<NucleotideSQPair> j,
                             DSegmentMapper d) {
        mappers[0] = v;
        mappers[1] = j;
        dMapper = d;
    }

    public VJSegmentMapper<NucleotideSQPair> getVMapper() {
        return mappers[0];
    }

    public VJSegmentMapper<NucleotideSQPair> getJMapper() {
        return mappers[1];
    }

    public VJSegmentMapper<NucleotideSQPair>[] getMappers() {
        return mappers;
    }

    public DSegmentMapper getDMapper() {
        return dMapper;
    }

    /**
     * Marks V, D and J regions on a read. TODO: rename
     *
     * @param wrapper a read to map
     * @return mapping result
     */
    public VDJSegmentsMappingResult map(NucleotideSQPair wrapper) {
        VJSegmentMappingResult[] results = new VJSegmentMappingResult[2];
        results[0] = mappers[0].map(wrapper);
        results[1] = mappers[1].map(wrapper);
        SegmentMappingResult dResult = null;
        if (dMapper != null)
            if (results[0] != null && results[1] != null) {
                int from = results[0].getSegmentBorderTo() + 1;
                int to = results[1].getSegmentBorderFrom() - 1;
                if (from < to) {
                    //search for d gene between v end and j start
                    dResult = dMapper.map(wrapper.getSequence().getRange(from, to + 1));
                    if (dResult != null) {
                        dResult.segmentBorderFrom += from;
                        dResult.segmentBorderTo += from;
                    }
                }
            }
        return new VDJSegmentsMappingResult(results, dResult);
    }
}
