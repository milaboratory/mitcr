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
package com.milaboratory.mitcr.pipeline;

import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.Species;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractorParameters;
import com.milaboratory.mitcr.cdrextraction.Strand;
import com.milaboratory.mitcr.clonegenerator.LQMappingCloneGeneratorParameters;
import com.milaboratory.mitcr.clusterization.CloneClusterizationType;
import com.milaboratory.mitcr.qualitystrategy.DummyQualityInterpretationStrategy;
import com.milaboratory.mitcr.qualitystrategy.IlluminaQualityInterpretationStrategy;
import com.milaboratory.mitcr.vdjmapping.AlignmentDirection;
import com.milaboratory.mitcr.vdjmapping.DSegmentMapperParameters;
import com.milaboratory.mitcr.vdjmapping.VJSegmentMapperParameters;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;

public class ParametersTest {
    @Test
    public void testXML() throws Exception {
        Parameters parameters = new Parameters(Gene.TRB, Species.HomoSapiens,
                new CDR3ExtractorParameters(new VJSegmentMapperParameters(AlignmentDirection.Both, -4, 1, 12, 3),
                        new VJSegmentMapperParameters(AlignmentDirection.InsideCDR3, -3, 2, 7, -2),
                        //new VJSegmentMapperParameters(-1, 4, 12, -2, AlignmentDirection.TrivialAlignment),
                        new DSegmentMapperParameters(6), Strand.Both, true));
        parameters.setQualityInterpretationStrategy(new IlluminaQualityInterpretationStrategy((byte) 30));
        parameters.setCloneGeneratorParameters(new LQMappingCloneGeneratorParameters(3));
        parameters.setClusterizationType(CloneClusterizationType.OneMismatch, .2f);
        Assert.assertEquals(parameters, Parameters.fromXML(parameters.asXML(new Element("parameters"))));
    }

    @Test
    public void testClone() throws Exception {
        Parameters parameters = new Parameters(Gene.TRB, Species.HomoSapiens,
                new CDR3ExtractorParameters(new VJSegmentMapperParameters(AlignmentDirection.Both, -4, 1, 12, 3),
                        new VJSegmentMapperParameters(AlignmentDirection.Both, -1, 4, 12, -2),
                        new DSegmentMapperParameters(6), Strand.Both, true));
        parameters.setQualityInterpretationStrategy(new DummyQualityInterpretationStrategy());
        parameters.setCloneGeneratorParameters(new LQMappingCloneGeneratorParameters(2));
        parameters.setClusterizationType(CloneClusterizationType.V2D1J2T3Explicit, .52f);
        Assert.assertEquals(parameters, parameters.clone());
    }

    @Test
    public void testPresets() throws Exception {
        Parameters parameters = ParameterPresets.getFlex();
        Assert.assertEquals(parameters, Parameters.fromXML(parameters.asXML(new Element("parameters"))));
        Assert.assertEquals(parameters, Parameters.fromXML(parameters.asXML(new Element("parameters"))).clone());
        Assert.assertEquals(parameters, parameters.clone());
        parameters = ParameterPresets.getJPrimer();
        Assert.assertEquals(parameters, Parameters.fromXML(parameters.asXML(new Element("parameters"))));
        Assert.assertEquals(parameters, Parameters.fromXML(parameters.asXML(new Element("parameters"))).clone());
        Assert.assertEquals(parameters, parameters.clone());
    }

    @Test
    public void testPresetsXSD() throws Exception {
        testParamsXSD(ParameterPresets.getFlex());
        testParamsXSD(ParameterPresets.getJPrimer());
    }

    private void testParamsXSD(Parameters parameters) throws Exception {
        Element paramsXML = parameters.asXML();
        Format format = Format.getPrettyFormat();
        format.setLineSeparator("\n");
        XMLOutputter outputter = new XMLOutputter(format);
        String xmlString = outputter.outputString(paramsXML);
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(new StringReader(xmlString));
        Parameters deserialized = Parameters.fromXML(document.detachRootElement());
        Assert.assertEquals(parameters, deserialized);
    }
}
