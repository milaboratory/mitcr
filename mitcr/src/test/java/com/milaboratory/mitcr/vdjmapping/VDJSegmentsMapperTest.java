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

import org.junit.Ignore;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
// TODO: several tests
@Ignore
public class VDJSegmentsMapperTest extends AbstractMapperTest {
    /* private static CDR3ExtractorParameters parameters;
    private static CoreCDR3Extractor extractor;
    private static final String paramsFileName = "testParams.xml";

    @BeforeClass
    public static void loadParameters() {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(paramsFileName);
            Element element = document.getRootElement().getChild("parameters").getChild("mapperParameters");
            parameters = new CDR3ExtractorParameters(element);
        } catch (JDOMException ex) {
            Logger.getLogger("xml").log(Level.WARNING, null, ex);
        } catch (IOException e) {
        }
        extractor = new CoreCDR3Extractor(-3, 0); //As for J-PHE extender
    }

    @Test
    public void goodTest() {
        VDJSegmentsMapper simpleMapper = VDJSegmentsMapperFactory.createVDJMapper(library, new NTreeNodeGeneratorBadMismatch(), parameters);
        VDJSegmentsMappingResult resultGood = simpleMapper.map(sequenceGood);
        SequenceWrapper goodCDR3 = extractor.extract(resultGood);
        String goodCDR3assertion = "TGTGCCAGCACCGTGGACAGTCTGGACACTGAAGCTTTC";
        assertEquals(goodCDR3.toString(), goodCDR3assertion);
    }

    @Test
    public void mmTest() {
        VDJSegmentsMapper simpleMapper = VDJSegmentsMapperFactory.createVDJMapper(library, new NTreeNodeGeneratorBadMismatch(), parameters);
        VDJSegmentsMappingResult result = simpleMapper.map(sequence1VMMMarked);
        SequenceWrapper CDR3 = extractor.extract(result);
        String CDR3assertion = "TGTGCCgGCACCGTGGACAGTCTGGACACTGAAGCTTTC";
        assertEquals(CDR3.toString(), CDR3assertion);
    }

    @Test
    public void insertionTest() {
        VDJSegmentsMapper simpleMapper = VDJSegmentsMapperFactory.createVDJMapper(library, new NTreeNodeGeneratorBadDeletion(), parameters);
        VDJSegmentsMappingResult result = simpleMapper.map(sequence1VInsertionMarked);
        SequenceWrapper CDR3 = extractor.extract(result);
        String CDR3assertion = "TGTGCCaAGCACCGTGGACAGTCTGGACACTGAAGCTTTC";
        assertEquals(CDR3.toString(), CDR3assertion);
    }*/
}
