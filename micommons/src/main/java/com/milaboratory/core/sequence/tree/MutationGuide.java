package com.milaboratory.core.sequence.tree;

import com.milaboratory.core.sequence.Sequence;

public interface MutationGuide<T extends Sequence> {
    boolean allowMutation(T reference, int position, byte type, byte to);
}
