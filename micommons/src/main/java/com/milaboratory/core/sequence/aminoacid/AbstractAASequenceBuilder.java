package com.milaboratory.core.sequence.aminoacid;

import com.milaboratory.core.sequence.AbstractSequenceBuilder;
import com.milaboratory.core.sequence.Sequence;

abstract class AbstractAASequenceBuilder<S extends Sequence> extends AbstractSequenceBuilder<S> {
    protected AbstractAASequenceBuilder(int length) {
        super(length);
    }

    @Override
    public void copyFrom(S sequence, int otherOffset, int thisOffset, int length) {
        byte[] otherData = Util.getTryData(sequence);
        if (otherData == null) {
            if (thisOffset < 0 || thisOffset + length > data.length ||
                    otherOffset < 0 || otherOffset + length > sequence.size())
                throw new IndexOutOfBoundsException();

            for (int i = 0; i < length; ++i)
                data[i] = sequence.codeAt(i);
        } else
            System.arraycopy(otherData, otherOffset, data, thisOffset, length);
    }
}
