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

import com.milaboratory.mitcr.vdjmapping.AlignmentDirection;
import com.milaboratory.mitcr.vdjmapping.DSegmentMapperParameters;
import com.milaboratory.mitcr.vdjmapping.VJSegmentMapperParameters;
import org.jdom.Element;
import org.junit.Assert;
import org.junit.Test;

public class CDR3ExtractorParametersTest {
    @Test
    public void testXML() throws Exception {
        CDR3ExtractorParameters params = new CDR3ExtractorParameters(new VJSegmentMapperParameters(AlignmentDirection.Both, -4, 1, 12, 3),
                new VJSegmentMapperParameters(AlignmentDirection.InsideCDR3, -3, 2, 7, -2),
                //new VJSegmentMapperParameters(-1, 4, 12, -2, AlignmentDirection.TrivialAlignment),
                new DSegmentMapperParameters(6), Strand.Both, true);
        Assert.assertEquals(params, CDR3ExtractorParameters.fromXML(params.toXML(new Element("cdr3Extractor"))));
    }
}
