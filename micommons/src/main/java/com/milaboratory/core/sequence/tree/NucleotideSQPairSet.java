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

package com.milaboratory.core.sequence.tree;

import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.core.sequence.quality.SequenceQualityPhred;
import com.milaboratory.core.sequence.util.SequencesUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class NucleotideSQPairSet implements Iterable<NucleotideSQPair> {
    //For debugging purpose
    private static final AtomicLong counter = new AtomicLong(0);
    private final long id = counter.getAndIncrement();
    private NucleotideSQPairSetListener listener = null;

    //TODO add read-write locking here to make this class thread-safe
    private final Penalty penalty;
    private int count = 0, filtered = 0, total = 0;
    //TODO idea: store {byte[] qual, int counter} to count aggregated sequences
    //TODO and not to remove good sequence if better sequenced PCR-error variant was passed to add() method
    private final float fFactor;
    private final Map<NucleotideSequence, byte[]> map = new HashMap<>();

    /**
     * Main constructor.
     *
     * @param penalty penalty model to be used in this set
     * @param fFactor minimal potential threshold (in penalty.threshold units) ( :)) see code)
     */
    public NucleotideSQPairSet(Penalty penalty, float fFactor) {
        this.penalty = penalty;
        this.fFactor = fFactor;
    }

    /**
     * Main method to aggregate sequences.
     *
     * @param pair could be null
     */
    public void add(NucleotideSQPair pair) {
        if (pair == null)
            throw new NullPointerException("Null sequence was added to the set.");

        total++;

        float penaltyThreshold = penalty.threshold(pair.size());

        //For compatibility
        //if (pair == null)
        //    return;

        //Pre-filtering
        float t = fFactor * penaltyThreshold;

        for (int i = 0; i < pair.size() && t >= 0.0f; ++i)
            t -= penalty.penalty(40, pair.getQuality().value(i));

        if (t >= 0.0f) {
            //Debug
            if (listener != null)
                listener.event(this, NucleotideSQPairSetEventType.FilteredOut, pair, null);
            return;
        }

        filtered++;

        //~ type cast. Only NucleotideSequenceImpl has right implementation for hashCode and equals
        //final NucleotideSequence sequence = NucleotideSequence.fromSequence(pair.getSequence());
        final NucleotideSequence sequence = pair.getSequence();

        //~~~~~~~~~~~~~ Search for exact hit ~~~~~~~~~~~~~
        byte[] qualities = map.get(sequence);

        if (qualities != null) { //Exact hit found!
            //Debug
            if (listener != null)
                listener.event(this, NucleotideSQPairSetEventType.ExactMatchFound, pair, new NucleotideSQPair(sequence, new SequenceQualityPhred(qualities)));

            byte qual;
            //Updating quality values from new sequence
            //Setting "max" quality.
            for (int i = 0; i < qualities.length; ++i)
                if ((qual = pair.getQuality().value(i)) > qualities[i])
                    qualities[i] = qual;
            //This sequence is already in the map, so, return. (No count++ is needed)
            return;
        }

        //~~~~~~~~~~~~~     Penalty search    ~~~~~~~~~~~~~
        float p;
        boolean found = false;

        //Iterating through whole map
        Iterator<Map.Entry<NucleotideSequence, byte[]>> it = map.entrySet().iterator(); //Iterator here is used to be abel to remove entries
        Map.Entry<NucleotideSequence, byte[]> e;
        while (it.hasNext()) {
            //Getting entry
            e = it.next();

            //Extracting quality and sequence
            final byte[] qualsInMap = e.getValue();
            final NucleotideSequence sequenceInMap = e.getKey();

            //Just in case...
            if (qualsInMap.length != sequence.size())
                continue;

            //Calculating penalty
            p = 0.0f;
            for (int i = 0; i < qualsInMap.length; ++i)
                if (sequence.codeAt(i) != sequenceInMap.codeAt(i)) {
                    p += penalty.penalty(qualsInMap[i], pair.getQuality().value(i));
                    if (p > penaltyThreshold) //Not a hit, break.
                        break;
                }

            if (p <= penaltyThreshold) { //Hit found!
                found = true;

                //Who is the best?
                int sum = 0;
                for (int i = 0; i < pair.size(); ++i) {
                    sum += pair.getQuality().value(i);
                    sum -= e.getValue()[i];
                }

                //Debug
                if (listener != null)
                    listener.event(this,
                            sum <= 0 ? NucleotideSQPairSetEventType.MatchFoundAndPairDropped :
                                    NucleotideSQPairSetEventType.MatchFoundAndReplaced,
                            pair, new NucleotideSQPair(sequenceInMap, new SequenceQualityPhred(qualsInMap)));

                if (sum <= 0) // e is the best
                    pair = null; //will not be putted to map
                else // pair is the best
                    it.remove(); //removing e, pair will be putted to the map

                break;
            }
        }

        //Adding this read if needed (hit was found, but removed due to lower quality OR no hits was found)
        if (pair != null)
            map.put(sequence, SequencesUtils.extractRawQualityValues(pair.getQuality()));

        //Debug
        if (listener != null && !found)
            listener.event(this,
                    NucleotideSQPairSetEventType.NewRecordCreated,
                    pair, null);

        //If no hits was found add one to the diversity count
        if (!found)
            count++;
    }

    public Set<NucleotideSequence> getVariants() {
        return ((Set<NucleotideSequence>) ((Object) map.keySet()));
    }

    public int getDiversityEstimate() {
        return count;
    }

    public int getFiltered() {
        return filtered;
    }

    public int getTotal() {
        return total;
    }

    public Penalty getPenalty() {
        return penalty;
    }

    public void setListener(NucleotideSQPairSetListener listener) {
        this.listener = listener;
    }

    @Override
    public Iterator<NucleotideSQPair> iterator() {
        return new It(map.entrySet().iterator());
    }

    /**
     * Each set has it's unique id. For debug purposes.
     *
     * @return unique set id
     */
    public long getId() {
        return id;
    }

    /**
     * Equivalent to:<br/><b><code>new NucleotideSQPairSet(LinearMinPenalty.INSTANCE_V1)</code></b>
     */
    public static NucleotideSQPairSet createV1() {
        return new NucleotideSQPairSet(LinearMinPenalty.INSTANCE_V1, 4.0f);
    }

    /**
     * Equivalent to:<br/><b><code>new NucleotideSQPairSet(LinearMinPenalty.INSTANCE_V2)</code></b>
     */
    public static NucleotideSQPairSet createV2() {
        return new NucleotideSQPairSet(LinearMinPenalty.INSTANCE_V2, 4.0f);
    }

    private static class It implements Iterator<NucleotideSQPair> {
        private final Iterator<Map.Entry<NucleotideSequence, byte[]>> innerIt;

        private It(Iterator<Map.Entry<NucleotideSequence, byte[]>> innerIt) {
            this.innerIt = innerIt;
        }

        @Override
        public boolean hasNext() {
            return innerIt.hasNext();
        }

        @Override
        public NucleotideSQPair next() {
            Map.Entry<NucleotideSequence, byte[]> n = innerIt.next();
            return new NucleotideSQPair(n.getKey(), new SequenceQualityPhred(n.getValue()));
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
