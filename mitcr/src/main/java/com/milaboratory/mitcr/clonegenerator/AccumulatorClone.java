package com.milaboratory.mitcr.clonegenerator;

import com.milaboratory.core.clone.CloneImpl;
import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult;
import com.milaboratory.mitcr.vdjmapping.SegmentMappingResult;

import java.util.ArrayList;

import static com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResultUtils.getSourceId;
import static com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResultUtils.hasSourceId;

public abstract class AccumulatorClone extends CloneImpl {
    long additionalCount = 0;
    final NucleotideSequence sequence;

    AccumulatorClone(int id, NucleotideSQPair cdr3, boolean saveLinks) {
        super(id, null);
        sequence = cdr3.getSequence();
        if (saveLinks)
            links = new ArrayList<>();
        else
            links = null;
    }

    protected void addLink(CDR3ExtractionResult result, boolean additional) {

        //This operation is defined only for Long or SequencingRead sources
        //E.g. will do nothing for CDR3ExtractionResult created for NucleotideSQPair
        if (links != null && hasSourceId(result))

            //Adding link
            links.add(new SequencingReadLink(result.getVMappingResult().getRefPoint() - 3,
                    result.getJMappingResult().getRefPoint() + 3,
                    getSourceId(result), additional, result.isFoundInReverseComplement()));

    }

    final void loadSegmentsCoordsFromMappingResult(CDR3ExtractionResult result) {
        int shift = result.getVMappingResult().getRefPoint() - 3;
        int vTo = result.getVMappingResult().getSegmentBorderTo() - shift;
        int jFrom = result.getJMappingResult().getSegmentBorderFrom() - shift;

        boolean tt = true;

        //TODO optimize
        while (vTo >= jFrom)
            if (tt = !tt) //This is right :)) [Tick-tock]
                --vTo; //Tock 1, 3, 5, ...
            else
                ++jFrom; //Tick 0, 2, 4, ...

        final SegmentMappingResult dResult = result.getDMappingResult();
        //int dFrom = -1, dTo = -1;

        if (dResult != null) {
            segmentCoords = new int[4];
            segmentCoords[0] = vTo;
            segmentCoords[1] = dResult.getSegmentBorderFrom() - shift;
            segmentCoords[2] = dResult.getSegmentBorderTo() - shift;
            segmentCoords[3] = jFrom;
        } else {
            segmentCoords = new int[2];
            segmentCoords[0] = vTo;
            segmentCoords[1] = jFrom;
        }
    }

    public int id() {
        return id;
    }

    void include(CDR3ExtractionResult result, boolean additional) {
        if (segmentCoords == null)
            loadSegmentsCoordsFromMappingResult(result);

        addLink(result, additional);
        ++count;

        if (additional)
            ++additionalCount;
    }

    abstract void compile(float barcodeAggregationFactor);
}
