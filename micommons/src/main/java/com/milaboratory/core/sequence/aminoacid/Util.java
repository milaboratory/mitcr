package com.milaboratory.core.sequence.aminoacid;

import com.milaboratory.core.sequence.Sequence;

class Util {
    static byte[] getTryData(Sequence sequence) {
        if (sequence instanceof CDRAminoAcidSequence)
            return ((CDRAminoAcidSequence) sequence).data;

        if (sequence instanceof AminoAcidSequenceImpl)
            return ((AminoAcidSequenceImpl) sequence).data;

        return null;
    }
}
