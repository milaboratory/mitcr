package com.milaboratory.core.util;

import com.milaboratory.core.clone.Clone;
import com.milaboratory.core.clone.CloneSet;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;

import java.util.HashMap;

public class CloneSetsComparator {
    public static final CloneSetsComparisonResult compare(CloneSet csFrom, CloneSet csTo) {
        int mClones = 0, nClones = 0, mSequences = 0, nSequences = 0;
        Clone cFrom;

        //Adding all from clone to has map
        HashMap<NucleotideSequence, Clone> fromClones = new HashMap<>(csFrom.getClones().size());
        for (Clone clone : csFrom)
            fromClones.put(clone.getCDR3().getSequence(), clone);

        for (Clone cTo : csTo) {
            cFrom = fromClones.remove(cTo.getCDR3().getSequence());
            if (cFrom == null) {
                ++nClones;
                nSequences += cTo.getCount();
            }
        }
        mClones = fromClones.size();
        for (Clone clone : fromClones.values())
            mSequences += clone.getCount();

        assert csFrom.getClones().size() + nClones - mClones == csTo.getClones().size();
        assert csFrom.getTotalCount() + nSequences - mSequences == csTo.getTotalCount();

        double difference = 1.0 * (nSequences + mSequences) / (csFrom.getTotalCount() + csTo.getTotalCount());

        return new CloneSetsComparisonResult(nClones, mClones, nSequences, mSequences, difference);
    }
}
