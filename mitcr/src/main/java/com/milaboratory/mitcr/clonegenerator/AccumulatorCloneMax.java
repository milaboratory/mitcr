package com.milaboratory.mitcr.clonegenerator;

import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequence.quality.SequenceQualityPhred;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult;

public abstract class AccumulatorCloneMax extends AccumulatorClone {
    final byte[] quality;

    public AccumulatorCloneMax(int id, NucleotideSQPair cdr3, boolean saveLinks) {
        super(id, cdr3, saveLinks);
        quality = new byte[cdr3.size()];
    }

    @Override
    synchronized void include(CDR3ExtractionResult result, boolean additional) {
        super.include(result, additional);

        byte val;
        for (int i = 0; i < quality.length; ++i)
            if (quality[i] < (val = result.getCDR3().getQuality().value(i)))
                quality[i] = val;

        //cdr3.getQuality().mergeWith(result.getCDR3().getQuality());
    }

    @Override
    void compile(float barcodeAggregationFactor) {
        cdr3 = new NucleotideSQPair(sequence, new SequenceQualityPhred(quality));
    }
}
