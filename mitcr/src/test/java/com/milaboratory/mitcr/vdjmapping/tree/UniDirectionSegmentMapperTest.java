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

/*
 */
package com.milaboratory.mitcr.vdjmapping.tree;

import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.core.sequencing.read.SSequencingRead;
import com.milaboratory.mitcr.qualitystrategy.IlluminaQualityInterpretationStrategy;
import com.milaboratory.mitcr.vdjmapping.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class UniDirectionSegmentMapperTest extends AbstractSingleMapperTest {
    @Test
    public void uniDirectionPosFromTo() {
        VJSegmentMapperParameters params = new VJSegmentMapperParameters(AlignmentDirection.Both, -6, 3, 12, 2);
        VJSegmentMapper<SSequencingRead> mapper = VJSegmentMapperFactory.createMapperForSReads(container, params, new IlluminaQualityInterpretationStrategy((byte) 20));
        VJSegmentMappingResult result = mapper.map(reads[3]);
        String assertSequence = "TTCTACATCTGCAGT";
        NucleotideSequence c = reads[3].getData().getSequence().getRange(result.getRefPoint() - 12, result.getRefPoint() + 3);
        Assert.assertEquals(assertSequence, c.toString());
    }

    @Test
    public void uniDirectionPosLen() {
        VJSegmentMapperParameters params = new VJSegmentMapperParameters(AlignmentDirection.Both, -6, 3, 38, 2);
        VJSegmentMapper<SSequencingRead> mapper = VJSegmentMapperFactory.createMapperForSReads(container, params, new IlluminaQualityInterpretationStrategy((byte) 20));
        VJSegmentMappingResult result = mapper.map(reads[3]);
        String assertSequence = "GTGCCCATCCTGAAGACAGCAGCTTCTACATCTGCAGT";
        NucleotideSequence c = reads[3].getData().getSequence().getRange(result.getRefPoint() - 35, result.getRefPoint() + 3);
        Assert.assertEquals(assertSequence, c.toString());
    }

    @Test
    public void uniDirectionNegFromTo() {
        VJSegmentMapperParameters params = new VJSegmentMapperParameters(AlignmentDirection.Both, -6, 4, 12, 2);
        VJSegmentMapper<SSequencingRead> mapper = VJSegmentMapperFactory.createMapperForSReads(container, params, new IlluminaQualityInterpretationStrategy((byte) 20));
        VJSegmentMappingResult result = mapper.map(reads[3]);
        Assert.assertEquals(null, result);
    }

    @Test
    public void uniDirectionNegFromLen() {
        VJSegmentMapperParameters params = new VJSegmentMapperParameters(AlignmentDirection.Both, -6, 4, 39, 2);
        VJSegmentMapper<SSequencingRead> mapper = VJSegmentMapperFactory.createMapperForSReads(container, params, new IlluminaQualityInterpretationStrategy((byte) 20));
        VJSegmentMappingResult result = mapper.map(reads[3]);
        Assert.assertEquals(null, result);
    }
}
