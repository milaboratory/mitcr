package com.milaboratory.core.sequence.tree;

import java.util.*;

public class PenaltyUtils {
    /**
     * @param penalty {mismatch, deletion, insertion}
     */
    public static byte[][] getDifferencesCombination(double max,
                                                     final double[] penalty) {
        return getDifferencesCombination(max, penalty, null);
    }

    /**
     * @param penalty {mismatch, deletion, insertion}
     */
    public static byte[][] getDifferencesCombination(double max,
                                                     final double[] penalty,
                                                     final int[] maxErrors) {
        //penalty = penalty.clone();
        //Arrays.sort(penalty);
        double sum;
        final byte[] count = new byte[3];

        List<SequenceWrapper> sequences = new ArrayList<>();
        sequences.add(new SequenceWrapper());

        int from = 0, to = 1, i;
        byte j;
        SequenceWrapper wrapper;

        do {
            for (i = from; i < to; ++i) {
                wrapper = sequences.get(i);
                for (j = 0; j < 3; ++j) {
                    if (wrapper.penaltyValue + penalty[j] <= max &&
                            (maxErrors == null ||
                                    wrapper.getErrorsOfType(j) < maxErrors[j]))
                        sequences.add(wrapper.next(j, penalty[j]));
                }
            }
            from = to;
            to = sequences.size();
        } while (from != to);

        final Comparator<SequenceWrapper> wrapperComparator = new Comparator<SequenceWrapper>() {
            @Override
            public int compare(SequenceWrapper o1, SequenceWrapper o2) {
                int v;

                if ((v = Double.compare(o1.penaltyValue, o2.penaltyValue)) != 0)
                    return v;

                if ((v = Double.compare(o1.sequence.length, o2.sequence.length)) != 0)
                    return v;

                for (int i = 0; i < o1.sequence.length; ++i)
                    if ((v = Double.compare(penalty[o1.sequence[i]],
                            penalty[o2.sequence[i]])) != 0)
                        return v;

                for (int i = 0; i < o1.sequence.length; ++i)
                    if ((v = Byte.compare(o1.sequence[i], o2.sequence[i])) != 0)
                        return v;

                return 0;
            }
        };

        Collections.sort(sequences, wrapperComparator);

        byte[][] result = new byte[sequences.size()][];
        for (i = sequences.size() - 1; i >= 0; --i)
            result[i] = sequences.get(i).sequence;
        return result;
    }

    private static final class SequenceWrapper {
        final byte[] sequence;
        final double penaltyValue;

        SequenceWrapper() {
            this.penaltyValue = 0.0;
            this.sequence = new byte[0];
        }

        private SequenceWrapper(byte[] sequence, double penaltyValue) {
            this.sequence = sequence;
            this.penaltyValue = penaltyValue;
        }

        public SequenceWrapper next(byte type, double penalty) {
            byte[] newSequence = Arrays.copyOfRange(sequence, 0, sequence.length + 1);
            newSequence[sequence.length] = type;
            return new SequenceWrapper(newSequence, penaltyValue + penalty);
        }

        public int getErrorsOfType(byte type) {
            int counter = 0;
            for (byte e : sequence)
                if (type == e)
                    ++counter;

            return counter;
        }

        //@Override
        //public int compareTo(SequenceWrapper o) {
        //    return Double.compare(this.penaltyValue, o.penaltyValue);
        //}
    }
}
