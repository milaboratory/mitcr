/*
 * MiTCR <http://milaboratory.com>
 *
 * Copyright (c) 2010-2013:
 *     Bolotin Dmitriy     <bolotin.dmitriy@gmail.com>
 *     Chudakov Dmitriy    <chudakovdm@mail.ru>
 *
 * MiTCR is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.milaboratory.mitcr.clonegenerator;

import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult;
import com.milaboratory.util.BitArray;

/**
 * Object to collect information about {@link com.milaboratory.core.clone.Clone}. This class is thread-safe. Used
 * internally
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
final class AccumulatorCloneMaxStrict extends AccumulatorCloneMax {
    public static final AccumulatorCloneFactory FACTORY = new AccumulatorCloneFactory() {
        @Override
        public AccumulatorClone create(int id, NucleotideSQPair cdr3, boolean saveLinks) {
            return new AccumulatorCloneMaxStrict(id, cdr3, saveLinks);
        }
    };

    private BarcodeAggregator[] bcAggregators = new BarcodeAggregator[3];

    AccumulatorCloneMaxStrict(int id, NucleotideSQPair cdr3, boolean saveLinks) {
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
