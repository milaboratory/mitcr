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
package com.milaboratory.mitcr.vdjmapping.tree;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class OneDirectionSegmentMapperTest extends AbstractSingleMapperTest {
    //TODO new test are needed
    /*@Test
    public void oneDirectionPos() {
        VJSegmentMapperParameters params = new VJSegmentMapperParameters(-3, 12, 2, AlignmentDirection.InsideCDR3);
        VJSegmentMapper<SSequencingRead> mapper = VJSegmentMapperFactory.createMapperForSReads(container, params, new IlluminaQualityInterpretationStrategy((byte) 20));
        VJSegmentMappingResult result = mapper.map(reads[0]);
        String assertSequence = "TGTGCCAGCAGC";
        NucleotideSequence c = new NucleotideSubSequence(reads[0].getData().getSequence(), result.getRefPoint() - 3, 12);
        Assert.assertEquals(assertSequence, c.toString());
    }

    @Test
    public void oneDirectionTwoMMsBadQ() {
        VJSegmentMapperParameters params = new VJSegmentMapperParameters(-3, 12, 2, AlignmentDirection.InsideCDR3);
        VJSegmentMapper<SSequencingRead> mapper = VJSegmentMapperFactory.createMapperForSReads(container, params, new IlluminaQualityInterpretationStrategy((byte) 20));
        String assertSequence = "TGTGCTAGCAGT";
        VJSegmentMappingResult result = mapper.map(reads[1]);
        NucleotideSequence c = new NucleotideSubSequence(reads[1].getData().getSequence(), result.getRefPoint() - 3, 12);
        Assert.assertEquals(assertSequence, c.toString());
    }

    @Test
    public void oneDirectionReversePos() {
        VJSegmentMapperParameters params = new VJSegmentMapperParameters(-3, 12, 2, AlignmentDirection.OutsideCDR3);
        VJSegmentMapper<SSequencingRead> mapper = VJSegmentMapperFactory.createMapperForSReads(container, params, new IlluminaQualityInterpretationStrategy((byte) 20));
        VJSegmentMappingResult result = mapper.map(reads[2]);
        String assertSequence = "GTGTATCTCTGT";
        NucleotideSequence c = new NucleotideSubSequence(reads[2].getData().getSequence(), result.getRefPoint() - 12, 12);
        Assert.assertEquals(assertSequence, c.toString());
    }

    @Test
    public void oneDirectionTooShort() {
        VJSegmentMapperParameters params = new VJSegmentMapperParameters(-3, 14, 2, AlignmentDirection.InsideCDR3);
        VJSegmentMapper<SSequencingRead> mapper = VJSegmentMapperFactory.createMapperForSReads(container, params, new IlluminaQualityInterpretationStrategy((byte) 20));
        VJSegmentMappingResult result = mapper.map(reads[0]);
        Assert.assertEquals(null, result);

    }

    @Test
    public void oneDirectionMMWithGoodQ() {
        VJSegmentMapperParameters params = new VJSegmentMapperParameters(-3, 12, 2, AlignmentDirection.InsideCDR3);
        VJSegmentMapper<SSequencingRead> mapper = VJSegmentMapperFactory.createMapperForSReads(container, params, new IlluminaQualityInterpretationStrategy((byte) 20));
        VJSegmentMappingResult result = mapper.map(reads[2]);
        Assert.assertEquals(null, result);

    }*/

    /*@Test
    public void goodSequenceMapping() {
        OneDirectionSegmentMapper mapper =
                new OneDirectionSegmentMapper(new NTreeNodeGeneratorBadMismatch(),
                        container, -3, +1, 6, 1,
                        SearchDirection.BeginToEnd);
        VJSegmentMappingResult result = mapper.map(sequenceGood);
        String assertSequence = "tgtgccagcaccgtggac";
        NucleotideSequence c = new NucleotideSubSequence(sequenceGood.getData().getSequence(), result.getRefPoint() - 3, 18);
        assertEquals(c.toString(), assertSequence);
    }

    @Test
    public void badMMSequenceMapping() {
        OneDirectionSegmentMapper mapper =
                new OneDirectionSegmentMapper(new NTreeNodeGeneratorBadMismatch(),
                        container, -3, +1, 6, 1,
                        SearchDirection.BeginToEnd);
        VJSegmentMappingResult resultMarked = mapper.map(sequence1VMMMarked);
        VJSegmentMappingResult resultUnMarked = mapper.map(sequence1VMM);
        assertTrue(resultMarked.getScore() > resultUnMarked.getScore());
        assertTrue(resultMarked.getRefPoint() == resultUnMarked.getRefPoint());
        String assertSequence = "tgtgccggcaccgtggac";
        NucleotideSequence c = new NucleotideSubSequence(sequence1VMMMarked.getData().getSequence(), resultMarked.getRefPoint() - 3, 18);
        assertEquals(c.toString(), assertSequence);
    }

    @Test
    public void badMMVsGoodSequenceMapping() {
        OneDirectionSegmentMapper mapper =
                new OneDirectionSegmentMapper(new NTreeNodeGeneratorBadMismatch(),
                        container, -3, +1, 6, 1,
                        SearchDirection.BeginToEnd);
        VJSegmentMappingResult resultMarkedMM = mapper.map(sequence1VMMMarked);
        VJSegmentMappingResult resultGood = mapper.map(sequenceGood);
        assertTrue(resultGood.getScore() > resultMarkedMM.getScore());
    }

    @Test
    public void badInsertionVsGoodSequenceMapping() {
        OneDirectionSegmentMapper mapper =
                new OneDirectionSegmentMapper(new NTreeNodeGeneratorBadDeletion(),
                        container, -3, +1, 6, 1,
                        SearchDirection.BeginToEnd);
        VJSegmentMappingResult resultMarkedInsertion = mapper.map(sequence1VInsertionMarked);
        VJSegmentMappingResult resultGood = mapper.map(sequenceGood);
        assertTrue(resultGood.getScore() == resultMarkedInsertion.getScore());
    }

    @Test
    public void badInsetionSequenceMapping() {
        OneDirectionSegmentMapper mapper =
                new OneDirectionSegmentMapper(new NTreeNodeGeneratorBadDeletion(),
                        container, -3, +1, 6, 1,
                        SearchDirection.BeginToEnd);
        VJSegmentMappingResult resultMarked = mapper.map(sequence1VInsertionMarked);
        VJSegmentMappingResult resultUnMarked = mapper.map(sequence1VInsertion);
        assertTrue(resultMarked.getScore() > resultUnMarked.getScore());
        assertTrue(resultMarked.getRefPoint() == resultUnMarked.getRefPoint());
        String assertSequence = "tgtgccaagcaccgtggac";
        NucleotideSequence c = new NucleotideSubSequence(sequence1VInsertionMarked.getData().getSequence(), resultMarked.getRefPoint() - 3, 19);
        assertEquals(c.toString(), assertSequence);
    }*/
}
