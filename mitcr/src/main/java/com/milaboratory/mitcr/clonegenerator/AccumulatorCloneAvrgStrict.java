package com.milaboratory.mitcr.clonegenerator;

import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult;
import com.milaboratory.util.BitArray;

public final class AccumulatorCloneAvrgStrict extends AccumulatorCloneAvrg {
    public static final AccumulatorCloneFactory FACTORY = new AccumulatorCloneFactory() {
        @Override
        public AccumulatorClone create(int id, NucleotideSQPair cdr3, boolean saveLinks) {
            return new AccumulatorCloneAvrgStrict(id, cdr3, saveLinks);
        }
    };

    private BarcodeAggregator[] bcAggregators = new BarcodeAggregator[3];

    AccumulatorCloneAvrgStrict(int id, NucleotideSQPair cdr3, boolean saveLinks) {
        super(id, cdr3, saveLinks);
    }

    @Override
    synchronized void include(CDR3ExtractionResult result, boolean additional) {
        super.include(result, additional);

        BitArray barcode;
        for (int i = 0; i < 3; ++i) {
            barcode = result.getBarcode(i);
            if (barcode != null) {
                if (bcAggregators[i] == null)
                    bcAggregators[i] = new BarcodeAggregator(barcode.size());
                bcAggregators[i].addBarcode(barcode);
            }
        }
    }

    @Override
    void compile(float barcodeAggregationFactor) {
        //Needed to compile quality values
        super.compile(barcodeAggregationFactor);

        for (int i = 0; i < 3; ++i)
            if (bcAggregators[i] == null)
                barcodes[i] = null;
            else
                barcodes[i] = bcAggregators[i].calculateBarcode(barcodeAggregationFactor);

        //Freeing memory
        this.bcAggregators = null;
    }
}
