package com.milaboratory.mitcr.clonegenerator;

import com.milaboratory.core.sequence.NucleotideSQPair;

public interface AccumulatorCloneFactory {
    AccumulatorClone create(int id, NucleotideSQPair cdr3, boolean saveLinks);
}
