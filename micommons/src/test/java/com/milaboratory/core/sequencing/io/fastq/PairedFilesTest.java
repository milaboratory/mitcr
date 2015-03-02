package com.milaboratory.core.sequencing.io.fastq;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;

public class PairedFilesTest {
    @Test
    public void testPatterns1() throws Exception {
        String file = "1829_Chu-NNN-1_NoIndex_L008_R1_001.fastq.gz";
        Assert.assertTrue(PairedFiles.preFilter.matcher(file).find());
        Matcher matcher = PairedFiles.rPatterns[0].matcher(file);
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals("1829_Chu-NNN-1_NoIndex_L008_R1_001.fastq.gz", matcher.group(0));
        Assert.assertEquals("1829_Chu-NNN-1_NoIndex_L008", matcher.group(1));
        Assert.assertEquals("001.fastq.gz", matcher.group(2));
    }

    @Test
    public void testPatterns2() throws Exception {
        String file = "1829_Chu-NNN-1_NoIndex_L008_r1_001.fastq.gz";
        Assert.assertTrue(PairedFiles.preFilter.matcher(file).find());
        Matcher matcher = PairedFiles.rPatterns[0].matcher(file);
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals("1829_Chu-NNN-1_NoIndex_L008_r1_001.fastq.gz", matcher.group(0));
        Assert.assertEquals("1829_Chu-NNN-1_NoIndex_L008", matcher.group(1));
        Assert.assertEquals("001.fastq.gz", matcher.group(2));
    }

    @Test
    public void testPatterns3() throws Exception {
        String file = "yla_R1.fastq.gz";
        Assert.assertTrue(PairedFiles.preFilter.matcher(file).find());
        Matcher matcher = PairedFiles.rPatterns[0].matcher(file);
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals("yla_R1.fastq.gz", matcher.group(0));
        Assert.assertEquals("yla", matcher.group(1));
        Assert.assertEquals("fastq.gz", matcher.group(2));
    }
}
