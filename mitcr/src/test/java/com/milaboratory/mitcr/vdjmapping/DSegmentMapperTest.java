package com.milaboratory.mitcr.vdjmapping;

import com.milaboratory.core.segment.DefaultSegmentLibrary;
import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.SegmentGroupType;
import com.milaboratory.core.segment.Species;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DSegmentMapperTest {
    @Test
    public void testGeneral() throws Exception {
        String seq0str = "TGCAGCGTACCGGGGGGCTCAAATGAAAAACTGTTTTTT";

        NucleotideSequence seq0 = new NucleotideSequence(seq0str);
        NucleotideSequence seq0rc = seq0.getReverseComplement();

        DSegmentMapper mapperT = new DSegmentMapper(DefaultSegmentLibrary.load().getGroup(Species.HomoSapiens, Gene.TRB, SegmentGroupType.Diversity), 5, true);
        DSegmentMapper mapperF = new DSegmentMapper(DefaultSegmentLibrary.load().getGroup(Species.HomoSapiens, Gene.TRB, SegmentGroupType.Diversity), 5, false);

        SegmentMappingResult smrTF = mapperT.map(seq0),
                smrTR = mapperT.map(seq0rc),
                smrFF = mapperF.map(seq0),
                smrFR = mapperF.map(seq0rc);

        assertEquals(null, smrFR);
        assertEquals(smrFF.getScore(), smrTF.getScore(), 0.01);
        assertEquals(smrFF.getSegmentBorderFrom(), smrTF.getSegmentBorderFrom());
        assertEquals(smrFF.getSegmentBorderTo(), smrTF.getSegmentBorderTo());
        assertEquals(seq0str.length() - 1 - smrTR.getSegmentBorderTo(), smrTF.getSegmentBorderFrom());
        assertEquals(seq0str.length() - 1 - smrTR.getSegmentBorderFrom(), smrTF.getSegmentBorderTo());
    }
}
