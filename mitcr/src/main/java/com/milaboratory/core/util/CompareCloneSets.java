package com.milaboratory.core.util;

import com.milaboratory.core.clone.CloneSet;
import com.milaboratory.core.io.CloneSetIO;

public class CompareCloneSets {
    public static void main(String[] args) throws Exception {
        CloneSet cs1 = CloneSetIO.importCloneSet(args[1]),
                cs2 = CloneSetIO.importCloneSet(args[2]);
        CloneSetsComparisonResult result = CloneSetsComparator.compare(cs1, cs2);

        switch (args[0]) {
            case "d":
                System.out.println(result.difference);
                break;
            case "v":
                System.out.println("Clones: +" + result.newClones +
                        " | -" + result.missedClones + "");
                System.out.println("Sequences: +" + result.newSequences +
                        " | -" + result.missedSequences);
                System.out.println("Difference = " + result.difference);
        }
    }
}
