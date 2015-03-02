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

import com.milaboratory.core.sequence.nucleotide.NucleotideAlphabet;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.core.sequence.util.NucleotideSequenceGenerator;
import com.milaboratory.util.Factory;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TSSequenceTreeMapTest {
    @Test
    public void simpleTest() {
        NucleotideSequence sequence1 = new NucleotideSequence("ATTAGACA");
        NucleotideSequence sequence2 = new NucleotideSequence("ATTATACA");
        Integer i1 = 1;
        Integer i2 = 2;
        TSSequenceTreeMap<NucleotideSequence, Integer> map = new TSSequenceTreeMap<>(NucleotideAlphabet.INSTANCE);
        map.put(sequence1, i1);
        map.put(sequence2, i2);
        assertEquals(map.get(sequence1), i1);
        assertEquals(map.get(sequence2), i2);
    }

    @Test
    public void simpleIterationTest() {
        NucleotideSequence sequence1 = new NucleotideSequence("ATTAGACA");
        NucleotideSequence sequence2 = new NucleotideSequence("ATTATACA");
        Integer i1 = 1;
        Integer i2 = 2;
        TSSequenceTreeMap<NucleotideSequence, Integer> map = new TSSequenceTreeMap<>(NucleotideAlphabet.INSTANCE);
        map.put(sequence1, i1);
        map.put(sequence2, i2);

        //Entries
        Set<Integer> ints = new HashSet<>();
        ints.add(i1);
        ints.add(i2);

        Set<NucleotideSequence> seqs = new HashSet<>();
        seqs.add(sequence1);
        seqs.add(sequence2);

        for (Map.Entry<NucleotideSequence, Integer> e : map.entrySet()) {
            assertTrue(ints.remove(e.getValue()));
            assertTrue(seqs.remove(e.getKey()));
        }
        assertTrue(ints.isEmpty());
        assertTrue(seqs.isEmpty());

        //Values
        ints.add(i1);
        ints.add(i2);
        for (Integer e : map) {
            assertTrue(ints.remove(e));
        }
        assertTrue(ints.isEmpty());
    }


    @Test
    public void concurrentCreate() throws InterruptedException {
        final int N = 10000;
        final int K = 100;
        NucleotideSequence[] sequences = NucleotideSequenceGenerator.generate(12, N);
        TSSequenceTreeMap<NucleotideSequence, Integer> map = new TSSequenceTreeMap<>(NucleotideAlphabet.INSTANCE);
        Creator[] creators = new Creator[K];
        for (int i = 0; i < K; ++i)
            creators[i] = new Creator(sequences, map);
        for (Creator creator : creators)
            creator.start();
        for (Creator creator : creators)
            creator.join();
        int over = -N;
        for (Creator creator : creators)
            over += creator.intF.cv();
        System.out.println("Over invocations: " + over);
        //Testing content
        for (NucleotideSequence seq : sequences)
            assertTrue(map.containsKey(seq));
    }

    @Test
    public void concurrentCreateSync() throws InterruptedException {
        final int N = 10000;
        final int K = 100;
        NucleotideSequence[] sequences = NucleotideSequenceGenerator.generate(12, N);
        TSSequenceTreeMap<NucleotideSequence, Integer> map = new TSSequenceTreeMap<>(NucleotideAlphabet.INSTANCE);
        CreatorSync[] creators = new CreatorSync[K];
        for (int i = 0; i < K; ++i)
            creators[i] = new CreatorSync(sequences, map);
        for (CreatorSync creator : creators)
            creator.start();
        for (CreatorSync creator : creators)
            creator.join();
        int over = -N;
        for (CreatorSync creator : creators)
            over += creator.intF.cv();
        //Testings no additional invocations of factory method
        assertEquals(0, over);
        //Testing content
        for (NucleotideSequence seq : sequences)
            assertTrue(map.containsKey(seq));
    }

    @Test
    public void concurrentCreateDelete() throws InterruptedException {
        final int N = 10000;
        final int K = 100;
        NucleotideSequence[] sequences = NucleotideSequenceGenerator.generate(12, N);
        TSSequenceTreeMap<NucleotideSequence, Integer> map = new TSSequenceTreeMap<>(NucleotideAlphabet.INSTANCE);
        Creator[] creators = new Creator[K];
        Deleter[] deleters = new Deleter[K];
        for (int i = 0; i < K; ++i)
            creators[i] = new Creator(sequences, map);
        for (int i = 0; i < K; ++i)
            deleters[i] = new Deleter(sequences, map);
        for (int i = 0; i < K; ++i) {
            deleters[i].start();
            creators[i].start();
        }
        for (Creator creator : creators)
            creator.join();
        for (Deleter deleter : deleters)
            deleter.join();
        int created = 0;
        for (Creator creator : creators)
            created += creator.created;
        for (Deleter deleter : deleters)
            created -= deleter.deleted;
        System.out.println("Removed nodes: " + map.removeEmptyBranches());
        System.out.println("Total created: " + created);
        //Testing content
        for (NucleotideSequence seq : sequences)
            if (map.containsKey(seq))
                --created;
        assertEquals(0, created);
    }

    @Test
    public void concurrentCreateDeleteSync() throws InterruptedException {
        final int N = 10000;
        final int K = 100;
        NucleotideSequence[] sequences = NucleotideSequenceGenerator.generate(12, N);
        TSSequenceTreeMap<NucleotideSequence, Integer> map = new TSSequenceTreeMap<>(NucleotideAlphabet.INSTANCE);
        CreatorSync[] creators = new CreatorSync[K];
        DeleterSync[] deleters = new DeleterSync[K];
        for (int i = 0; i < K; ++i)
            creators[i] = new CreatorSync(sequences, map);
        for (int i = 0; i < K; ++i)
            deleters[i] = new DeleterSync(sequences, map);
        for (int i = 0; i < K; ++i) {
            deleters[i].start();
            creators[i].start();
        }
        for (CreatorSync creator : creators)
            creator.join();
        for (DeleterSync deleter : deleters)
            deleter.join();
        int created = 0;
        for (CreatorSync creator : creators) {
            created += creator.created;
            assertEquals(creator.created, creator.intF.cv());
        }
        for (DeleterSync deleter : deleters)
            created -= deleter.deleted;
        System.out.println("Removed nodes: " + map.removeEmptyBranches());
        System.out.println("Total created: " + created);
        //Testing content
        for (NucleotideSequence seq : sequences)
            if (map.containsKey(seq))
                --created;
        assertEquals(0, created);
    }

    public static class IntFactory implements Factory<Integer> {
        private final AtomicInteger ai = new AtomicInteger(0);
        private Integer lastValue;

        @Override
        public Integer create() {
            return lastValue = new Integer(ai.getAndIncrement());
        }

        public int cv() {
            return ai.get();
        }
    }

    public abstract static class AbstractWorker extends Thread {
        protected final NucleotideSequence[] sequences;
        protected final TSSequenceTreeMap<NucleotideSequence, Integer> map;

        public AbstractWorker(NucleotideSequence[] sequences, TSSequenceTreeMap<NucleotideSequence, Integer> map) {
            this.sequences = sequences;
            this.map = map;
        }
    }

    public static class Deleter extends AbstractWorker {
        public int deleted = 0;

        public Deleter(NucleotideSequence[] sequences, TSSequenceTreeMap<NucleotideSequence, Integer> map) {
            super(sequences, map);
        }

        @Override
        public void run() {
            for (NucleotideSequence sequence : sequences)
                if (map.removeDirty(sequence))
                    ++deleted;
        }
    }

    public static class DeleterSync extends AbstractWorker {
        public int deleted = 0;

        public DeleterSync(NucleotideSequence[] sequences, TSSequenceTreeMap<NucleotideSequence, Integer> map) {
            super(sequences, map);
        }

        @Override
        public void run() {
            for (NucleotideSequence sequence : sequences)
                if (map.removeDirtySync(sequence))
                    ++deleted;
        }
    }

    public abstract static class AbstractCreator extends AbstractWorker {
        protected final IntFactory intF = new IntFactory();

        protected AbstractCreator(NucleotideSequence[] sequences, TSSequenceTreeMap<NucleotideSequence, Integer> map) {
            super(sequences, map);
        }

        public IntFactory getIntF() {
            return intF;
        }
    }

    public static class Creator extends AbstractCreator {
        public int created = 0;
        public int notCreated = 0;

        public Creator(NucleotideSequence[] sequences, TSSequenceTreeMap<NucleotideSequence, Integer> map) {
            super(sequences, map);
        }

        @Override
        public void run() {
            for (NucleotideSequence sequence : sequences)
                if (map.getOrCreate(sequence, intF) == intF.lastValue)
                    ++created;
                else
                    ++notCreated;
        }
    }

    public static class CreatorSync extends AbstractCreator {
        public int created = 0;
        public int notCreated = 0;

        public CreatorSync(NucleotideSequence[] sequences, TSSequenceTreeMap<NucleotideSequence, Integer> map) {
            super(sequences, map);
        }

        @Override
        public void run() {
            for (NucleotideSequence sequence : sequences)
                if (map.getOrCreateSync(sequence, intF) == intF.lastValue)
                    ++created;
                else
                    ++notCreated;
        }
    }
}
