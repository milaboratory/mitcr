package com.milaboratory.mitcr.clonegenerator;

import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequence.quality.SequenceQualityPhred;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult;

import static com.milaboratory.mitcr.clonegenerator.AverageQualityCalculator.convertValues;
import static com.milaboratory.mitcr.clonegenerator.AverageQualityCalculator.nextValues;

public abstract class AccumulatorCloneAvrg extends AccumulatorClone {
    byte[] accumulator;

    public AccumulatorCloneAvrg(int id, NucleotideSQPair cdr3, boolean saveLinks) {
        super(id, cdr3, saveLinks);
        accumulator = new byte[cdr3.size()];
    }

    @Override
    synchronized void include(CDR3ExtractionResult result, boolean additional) {
        super.include(result, additional);

        ////Returns a link to the underlying byte array, so it could be used here for
        ////accumulation of average values
        ////IMPORTANT: because of this quality object of clone is invalid before clone compilation
        //final byte[] avrgStorage = cdr3.getQuality().getInnerData();

        //Adding observed values
        nextValues(accumulator, count - 1,
                result.getCDR3().getQuality().getInnerData());
    }

    @Override
    void compile(float barcodeAggregationFactor) {
        //Now the quality object is valid
        //convertValues(cdr3.getQuality().getInnerData());
        convertValues(accumulator);
        cdr3 = new NucleotideSQPair(sequence, new SequenceQualityPhred(accumulator));
        accumulator = null;
    }
}
