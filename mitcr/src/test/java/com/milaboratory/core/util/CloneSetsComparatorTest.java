package com.milaboratory.core.util;

import com.milaboratory.core.clone.CloneSet;
import com.milaboratory.core.io.CloneSetIO;
import com.milaboratory.util.CompressionType;
import junit.framework.Assert;
import org.junit.Test;

public class CloneSetsComparatorTest {
    private CloneSet loadCloneSet(String fileName) throws Exception {
        return CloneSetIO.importCloneSet(
                this.getClass().getClassLoader().
                        getResourceAsStream(fileName), CompressionType.GZIP);
    }

    @Test
    public void test1() throws Exception {
        CloneSet cloneSetFlex1 = loadCloneSet("o_good_flex1.txt.gz"),
                cloneSetFlex2 = loadCloneSet("o_good_flex2.txt.gz"),
                cloneSetJPrimer1 = loadCloneSet("o_good_jprimer1.txt.gz"),
                cloneSetJPrimer2 = loadCloneSet("o_good_jprimer2.txt.gz");

        Assert.assertEquals(0.0, CloneSetsComparator.compare(cloneSetFlex1, cloneSetFlex2).difference, 1E-10);
        Assert.assertEquals(0.0, CloneSetsComparator.compare(cloneSetJPrimer1, cloneSetJPrimer2).difference, 1E-10);

        //Commutativity test
        Assert.assertEquals(CloneSetsComparator.compare(cloneSetJPrimer2, cloneSetFlex1).difference, CloneSetsComparator.compare(cloneSetFlex1, cloneSetJPrimer2).difference, 1E-10);
    }
}
