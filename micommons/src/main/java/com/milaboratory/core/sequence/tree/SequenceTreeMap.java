package com.milaboratory.core.sequence.tree;

import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.core.sequence.Sequence;
import com.milaboratory.core.sequence.SequenceBuilder;

import java.util.Arrays;

/**
 * Sequence tree map, with fast neighbours search.
 *
 * <p>Types of mutations: <br/>
 * 0 = mismatch, <br/>
 * 1 = deletion (excess nucleotide in the reference sequence relative to the target key),<br/>
 * 2 = insertion (missing nucleotide in the reference sequence relative to the target key)
 * </p>
 *
 * @param <S> - key type (must be a sequence)
 * @param <O> - value type
 */
public class SequenceTreeMap<S extends Sequence, O> {
    private static final double[] DefaultPenalty = {0.1, // Mismatch penalty
            0.21, // Deletion penalty
            0.32}; // Insertion penalty

    private static final double UniformPenaltyValue = 0.1;
    private static final double[] UniformPenalty = {0.1, // Mismatch penalty
            0.1, // Deletion penalty
            0.1}; // Insertion penalty

    public final Alphabet alphabet;
    public final Node<O> root;

    /**
     * Creates a tree map for specified {@link Alphabet}.
     *
     * @param alphabet alphabet
     */
    public SequenceTreeMap(Alphabet alphabet) {
        this.alphabet = alphabet;
        this.root = new Node(alphabet.codesCount());
    }

    public O put(S sequence, O object) {
        final int size = sequence.size();
        Node<O> node = root;
        for (int i = 0; i < size; ++i)
            node = node.getOrCreate(sequence.codeAt(i));
        O prev = node.object;
        node.object = object;
        return prev;
    }

    public Node<O> getNode(S sequence) {
        final int size = sequence.size();
        Node<O> node = root;
        for (int i = 0; i < size; ++i)
            if ((node = node.links[sequence.codeAt(i)]) == null)
                break;
        return node;
    }

    public O get(S sequence) {
        Node<O> node = getNode(sequence);
        if (node == null)
            return null;
        return node.object;
    }

    public Iterable<O> values() {
        return new Iterable<O>() {
            @Override
            public java.util.Iterator<O> iterator() {
                final NodeIterator ni = nodeIterator();
                return new java.util.Iterator<O>() {
                    @Override
                    public boolean hasNext() {
                        return ni.hasNext();
                    }

                    @Override
                    public O next() {
                        return ni.next().object;
                    }

                    @Override
                    public void remove() {
                        ni.remove();
                    }
                };
            }
        };
    }

    public Iterable<Node<O>> nodes() {
        return new Iterable<Node<O>>() {
            @Override
            public java.util.Iterator<Node<O>> iterator() {
                return new NodeIterator(root);
            }
        };
    }

    public NodeIterator nodeIterator() {
        return new NodeIterator(root);
    }

    public NeighborhoodIterator getNeighborhoodIterator(Sequence reference, int mismatches, int deletions, int insertions,
                                                        int totalErrors) {
        return getNeighborhoodIterator(reference, UniformPenaltyValue * totalErrors, UniformPenalty,
                new int[]{mismatches, deletions, insertions}, null);
    }

    public Iterable<O> getNeighborhood(final Sequence reference, final int mismatches, final int deletions, final int insertions,
                                       final int totalErrors, final MutationGuide guide) {
        return new Iterable<O>() {
            @Override
            public java.util.Iterator<O> iterator() {
                return new NeighbourhoodIteratorWrapper<O>(
                        getNeighborhoodIterator(reference, mismatches, deletions,
                                insertions, totalErrors, guide));
            }
        };
    }

    public NeighborhoodIterator getNeighborhoodIterator(Sequence reference, int mismatches, int deletions, int insertions,
                                                        int totalErrors, MutationGuide guide) {
        return getNeighborhoodIterator(reference, UniformPenaltyValue * totalErrors, UniformPenalty,
                new int[]{mismatches, deletions, insertions}, guide);
    }

    public NeighborhoodIterator getNeighborhoodIterator(Sequence reference, int mismatches, int deletions, int insertions) {
        double maxPenalty = .1;
        maxPenalty += mismatches * DefaultPenalty[0];
        maxPenalty += deletions * DefaultPenalty[1];
        maxPenalty += insertions * DefaultPenalty[2];

        return getNeighborhoodIterator(reference, maxPenalty, DefaultPenalty,
                new int[]{mismatches, deletions, insertions}, null);
    }

    public NeighborhoodIterator getNeighborhoodIterator(Sequence reference, double maxPenalty,
                                                        double[] penalties, int[] maxErrors, MutationGuide guide) {
        if (penalties.length != 3)
            throw new IllegalArgumentException();

        return new NeighborhoodIterator(reference, penalties, maxErrors, guide, maxPenalty, root);
    }

    public final static class NeighborhoodIterator<O> {
        //Reference sequence
        final Sequence reference;

        //Penalty & other restrictions
        final double[] penalties;
        final byte[][] branchingSequences;
        final MutationGuide guide;
        double maxPenalty;

        //Runtime data
        int branchingSequenceIndex = 0, lastEnumerator;
        Node<O> root;
        BranchingEnumerator<O>[] branchingEnumerators = new BranchingEnumerator[1];

        /**
         * Constrictor for root NeighborhoodIterator iterator.
         *
         * @param penalties  array with penalties for {mismatch, deletion, insertion}
         * @param maxPenalty maximal penalty
         * @param reference  reference sequence
         * @param root       root node of the tree
         */
        public NeighborhoodIterator(Sequence reference, double[] penalties,
                                    int[] maxErrors, MutationGuide guide,
                                    double maxPenalty, Node<O> root) {
            this.penalties = penalties.clone();
            this.guide = guide;
            this.maxPenalty = maxPenalty;
            this.reference = reference;
            this.root = root;
            this.branchingSequences = PenaltyUtils.getDifferencesCombination(maxPenalty, penalties, maxErrors);
            this.branchingEnumerators[0] = new BranchingEnumerator<>(reference, guide);

            setupBranchingEnumerators();
        }

        /**
         * Ensures capacity for storing BranchingEnumerators.
         *
         * @param newSize desired size
         */
        private void ensureCapacity(int newSize) {
            int oldSize;
            if ((oldSize = branchingEnumerators.length) < newSize) {
                branchingEnumerators = Arrays.copyOfRange(branchingEnumerators, 0, newSize);
                for (int i = oldSize; i < newSize; ++i)
                    branchingEnumerators[i] = new BranchingEnumerator<>(reference, guide);
            }
        }

        /**
         * Setts up BranchingEnumerators for current branching sequence
         */
        private void setupBranchingEnumerators() {
            //Getting required sequence of differences (mutations)
            final byte[] bSequence = branchingSequences[branchingSequenceIndex];

            //Ensure number of branching enumerators
            ensureCapacity(bSequence.length);

            //Setting up initial branching enumerators
            byte previous = -1, current;
            for (int i = 0; i < bSequence.length; ++i) {
                current = bSequence[i];

                branchingEnumerators[i].setup(current,
                        (previous == 1 && current == 2) || // prevents insertion right after deletion
                                (previous == 2 && current == 1) || // prevents deletion right after insertion
                                (previous == 2 && current == 0)); // prevents mismatch right after insertion

                previous = bSequence[i];
            }

            branchingEnumerators[0].reset(0, root);

            lastEnumerator = bSequence.length - 1;
        }

        public O next() {
            Node<O> n;
            if ((n = nextNode()) == null)
                return null;

            return n.object;
        }

        public Node<O> nextNode() {
            if (branchingSequenceIndex == branchingSequences.length)
                return null;

            Node<O> n;

            while (true) {

                if (lastEnumerator == -1) {
                    --lastEnumerator;
                    if ((n = traverseToTheEnd(root, 0)) != null && n.object != null)
                        return n;
                }

                int i = lastEnumerator;

                INNER:
                while (i >= 0) {
                    for (; i < lastEnumerator; ++i)
                        if ((n = branchingEnumerators[i].next()) != null)
                            branchingEnumerators[i + 1].reset(branchingEnumerators[i].getPosition(), n);
                        else {
                            --i;
                            continue INNER;
                        }

                    assert i == lastEnumerator;

                    if ((n = branchingEnumerators[i].next()) != null)
                        if ((n = traverseToTheEnd(n, branchingEnumerators[i].getPosition())) != null && n.object != null)
                            return n;
                        else
                            continue;
                    else
                        --i;
                }

                if ((++branchingSequenceIndex) >= branchingSequences.length ||
                        getPenalty() > maxPenalty) {
                    branchingSequenceIndex = branchingSequences.length;
                    return null;
                } else
                    setupBranchingEnumerators();

            }
        }

        public Node<O> traverseToTheEnd(Node<O> node, int position) {
            while (position < reference.size())
                if ((node = node.links[reference.codeAt(position++)]) == null)
                    break;

            return node;
        }

        public byte[] getDiffModeSequence() {
            return branchingSequences[branchingSequenceIndex];
        }

        public int getMutationsCount() {
            return branchingSequences[branchingSequenceIndex].length;
        }

        public byte getType(int i) {
            return branchingSequences[branchingSequenceIndex][i];
        }

        public int getPosition(int i) {
            return branchingEnumerators[i].getPosition() - 1;
        }

        public byte getCode(int i) {
            return branchingEnumerators[i].code;
        }

        public int getMismatches() {
            int ret = 0;

            for (byte b : getDiffModeSequence())
                if (b == 0)
                    ++ret;

            return ret;
        }

        public int getDeletions() {
            int ret = 0;

            for (byte b : getDiffModeSequence())
                if (b == 1)
                    ++ret;

            return ret;
        }

        public int getInsertions() {
            int ret = 0;

            for (byte b : getDiffModeSequence())
                if (b == 2)
                    ++ret;

            return ret;
        }

        public int[] getIntroducedDifferences() {
            int[] ret = new int[3];

            for (byte b : getDiffModeSequence())
                ++ret[b];

            return ret;
        }

        public double getPenalty() {
            double p = 0.0;

            //Getting required sequence of differences (mutations)
            final byte[] bSequence = branchingSequences[branchingSequenceIndex];

            //Calculating penalty
            for (int i = bSequence.length - 1; i >= 0; --i)
                p += penalties[bSequence[i]];

            return p;
        }
    }

    final static class BranchingEnumerator<O> {
        //reference sequence
        final Sequence reference;
        final MutationGuide guide;

        //Setup parameters
        byte mode;
        boolean autoMove1;

        //Runtime fields
        byte code;
        int position;
        Node<O> node;

        BranchingEnumerator(Sequence reference, MutationGuide guide) {
            this.reference = reference;
            this.guide = guide;
        }

        /**
         * @param mode
         * @param autoMove1 used to prevent mutually compensating mutations
         */
        public void setup(byte mode, boolean autoMove1) {
            this.mode = mode;
            this.autoMove1 = autoMove1;
            this.node = null;
        }

        public void reset(int position, Node<O> node) {
            this.position = position;
            this.node = node;
            this.code = -1;

            if (autoMove1)
                move1();

            checkIterationEnd();
        }

        /**
         * Move the pointer one step forward. Move is made exactly matching the corresponding nucleotide in the
         * reference sequence, so this method prevents branching in the current position.
         */
        private void move1() {
            if (node == null)
                return;

            if (reference.size() >= position) {
                node = null;
                return;
            }

            node = node.links[reference.codeAt(position++)];
        }

        public void checkIterationEnd() {
            switch (mode) {
                case 0:
                    if (position >= reference.size())
                        node = null;
                    return;
                case 1:
                    if (position >= reference.size() - 1 && code != -1)
                        node = null;
                    return;
                case 2:
                    if (position >= reference.size() + 1)
                        node = null;
                    return;
            }

            throw new IllegalStateException();
        }

        public Node<O> next() {
            if (node == null)
                return null;

            switch (mode) {
                case 0:
                    while (true) {
                        ++code;

                        if (code == reference.getAlphabet().codesCount()) {
                            if (position >= reference.size() - 1)
                                return node = null;

                            code = 0;
                            node = node.links[reference.codeAt(position++)];

                            if (node == null)
                                return null;
                        }

                        if (code == reference.codeAt(position))
                            continue;

                        if (node.links[code] != null &&
                                (guide == null || guide.allowMutation(reference, position, (byte) 0, code)))
                            return node.links[code];
                    }
                case 1:
                    do {
                        if (position >= reference.size() - 1 && code != -1 || // ?
                                position >= reference.size()) //Out of sequence range
                            return node = null;

                        if (code != -1) {
                            node = node.links[reference.codeAt(position++)];
                        } else
                            code = 0;

                        if (guide == null || guide.allowMutation(reference, position, (byte) 1, (byte) -1))
                            return node;
                    } while (node != null);
                case 2:
                    while (true) {
                        ++code;

                        if (code == reference.getAlphabet().codesCount()) {
                            if (position >= reference.size())
                                return node = null;

                            code = 0;
                            node = node.links[reference.codeAt(position++)];
                        }

                        if (node == null)
                            return null;

                        if (node.links[code] != null &&
                                (guide == null || guide.allowMutation(reference, position, (byte) 2, code)))
                            return node.links[code];
                    }
            }

            return null;
        }

        /**
         * Returns the position of next nucleotide after branching.
         *
         * @return
         */
        public int getPosition() {
            switch (mode) {
                case 0:
                    return position + 1;
                case 1:
                    return position + 1;
                case 2:
                    return position;
            }
            return -1;
        }
    }

    public final class Iterator implements java.util.Iterator<O> {
        NodeIterator nodeIterator;

        public Iterator(NodeIterator nodeIterator) {
            this.nodeIterator = nodeIterator;
        }

        @Override
        public boolean hasNext() {
            return nodeIterator.hasNext();
        }

        @Override
        public O next() {
            return nodeIterator.next().object;
        }

        @Override
        public void remove() {
            nodeIterator.remove();
        }
    }

    public final class NodeIterator implements java.util.Iterator<Node<O>> {
        int pointer = 0;
        NodeWrapper<O>[] wrappers = new NodeWrapper[10];

        public NodeIterator(Node<O> root) {
            wrappers[0] = new NodeWrapper<O>(root);
        }

        @Override
        public boolean hasNext() {
            moveNext();
            return pointer > 0;
        }

        @Override
        public Node<O> next() {
            return wrappers[pointer].node;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void moveNext() {
            NodeWrapper<O> nodeWrapper;
            Node<O> node;
            do {
                nodeWrapper = wrappers[pointer];
                node = nodeWrapper.getNext();
                if (node != null) {
                    ensureNext();
                    wrappers[++pointer].reset(node);
                    if (node.object != null)
                        return;
                } else
                    --pointer;
            } while (pointer > 0);
        }

        private void ensureNext() {
            if (pointer + 1 == wrappers.length)
                wrappers = Arrays.copyOf(wrappers,
                        (wrappers.length * 3) / 2 + 1);

            if (wrappers[pointer + 1] == null)
                wrappers[pointer + 1] = new NodeWrapper<O>();
        }

        public S getSequence() {
            SequenceBuilder builder = alphabet.getBuilderFactory().create(pointer);
            for (int i = 0; i < pointer; ++i)
                builder.setCode(i, wrappers[i].position);
            return (S) builder.create();
        }
    }

    private static final class NeighbourhoodIteratorWrapper<O> implements java.util.Iterator<O> {
        final NeighborhoodIterator<O> iterator;
        O next;

        private NeighbourhoodIteratorWrapper(NeighborhoodIterator<O> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return (next = iterator.next()) != null;
        }

        @Override
        public O next() {
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static final class NodeWrapper<O> {
        private byte position = -1;
        private Node<O> node;

        NodeWrapper() {
        }

        NodeWrapper(Node<O> node) {
            this.node = node;
        }

        void reset(Node<O> node) {
            this.node = node;
            this.position = -1;
        }

        Node<O> getNext() {
            Node<O> n;
            while (++position < node.links.length)
                if ((n = node.links[position]) != null)
                    return node.links[position];
            return null;
        }
    }

    public static final class Node<O> {
        final Node[] links;
        O object;

        public Node(int letters) {
            this.links = new Node[letters];
        }

        public Node<O> getOrCreate(byte code) {
            Node node;
            if ((node = links[code]) == null)
                node = links[code] = new Node(links.length);
            return node;
        }
    }
}
