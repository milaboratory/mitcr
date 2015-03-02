package com.milaboratory.mitcr.vdjmapping;

import org.jdom.Element;
import org.junit.Test;

import static org.junit.Assert.*;

public class DSegmentMapperParametersTest {
    @Test
    public void testGeneral() throws Exception {
        DSegmentMapperParameters[] params = {new DSegmentMapperParameters(10, true),
                new DSegmentMapperParameters(9, false),
                new DSegmentMapperParameters(10, false)};


        for (DSegmentMapperParameters param1 : params) {
            assertEquals(param1, DSegmentMapperParameters.fromXML(param1.asXML(new Element("d"))));
            for (DSegmentMapperParameters param2 : params)
                if (param1 != param2)
                    assertFalse(param1.equals(param2));
                else
                    assertTrue(param1.equals(param2));
        }
    }
}
