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

import cc.redberry.pipe.CUtils;
import cc.redberry.pipe.OutputPort;
import com.milaboratory.core.clone.Clone;
import com.milaboratory.core.segment.DefaultSegmentLibrary;
import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.Species;
import com.milaboratory.core.sequencing.io.SSequencingDataReader;
import com.milaboratory.core.sequencing.read.SSequencingRead;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResultUtils;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractorFromSRead;
import com.milaboratory.mitcr.qualitystrategy.IlluminaQualityInterpretationStrategy;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

import static com.milaboratory.mitcr.MiTCRTestUtils.getFlexCDR3ExtractionParameters;
import static com.milaboratory.mitcr.MiTCRTestUtils.getReaderFromString;
import static com.milaboratory.mitcr.clonegenerator.LQMappingCloneGeneratorTest.centralCloneRead1;
import static com.milaboratory.mitcr.clonegenerator.LQMappingCloneGeneratorTest.centralCloneRead2;
import static org.junit.Assert.assertTrue;

public class CDR3ExtractionResultSerializationTest {
    @Test
    public void testSerialization() throws Exception {
        //Some reads
        String reads = centralCloneRead1 + centralCloneRead2 + centralCloneRead2 + centralCloneRead1 +
                centralCloneRead2 + centralCloneRead2;

        SSequencingDataReader reader = getReaderFromString(reads);

        CDR3ExtractorFromSRead extractor = new CDR3ExtractorFromSRead(Species.HomoSapiens, Gene.TRB,
                getFlexCDR3ExtractionParameters(), DefaultSegmentLibrary.load(),
                new IlluminaQualityInterpretationStrategy((byte) 25));

        OutputPort<CDR3ExtractionResult<SSequencingRead>> results = CUtils.wrap(reader, extractor);

        CDR3ExtractionResult result;

        LQMappingCloneGenerator generator = new LQMappingCloneGenerator(AccumulatorCloneMaxStrict.FACTORY,
                0.7f, false, new IlluminaQualityInterpretationStrategy((byte) 25), 3, true);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);

        int count = 0;
        CDR3ExtractionResult first = null;

        while ((result = results.take()) != null) {
            result = CDR3ExtractionResultUtils.makeSerializable(result);

            if (first == null)
                first = result;
            else {
                oos.writeBoolean(true);
                oos.writeObject(result);
            }

            generator.put(result);
        }

        generator.put(null);
        oos.writeBoolean(false);
        oos.close();

        byte[] data = os.toByteArray();

        HashSet<Clone> clones = new HashSet<>();
        for (Clone c : generator.getCloneSet().getClones())
            clones.add(c);

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));

        generator = new LQMappingCloneGenerator(AccumulatorCloneMaxStrict.FACTORY,
                0.7f, false, new IlluminaQualityInterpretationStrategy((byte) 25), 3, true);

        generator.put(first);

        while (ois.readBoolean()) {
            result = (CDR3ExtractionResult) ois.readObject();

            generator.put(result);
        }

        ois.close();

        generator.put(null);

        for (Clone c : generator.getCloneSet().getClones())
            assertTrue(clones.remove(c));

        assertTrue(clones.isEmpty());
    }
}
