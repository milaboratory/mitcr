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

import org.jdom.Element;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CloneGeneratorParametersDeserializerTest {
    @Test
    public void testBasic() throws Exception {
        CloneGeneratorParameters params = new BasicCloneGeneratorParameters(.32f);
        assertEquals(params,
                CloneGeneratorParametersDeserializer.fromXML(params.asXML()));
    }

    @Test
    public void testLQMappingP() throws Exception {
        CloneGeneratorParameters params = new LQMappingCloneGeneratorParameters(.3f, 3, true);
        assertEquals(params,
                CloneGeneratorParametersDeserializer.fromXML(params.asXML()));
    }

    @Test
    public void testLQMapping() throws Exception {
        CloneGeneratorParameters params = new LQMappingCloneGeneratorParameters(.3f, 3, false);
        assertEquals(params,
                CloneGeneratorParametersDeserializer.fromXML(params.asXML()));
    }

    @Test
    public void testLQFiltering() throws Exception {
        CloneGeneratorParameters params = new LQFilteringOffCloneGeneratorParameters(0.33f);
        assertEquals(params,
                CloneGeneratorParametersDeserializer.fromXML(params.asXML()));
    }

    @Test
    public void testAccumulatorType() throws Exception {
        CloneGeneratorParameters params = new LQFilteringOffCloneGeneratorParameters(AccumulatorType.AvrgStrict, 0.33f);
        assertEquals(params,
                CloneGeneratorParametersDeserializer.fromXML(params.asXML(new Element("cloneGenerator"))));

        params = new LQFilteringOffCloneGeneratorParameters(AccumulatorType.MaxCompressed, 0.35f);
        assertEquals(params,
                CloneGeneratorParametersDeserializer.fromXML(params.asXML(new Element("cloneGenerator"))));
    }
}
