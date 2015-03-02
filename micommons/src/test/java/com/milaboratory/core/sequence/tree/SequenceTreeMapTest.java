package com.milaboratory.core.sequence.tree;

import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.core.sequence.Sequence;
import com.milaboratory.core.sequence.SequenceBuilder;
import com.milaboratory.core.sequence.aminoacid.AminoAcidAlphabet;
import com.milaboratory.core.sequence.aminoacid.CDRAminoAcidAlphabet;
import com.milaboratory.core.sequence.aminoacid.CDRAminoAcidSequence;
import com.milaboratory.core.sequence.nucleotide.NucleotideAlphabet;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.util.Bit2Array;
import org.apache.commons.math.random.RandomGenerator;
import org.apache.commons.math.random.Well19937a;
import org.junit.Test;

import java.util.*;

import static com.milaboratory.core.sequence.util.SequencesUtils.cat;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class SequenceTreeMapTest {
    private int repeats = 10;

    public SequenceTreeMapTest() {
        this.repeats = 1;

        String val = System.getProperty("repeats");

        if (val != null) {
            try {
                this.repeats = Integer.valueOf(val, 10);
            } catch (NumberFormatException nfe) {
            }
        }
    }

    /*
     * Exact tests
     */

    @Test
    public void testExact1() throws Exception {
        SequenceTreeMap<NucleotideSequence, Integer> map = new SequenceTreeMap<>(NucleotideAlphabet.INSTANCE);

        assertNull(map.put(new NucleotideSequence("attagaca"), 1));
        assertEquals((Integer) 1, map.put(new NucleotideSequence("attagaca"), 2));

        assertNull(map.put(new NucleotideSequence("attacaca"), 3));

        assertEquals((Integer) 3, map.get(new NucleotideSequence("attacaca")));

        Set<NucleotideSequence> sequences = new HashSet<>();
        sequences.add(new NucleotideSequence("attacaca"));
        sequences.add(new NucleotideSequence("attagaca"));

        Set<Integer> ints = new HashSet<>();
        ints.add(2);
        ints.add(3);

        SequenceTreeMap.Node node;

        for (SequenceTreeMap.NodeIterator iterator = map.nodeIterator(); iterator.hasNext(); ) {
            node = iterator.next();
            assertTrue(ints.remove(node.object));
            assertTrue(sequences.remove(iterator.getSequence()));
        }

        assertTrue(sequences.isEmpty());
        assertTrue(ints.isEmpty());
    }

    @Test
    public void testExact2() throws Exception {
        SequenceTreeMap<NucleotideSequence, Integer> map = new SequenceTreeMap<>(NucleotideAlphabet.INSTANCE);
        Set<NucleotideSequence> sequences = new HashSet<>();
        Set<Integer> ints = new HashSet<>();

        assertNull(map.put(new NucleotideSequence("attacacaattaattacacacacaattacaca"), 3));
        sequences.add(new NucleotideSequence("attacacaattaattacacacacaattacaca"));
        ints.add(3);
        assertNull(map.put(new NucleotideSequence("attacacaattacacaattacgacacttacaca"), 4));
        sequences.add(new NucleotideSequence("attacacaattacacaattacgacacttacaca"));
        ints.add(4);
        assertNull(map.put(new NucleotideSequence("atattattacacaacacatacattacacaaca"), 5));
        sequences.add(new NucleotideSequence("atattattacacaacacatacattacacaaca"));
        ints.add(5);
        assertNull(map.put(new NucleotideSequence("attacacaattacacaattacacaattacacaattacacaattacaca"), 19));
        sequences.add(new NucleotideSequence("attacacaattacacaattacacaattacacaattacacaattacaca"));
        ints.add(19);

        SequenceTreeMap.Node node;
        for (SequenceTreeMap.NodeIterator iterator = map.nodeIterator(); iterator.hasNext(); ) {
            node = iterator.next();
            assertTrue(ints.remove(node.object));
            assertTrue(sequences.remove(iterator.getSequence()));
        }

        assertTrue(sequences.isEmpty());
        assertTrue(ints.isEmpty());
    }

    /*
     * Branching enumerator tests
     */

    @Test
    public void testBranchingEnumerator1() {
        SequenceTreeMap<NucleotideSequence, Integer> map = new SequenceTreeMap<>(NucleotideAlphabet.INSTANCE);

        assertNull(map.put(new NucleotideSequence("attagaca"), 1)); // 1 mm
        assertNull(map.put(new NucleotideSequence("attacaca"), 2)); // match
        assertNull(map.put(new NucleotideSequence("ataacaca"), 3)); // 1 mm
        assertNull(map.put(new NucleotideSequence("attcgtca"), 4)); // many mm
        assertNull(map.put(new NucleotideSequence("atttacaca"), 5)); // 1 insertion in stretch
        assertNull(map.put(new NucleotideSequence("atacaca"), 6)); // 1 deletion in the "t" stretch
        assertNull(map.put(new NucleotideSequence("attacacta"), 7)); // 1 insertion
        assertNull(map.put(new NucleotideSequence("attcaca"), 8)); // 1 deletion
        assertNull(map.put(new NucleotideSequence("attacac"), 9)); // 1 deletion in the end
        assertNull(map.put(new NucleotideSequence("ttacaca"), 10)); // 1 deletion in the beginning
        assertNull(map.put(new NucleotideSequence("tattacaca"), 11)); // 1 insertion in the beginning
        assertNull(map.put(new NucleotideSequence("attacacat"), 12)); // 1 insertion in the ent
        assertNull(map.put(new NucleotideSequence("attacact"), 13)); // 1 mm end
        assertNull(map.put(new NucleotideSequence("tttacaca"), 14)); // 1 mm begin

        HashSet<Integer>[] asserts = new HashSet[3];
        asserts[0] = new HashSet<>(Arrays.asList(1, 3, 13, 14));
        asserts[1] = new HashSet<>(Arrays.asList(6, 8, 9, 10));
        asserts[2] = new HashSet<>(Arrays.asList(5, 7, 11, 12));

        NucleotideSequence reference = new NucleotideSequence("attacaca");

        for (byte mode = 0; mode < 3; ++mode) {

            SequenceTreeMap.BranchingEnumerator<Integer> e =
                    new SequenceTreeMap.BranchingEnumerator<Integer>(reference, null);
            e.setup(mode, false);
            e.reset(0, map.root);
            SequenceTreeMap.Node<Integer> n;

            HashSet<Integer> collector = new HashSet<>();

            while ((n = e.next()) != null) {
                int i = e.getPosition();
                while (i < reference.size() && n != null)
                    n = n.links[reference.codeAt(i++)];
                if (n != null && n.object != null)
                    collector.add(n.object);
            }

            assertEquals(asserts[mode], collector);
        }
    }

    /*
     * Non-randomised tests for NeighborhoodIterator
     */

    @Test
    public void testNIterator() throws Exception {
        SequenceTreeMap<NucleotideSequence, Integer> map = new SequenceTreeMap<>(NucleotideAlphabet.INSTANCE);

        assertNull(map.put(new NucleotideSequence("attagaca"), 1)); // 1 mm
        assertNull(map.put(new NucleotideSequence("attacaca"), 2)); // match
        assertNull(map.put(new NucleotideSequence("ataacaca"), 3)); // 1 mm
        assertNull(map.put(new NucleotideSequence("attcgtca"), 4)); // many mm
        assertNull(map.put(new NucleotideSequence("atttacaca"), 5)); // 1 insertion in stretch
        assertNull(map.put(new NucleotideSequence("atacaca"), 6)); // 1 deletion in the "t" stretch
        assertNull(map.put(new NucleotideSequence("attacacta"), 7)); // 1 insertion
        assertNull(map.put(new NucleotideSequence("attcaca"), 8)); // 1 deletion
        assertNull(map.put(new NucleotideSequence("attacac"), 9)); // 1 deletion in the end
        assertNull(map.put(new NucleotideSequence("ttacaca"), 10)); // 1 deletion in the beginning
        assertNull(map.put(new NucleotideSequence("tattacaca"), 11)); // 1 insertion in the beginning
        assertNull(map.put(new NucleotideSequence("attacacat"), 12)); // 1 insertion in the ent
        assertNull(map.put(new NucleotideSequence("attacact"), 13)); // 1 mm end
        assertNull(map.put(new NucleotideSequence("tttacaca"), 14)); // 1 mm begin

        NucleotideSequence reference = new NucleotideSequence("attacaca");

        SequenceTreeMap.Node<Integer> node;

        HashSet<Integer>[] allAsserts = new HashSet[3];
        allAsserts[0] = new HashSet<>(Arrays.asList(1, 3, 13, 14));
        allAsserts[1] = new HashSet<>(Arrays.asList(6, 8, 9, 10));
        allAsserts[2] = new HashSet<>(Arrays.asList(5, 7, 11, 12));

        for (int i = 0; i < 8; ++i) {

            double lastPenalty = -1.0;
            HashSet<Integer> asserts = new HashSet<>();
            asserts.add(2);
            int[] maxMut = new int[3];
            for (int j = 0; j < 3; ++j) {
                if (((0x1 << j) & i) != 0) {
                    maxMut[j] = 1;
                    asserts.addAll(allAsserts[j]);
                }
            }

            HashSet<Integer> asserts1 = new HashSet<>(asserts);

            SequenceTreeMap.NeighborhoodIterator ni = map.getNeighborhoodIterator(reference, 0.5,
                    new double[]{0.31, 0.301, 0.3001}, maxMut, null);

            while ((node = ni.nextNode()) != null) {
                assertTrue(lastPenalty <= ni.getPenalty());
                lastPenalty = ni.getPenalty();
                asserts.remove(node.object);
                assertTrue(asserts1.contains(node.object));
            }
            assertTrue(asserts.isEmpty());
        }
    }

    @Test
    public void testEdge1() throws Exception {
        NucleotideSequence sequence1 = new NucleotideSequence("CTG"),
                sequence2 = new NucleotideSequence("C");

        SequenceTreeMap<NucleotideSequence, Integer> map = new SequenceTreeMap<>(NucleotideAlphabet.INSTANCE);

        //map.put(sequence1, 1);
        map.put(sequence2, 2);

        SequenceTreeMap.NeighborhoodIterator<Integer> neighborhoodIterator =
                map.getNeighborhoodIterator(sequence1, 1.0,
                        new double[]{0.1, 0.1, Double.MAX_VALUE},
                        new int[]{0, 2, 0}, null);

        System.out.println(neighborhoodIterator.nextNode().object);
    }

    @Test
    public void testEdge2() throws Exception {
        NucleotideSequence sequence1 = new NucleotideSequence("CTG"),
                sequence2 = new NucleotideSequence("CGT");

        SequenceTreeMap<NucleotideSequence, Integer> map = new SequenceTreeMap<>(NucleotideAlphabet.INSTANCE);

        //map.put(sequence1, 1);
        map.put(sequence2, 2);

        SequenceTreeMap.NeighborhoodIterator<Integer> neighborhoodIterator =
                map.getNeighborhoodIterator(sequence1, 1.0,
                        new double[]{0.1, 0.1, Double.MAX_VALUE},
                        new int[]{2, 0, 0}, null);

        System.out.println(neighborhoodIterator.nextNode().object);
    }

    @Test
    public void testEdge3() throws Exception {
        NucleotideSequence sequence1 = new NucleotideSequence("C"),
                sequence2 = new NucleotideSequence("CTG");

        SequenceTreeMap<NucleotideSequence, Integer> map = new SequenceTreeMap<>(NucleotideAlphabet.INSTANCE);

        //map.put(sequence1, 1);
        map.put(sequence2, 2);

        SequenceTreeMap.NeighborhoodIterator<Integer> neighborhoodIterator =
                map.getNeighborhoodIterator(sequence1, 1.0,
                        new double[]{0.1, 0.1, 0.1},
                        new int[]{0, 0, 2}, null);

        System.out.println(neighborhoodIterator.nextNode().object);
    }

    /*
     * Non-randomised tests for NeighborhoodIterator in guided mode
     */

    @Test
    public void testGuideDel() throws Exception {
        SequenceTreeMap<NucleotideSequence, Integer> map = new SequenceTreeMap<>(NucleotideAlphabet.INSTANCE);
        map.put(new NucleotideSequence("attacacaattaattacacacacaattacaca"), 3);

        Sequence sequence = new NucleotideSequence("attacacaattaatttacacacacaattacaca");

        SequenceTreeMap.NeighborhoodIterator<Integer> neighborhoodIterator =
                map.getNeighborhoodIterator(sequence, 0.2,
                        new double[]{0.1, 0.1, Double.MAX_VALUE},
                        new int[]{1, 1, 0}, new MutationGuide<NucleotideSequence>() {
                    @Override
                    public boolean allowMutation(NucleotideSequence ref, int position, byte type, byte code) {
                        return position == 15 && type == 1;
                    }
                });

        assertNotNull(neighborhoodIterator.nextNode());

        neighborhoodIterator =
                map.getNeighborhoodIterator(sequence, 0.2,
                        new double[]{0.1, 0.1, Double.MAX_VALUE},
                        new int[]{1, 1, 0}, new MutationGuide<NucleotideSequence>() {
                    @Override
                    public boolean allowMutation(NucleotideSequence ref, int position, byte type, byte code) {
                        return position == 16 && type == 1;
                    }
                });

        assertNull(neighborhoodIterator.nextNode());
    }

    @Test
    public void testGuideMM() throws Exception {
        SequenceTreeMap<NucleotideSequence, Integer> map = new SequenceTreeMap<>(NucleotideAlphabet.INSTANCE);
        map.put(new NucleotideSequence("attacacaattaattacacacacaattacaca"), 3);
        //map.put(new NucleotideSequence("attacacaattaatttacacacacaattacaca"), 4);

        Sequence sequence = new NucleotideSequence("attacacaattaataacacacacaattacaca");

        SequenceTreeMap.NeighborhoodIterator<Integer> neighborhoodIterator =
                map.getNeighborhoodIterator(sequence, 0.2,
                        new double[]{0.1, 0.1, Double.MAX_VALUE},
                        new int[]{1, 1, 0}, new MutationGuide<NucleotideSequence>() {
                    @Override
                    public boolean allowMutation(NucleotideSequence ref, int position, byte type, byte code) {
                        return position == 14 && type == 0;
                    }
                });

        assertNotNull(neighborhoodIterator.nextNode());

        neighborhoodIterator =
                map.getNeighborhoodIterator(sequence, 0.2,
                        new double[]{0.1, 0.1, Double.MAX_VALUE},
                        new int[]{1, 1, 0}, new MutationGuide<NucleotideSequence>() {
                    @Override
                    public boolean allowMutation(NucleotideSequence ref, int position, byte type, byte code) {
                        return position == 15 && type == 0;
                    }
                });

        assertNull(neighborhoodIterator.nextNode());
    }

    @Test
    public void testGuideIns() throws Exception {
        SequenceTreeMap<NucleotideSequence, Integer> map = new SequenceTreeMap<>(NucleotideAlphabet.INSTANCE);
        map.put(new NucleotideSequence("attacacaattaattacacacacaattacaca"), 3);
        //map.put(new NucleotideSequence("attacacaattaatttacacacacaattacaca"), 4);

        Sequence sequence = new NucleotideSequence("attacacaattaatacacacacaattacaca");

        SequenceTreeMap.NeighborhoodIterator<Integer> neighborhoodIterator =
                map.getNeighborhoodIterator(sequence, 0.2,
                        new double[]{0.1, 0.1, 0.1},
                        new int[]{1, 1, 1}, new MutationGuide<NucleotideSequence>() {
                    @Override
                    public boolean allowMutation(NucleotideSequence ref, int position, byte type, byte code) {
                        return position == 14 && type == 2;
                    }
                });

        assertNotNull(neighborhoodIterator.nextNode());

        neighborhoodIterator =
                map.getNeighborhoodIterator(sequence, 0.2,
                        new double[]{0.1, 0.1, 0.1},
                        new int[]{1, 1, 1}, new MutationGuide<NucleotideSequence>() {
                    @Override
                    public boolean allowMutation(NucleotideSequence ref, int position, byte type, byte code) {
                        return position == 15 && type == 2;
                    }
                });

        assertNull(neighborhoodIterator.nextNode());
    }

    @Test
    public void testGuideNew() throws Exception {
        SequenceTreeMap<CDRAminoAcidSequence, Integer> map = new SequenceTreeMap<>(CDRAminoAcidAlphabet.INSTANCE);

        map.put(new CDRAminoAcidSequence("AA~SFD"), 3);
        map.put(new CDRAminoAcidSequence("AA~FD"), 4);

        Set<Integer> set = new HashSet<>();
        Integer i;

        MutationGuide guide = new MutationGuide() {
            @Override
            public boolean allowMutation(Sequence reference, int position, byte type, byte to) {
                return type == 2 && to == CDRAminoAcidAlphabet.NonFullCodon;
            }
        };

        SequenceTreeMap.NeighborhoodIterator<Integer> ni = map.getNeighborhoodIterator(new CDRAminoAcidSequence("AASFD"), 1, 1, 1, 1, guide);

        while ((i = ni.next()) != null) {
            set.add(i);
        }

        assertTrue(set.contains(3));
        assertFalse(set.contains(4));

        guide = new MutationGuide() {
            @Override
            public boolean allowMutation(Sequence reference, int position, byte type, byte to) {
                return type == 2 && to == CDRAminoAcidAlphabet.F;
            }
        };

        ni = map.getNeighborhoodIterator(new CDRAminoAcidSequence("AA~SD"), 1, 1, 1, 1, guide);

        while ((i = ni.next()) != null) {
            set.add(i);
        }

        assertTrue(set.contains(3));
        assertFalse(set.contains(4));


        guide = new MutationGuide() {
            @Override
            public boolean allowMutation(Sequence reference, int position, byte type, byte to) {
                return type == 1 && reference.codeAt(position) == CDRAminoAcidAlphabet.G;
            }
        };

        ni = map.getNeighborhoodIterator(new CDRAminoAcidSequence("AA~SGFD"), 1, 1, 1, 1, guide);

        while ((i = ni.next()) != null) {
            set.add(i);
        }

        assertTrue(set.contains(3));
        assertFalse(set.contains(4));
    }

    /*
         * Randomized tests
         */
    final RandomGenerator random = new Well19937a();

    /*
     * Utility functions and their tests
     */
    private Sequence getRandomSequence(Alphabet alphabet, int length) {
        SequenceBuilder builder = alphabet.getBuilderFactory().create(length);
        for (int i = 0; i < length; ++i)
            builder.setCode(i, (byte) random.nextInt(alphabet.codesCount()));
        return builder.create();
    }

    private Sequence introduceMutation(Sequence sequence, int type) {
        SequenceBuilder builder;
        int position, i;
        switch (type) {
            case -1: //Copy
                builder = sequence.getAlphabet().getBuilderFactory().create(sequence.size());
                for (i = 0; i < builder.size(); ++i)
                    builder.setCode(i, sequence.codeAt(i));
                return builder.create();

            case 0: //Mismatch
                if (sequence.getAlphabet() == NucleotideAlphabet.INSTANCE)
                    return introduceNucleotideMismatch((NucleotideSequence) sequence);
                builder = sequence.getAlphabet().getBuilderFactory().create(sequence.size());
                for (i = 0; i < builder.size(); ++i)
                    builder.setCode(i, sequence.codeAt(i));
                position = random.nextInt(sequence.size());
                builder.setCode(position, (byte) ((sequence.codeAt(position) + 1 + random.nextInt(sequence.getAlphabet().codesCount() - 1)) %
                        sequence.getAlphabet().codesCount()));
                return builder.create();

            case 1: //Deletion
                builder = sequence.getAlphabet().getBuilderFactory().create(sequence.size() - 1);
                position = random.nextInt(sequence.size());
                for (i = 0; i < position; ++i)
                    builder.setCode(i, sequence.codeAt(i));
                ++i;
                for (; i < sequence.size(); ++i)
                    builder.setCode(i - 1, sequence.codeAt(i));
                return builder.create();

            case 2: //Insertion
                builder = sequence.getAlphabet().getBuilderFactory().create(sequence.size() + 1);
                position = random.nextInt(sequence.size() + 1);
                for (i = 0; i < position; ++i)
                    builder.setCode(i, sequence.codeAt(i));
                builder.setCode(position, (byte) random.nextInt(sequence.getAlphabet().codesCount()));
                for (; i < sequence.size(); ++i)
                    builder.setCode(i + 1, sequence.codeAt(i));
                return builder.create();

            default:
                throw new IllegalArgumentException();
        }
    }

    private NucleotideSequence introduceNucleotideMismatch(NucleotideSequence sequence) {
        final Bit2Array storage = sequence.getInnerData();
        int position = random.nextInt(storage.size());
        storage.set(position, 0x3 & (storage.get(position) + 1 + random.nextInt(3)));
        return new NucleotideSequence(storage);
    }

    final static Alphabet[] alphabets = {NucleotideAlphabet.INSTANCE, AminoAcidAlphabet.INSTANCE, CDRAminoAcidAlphabet.INSTANCE};

    private Alphabet getRandomAlphabet() {
        return alphabets[random.nextInt(alphabets.length)];
    }

    private Alphabet getAlphabetSequence(int id) {
        return alphabets[id % alphabets.length];
    }

    @Test
    public void testIntroduceMutation1() throws Exception {
        NucleotideSequence sequence;
        for (int i = 100; i > 0; --i) {
            sequence = (NucleotideSequence) getRandomSequence(NucleotideAlphabet.INSTANCE, 100);
            for (int j = 100; j > 0; --j)
                assertThat(sequence, not(introduceNucleotideMismatch(sequence)));
        }
    }

    @Test
    public void testIntroduceMutation2() throws Exception {
        Sequence sequence;
        Alphabet alphabet;
        for (int i = 100; i > 0; --i) {
            alphabet = getRandomAlphabet();
            sequence = getRandomSequence(alphabet, 100);

            //Testing correct equals implementation
            assertEquals(sequence, introduceMutation(sequence, -1));

            for (int j = 100; j > 0; --j) {
                assertThat(sequence, not(introduceMutation(sequence, 0)));
                assertThat(sequence, not(introduceMutation(sequence, 1)));
                assertThat(sequence, not(introduceMutation(sequence, 2)));
            }
        }
    }

    /*
     * More utility functions for randomized testing
     */
    private SequenceCluster generateCluster(Alphabet alphabet, int maxInCluster, int... maxMutations) {
        final SequenceCluster cluster = new SequenceCluster(getRandomSequence(alphabet, 200 + randomInt(200)));
        Sequence seq;
        int i, j;
        int[] mutations;
        for (i = 1 + random.nextInt(maxInCluster - 1); i > 0; --i) {
            seq = cluster.sequence;
            mutations = new int[3];

            for (j = (mutations[1] = randomInt(maxMutations[1] + 1)); j > 0; --j)
                seq = introduceMutation(seq, 1);

            for (j = (mutations[0] = randomInt(maxMutations[0] + 1)); j > 0; --j)
                seq = introduceMutation(seq, 0);

            for (j = (mutations[2] = randomInt(maxMutations[2] + 1)); j > 0; --j)
                seq = introduceMutation(seq, 2);

            if (seq.equals(cluster.sequence))
                continue;

            cluster.add(new MutatedSequence(seq, mutations));
        }

        return cluster;
    }

    private Sequence introduceErrors(Sequence seq, int[] maxMutations) {
        while (true) {
            int j;

            Sequence sequence = seq;

            for (j = randomInt(maxMutations[1] + 1); j > 0; --j)
                sequence = introduceMutation(sequence, 1);

            for (j = randomInt(maxMutations[0] + 1); j > 0; --j)
                sequence = introduceMutation(sequence, 0);

            for (j = randomInt(maxMutations[2] + 1); j > 0; --j)
                sequence = introduceMutation(sequence, 2);

            if (seq.equals(sequence))
                continue;

            return sequence;
        }
    }

    /**
     * Template for randomized test. See {@link #testRandomizedTest4Clusters()}
     */
    public void clusterTest(Alphabet alphabet, int clusterCount, int inCluster, int[] errors) {
        SequenceCluster[] clusters = new SequenceCluster[clusterCount];

        SequenceTreeMap<Sequence, Integer> sequenceTreeMap = new SequenceTreeMap<>(alphabet);

        for (int i = 0; i < clusters.length; ++i) {
            clusters[i] = generateCluster(alphabet, inCluster, errors);
            sequenceTreeMap.put(clusters[i].sequence, 0);
            for (MutatedSequence s : clusters[i].mutatedSequences)
                sequenceTreeMap.put(s.sequence, s.hashCode());
        }

        for (int i = 0; i < clusters.length; ++i) {
            SequenceTreeMap.NeighborhoodIterator<Integer> neighborhoodIterator =
                    sequenceTreeMap.getNeighborhoodIterator(clusters[i].sequence, 1.0,
                            new double[]{0.1, 0.1, 0.1},
                            errors, null);

            Set<Integer> set = new HashSet<>(clusters[i].hashes);
            set.add(0);

            SequenceTreeMap.Node<Integer> n;
            while ((n = neighborhoodIterator.nextNode()) != null)
                set.remove(n.object);

            if (!set.isEmpty()) {
                int k = set.iterator().next();
                for (MutatedSequence ms : clusters[i].mutatedSequences)
                    if (ms.hashCode() == k)
                        set.remove(1);
            }
            assertTrue(set.isEmpty());
        }
    }

    int randomInt(int i) {
        if (i == 0)
            return 0;
        return random.nextInt(i);
    }

    /*
     * Randomized tests
     */

    @Test
    public void testRandomizedTest1() throws Exception {
        for (int f = 0; f < repeats; ++f) {
            System.out.println(f);

            Alphabet alphabet = NucleotideAlphabet.INSTANCE;
            SequenceCluster[] clusters = new SequenceCluster[300];

            SequenceTreeMap<Sequence, Integer> sequenceTreeMap = new SequenceTreeMap<>(alphabet);
            for (int i = 0; i < clusters.length; ++i) {
                clusters[i] = generateCluster(alphabet, 70, 2, 2, 0);
                sequenceTreeMap.put(clusters[i].sequence, 0);
                for (MutatedSequence s : clusters[i].mutatedSequences)
                    sequenceTreeMap.put(s.sequence, s.hashCode());
            }

            for (int i = 0; i < clusters.length; ++i) {
                SequenceTreeMap.NeighborhoodIterator<Integer> neighborhoodIterator =
                        sequenceTreeMap.getNeighborhoodIterator(clusters[i].sequence, 1.0,
                                new double[]{0.1, 0.1, Double.MAX_VALUE},
                                new int[]{2, 2, 0}, null);
                Set<Integer> set = new HashSet<>(clusters[i].hashes);
                set.add(0);
                SequenceTreeMap.Node<Integer> n;
                while ((n = neighborhoodIterator.nextNode()) != null)
                    set.remove(n.object);

                if (!set.isEmpty()) {
                    int k = set.iterator().next();
                    for (MutatedSequence ms : clusters[i].mutatedSequences)
                        if (ms.hashCode() == k)
                            set.remove(1);
                }
                assertTrue(set.isEmpty());
            }
        }
    }

    @Test
    public void testRandomizedTest4Clusters() {
        for (byte t = 0; t < 3; ++t) {
            int[] mut = new int[3];
            mut[t] = 2;
            clusterTest(NucleotideAlphabet.INSTANCE, 100, 30, mut);
            clusterTest(CDRAminoAcidAlphabet.INSTANCE, 100, 30, mut);
            clusterTest(AminoAcidAlphabet.INSTANCE, 100, 30, mut);
        }
    }

    /*
     * Randomized tests for guided search
     */

    @Test
    public void testRandomizedTest3() throws Exception {
        for (int f = 0; f < repeats * 6; ++f) {
            System.out.println(f);
            Alphabet alphabet = getAlphabetSequence(f);

            for (byte t = 0; t < 3; ++t) {
                final Sequence seqRight = getRandomSequence(alphabet, 50 + randomInt(50)),
                        seqLeft = getRandomSequence(alphabet, 50 + randomInt(50)),
                        spacer = getRandomSequence(alphabet, 200),
                        goodSequence = cat(seqLeft, spacer, seqRight);


                SequenceTreeMap<Sequence, Sequence> map = new SequenceTreeMap<>(alphabet);

                int[] mut = new int[3];
                mut[t] = 3;

                HashSet<Sequence> lErr = new HashSet<>(),
                        rErr = new HashSet<>(), lrErr = new HashSet<>();

                Sequence seq1, seq2, mseq;

                for (int i = 0; i < 100; ++i) {
                    //Left Error
                    seq1 = introduceErrors(seqLeft, mut);
                    mseq = cat(seq1, spacer, seqRight);
                    lErr.add(mseq);
                    map.put(mseq, mseq);

                    //Right Error
                    seq1 = introduceErrors(seqRight, mut);
                    mseq = cat(seqLeft, spacer, seq1);
                    rErr.add(mseq);
                    map.put(mseq, mseq);

                    //LR Error
                    seq1 = introduceErrors(seqLeft, mut);
                    seq2 = introduceErrors(seqRight, mut);
                    mseq = cat(seq1, spacer, seq2);
                    lrErr.add(mseq);
                    map.put(mseq, mseq);
                }

                SequenceTreeMap.Node<Sequence> n;

                //Left run
                SequenceTreeMap.NeighborhoodIterator<Sequence> neighborhoodIterator =
                        map.getNeighborhoodIterator(goodSequence, 1.3,
                                new double[]{0.1, 0.1, 0.1},
                                mut, new MutationGuide<Sequence>() {
                            @Override
                            public boolean allowMutation(Sequence ref, int position, byte type, byte code) {
                                return position < seqLeft.size() + 100;
                            }
                        });

                HashSet<Sequence> acc = new HashSet<>(lErr);

                while ((n = neighborhoodIterator.nextNode()) != null) {
                    assertTrue(lErr.contains(n.object));
                    assertFalse(rErr.contains(n.object));
                    assertFalse(lrErr.contains(n.object));
                    acc.remove(n.object);
                }
                assertTrue(acc.isEmpty());

                //Right run
                neighborhoodIterator =
                        map.getNeighborhoodIterator(goodSequence, 1.3,
                                new double[]{0.1, 0.1, 0.1},
                                mut, new MutationGuide<Sequence>() {
                            @Override
                            public boolean allowMutation(Sequence ref, int position, byte type, byte code) {
                                return position > seqLeft.size() + 100;
                            }
                        });

                acc = new HashSet<>(rErr);

                while ((n = neighborhoodIterator.nextNode()) != null) {
                    assertTrue(rErr.contains(n.object));
                    assertFalse(lErr.contains(n.object));
                    assertFalse(lrErr.contains(n.object));
                    acc.remove(n.object);
                }
                assertTrue(acc.isEmpty());
            }
        }
    }

    /**
     * Position tests
     */

    //@Test
    //public void testPosition() throws Exception {
    //    NSTa map = new NSTa();
    //
    //    //assertNull(map.put(new NucleotideSequence("attagaca"))); // 1 mm
    //    //assertNull(map.put(new NucleotideSequence("attacaca"))); // match
    //    //assertNull(map.put(new NucleotideSequence("ataacaca"))); // 1 mm
    //    //assertNull(map.put(new NucleotideSequence("attcgtca"))); // many mm
    //    //assertNull(map.put(new NucleotideSequence("atttacaca"))); // 1 insertion in stretch
    //    //assertNull(map.put(new NucleotideSequence("atacaca"))); // 1 deletion in the "t" stretch
    //    //assertNull(map.put(new NucleotideSequence("attacacga"))); // 1 insertion
    //    //assertNull(map.put(new NucleotideSequence("attcaca"))); // 1 deletion
    //    //assertNull(map.put(new NucleotideSequence("attacac"))); // 1 deletion in the end
    //    //assertNull(map.put(new NucleotideSequence("ttacaca"))); // 1 deletion in the beginning
    //    //assertNull(map.put(new NucleotideSequence("tattacaca"))); // 1 insertion in the beginning
    //    //assertNull(map.put(new NucleotideSequence("attacacat"))); // 1 insertion in the ent
    //    assertNull(map.put(new NucleotideSequence("attacagt"))); // 1 mm end
    //    //assertNull(map.put(new NucleotideSequence("attacaca"))); // 1 mm end
    //    //assertNull(map.put(new NucleotideSequence("tttacaca"))); // 1 mm begin
    //    //assertNull(map.put(new NucleotideSequence("cttagaca"))); // 2 mm begin
    //
    //    NucleotideSequence reference = new NucleotideSequence("attacaga");
    //
    //    SequenceTreeMap.NeighborhoodIterator<NucleotideSequence> ni = map.getNeighborhoodIterator(reference, 2, 1, 1, 2);
    //
    //    NucleotideSequence s;
    //
    //    while ((s = ni.next()) != null) {
    //        int lp = -1;
    //        for (int i = 0; i < ni.getMutationsCount(); ++i) {
    //            System.out.print(spaces(ni.getPosition(i) - lp));
    //            System.out.print(ni.getType(i));
    //            lp = ni.getPosition(i) + 1;
    //        }
    //        System.out.println();
    //        System.out.println(" " + reference);
    //        System.out.println(" " + s);
    //        for (int i = 0; i < ni.getMutationsCount(); ++i)
    //            if (ni.getType(i) == 2) {
    //                System.out.println("2" +
    //                        NucleotideAlphabet.INSTANCE.symbolFromCode(ni.getCode(i)));
    //            } else
    //                System.out.println("" +
    //                        NucleotideAlphabet.INSTANCE.symbolFromCode(reference.codeAt(ni.getPosition(i))) +
    //                        ni.getType(i) +
    //                        NucleotideAlphabet.INSTANCE.symbolFromCode(ni.getCode(i)));
    //
    //
    //        System.out.println();
    //    }
    //}
    //
    //public static String spaces(int n) {
    //    if (n < 0)
    //        return "<";
    //    char[] chars = new char[n];
    //    Arrays.fill(chars, ' ');
    //    return new String(chars);
    //}
    //
    //public static final class NSTa extends SequenceTreeMap<NucleotideSequence, NucleotideSequence> {
    //    public NSTa() {
    //        super(NucleotideAlphabet.INSTANCE);
    //    }
    //
    //    public NucleotideSequence put(NucleotideSequence s) {
    //        return put(s, s);
    //    }
    //}

    /*
     * Classes for randomized tests
     */
    public static final class SequenceCluster {
        final Sequence sequence;
        final List<MutatedSequence> mutatedSequences = new ArrayList<>();
        final Set<Integer> hashes = new HashSet<>();

        public SequenceCluster(Sequence sequence) {
            this.sequence = sequence;
        }

        public void add(MutatedSequence s) {
            mutatedSequences.add(s);
            hashes.add(s.hashCode());
        }
    }

    public static final class MutatedSequence {
        final int[] mutations;
        final Sequence sequence;

        public MutatedSequence(Sequence sequence, int... mutations) {
            this.mutations = mutations;
            this.sequence = sequence;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MutatedSequence that = (MutatedSequence) o;

            if (!Arrays.equals(mutations, that.mutations)) return false;
            if (!sequence.equals(that.sequence)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            //int result = Arrays.hashCode(mutations);
            return sequence.hashCode();
        }
    }
}
