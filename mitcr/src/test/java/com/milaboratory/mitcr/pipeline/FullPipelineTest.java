package com.milaboratory.mitcr.pipeline;

import com.milaboratory.core.clone.CloneSet;
import com.milaboratory.core.io.CloneSetIO;
import com.milaboratory.core.sequencing.io.fastq.SFastqReader;
import com.milaboratory.core.util.CloneSetsComparator;
import com.milaboratory.util.CompressionType;
import org.junit.Assert;
import org.junit.Test;

public class FullPipelineTest {
    private CloneSet loadCloneSet(String fileName) throws Exception {
        return CloneSetIO.importCloneSet(
                this.getClass().getClassLoader().
                        getResourceAsStream(fileName), CompressionType.GZIP);
    }

    private void test(Parameters params, String referenceName) throws Exception {
        FullPipeline pipeline = new FullPipeline(new SFastqReader(this.getClass().getClassLoader().
                getResourceAsStream("good_ds_test.fastq.gz"), CompressionType.GZIP), params);
        pipeline.run();
        CloneSet csResult = pipeline.getResult();
        Assert.assertEquals(0.0, CloneSetsComparator.compare(csResult, loadCloneSet(referenceName)).difference, 0.005);
    }

    @Test
    public void testFlex1() throws Exception {
        test(ParameterPresets.getFlex(), "o_good_flex1.txt.gz");
    }

    @Test
    public void testJPrimer1() throws Exception {
        test(ParameterPresets.getJPrimer(), "o_good_jprimer1.txt.gz");
    }
}
