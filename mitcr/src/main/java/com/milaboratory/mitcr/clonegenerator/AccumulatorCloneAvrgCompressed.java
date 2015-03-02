package com.milaboratory.mitcr.clonegenerator;

import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult;
import com.milaboratory.util.BitArray;

import static com.milaboratory.mitcr.clonegenerator.CountVectorUtils.*;

public class AccumulatorCloneAvrgCompressed extends AccumulatorCloneAvrg {
    public static final AccumulatorCloneFactory FACTORY = new AccumulatorCloneFactory() {
        @Override
        public AccumulatorClone create(int id, NucleotideSQPair cdr3, boolean saveLinks) {
            return new AccumulatorCloneAvrgCompressed(id, cdr3, saveLinks);
        }
    };

    private byte[][] barcodeAccumulators = new byte[3][];

    AccumulatorCloneAvrgCompressed(int id, NucleotideSQPair cdr3, boolean saveLinks) {
        super(id, cdr3, saveLinks);
    }

    @Override
    synchronized void include(CDR3ExtractionResult result, boolean additional) {
        super.include(result, additional);

        BitArray barcode;
        for (int i = 0; i < 3; ++i) {
            barcode = result.getBarcode(i);
            if (barcode != null) {
                if (barcodeAccumulators[i] == null)
                    barcodeAccumulators[i] = new byte[barcode.size() + DETERMINISTIC_ACCUMULATOR_HEADER_SIZE];
                addOneDeterministic(barcodeAccumulators[i], barcode);
            }
        }
    }

    @Override
    void compile(float barcodeAggregationFactor) {
        //Needed to compile quality values
        super.compile(barcodeAggregationFactor);

        for (int i = 0; i < 3; ++i)
            if (barcodeAccumulators[i] == null)
                barcodes[i] = null;
            else
                barcodes[i] = new BarcodeAggregator(getResultDeterministic(barcodeAccumulators[i])).calculateBarcode(barcodeAggregationFactor);

        //Freeing memory
        this.barcodeAccumulators = null;
    }

}
