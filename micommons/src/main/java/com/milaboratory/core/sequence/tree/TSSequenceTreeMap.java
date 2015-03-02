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

import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.core.sequence.Sequence;
import com.milaboratory.core.sequence.SequenceBuilder;
import com.milaboratory.core.sequence.SequenceBuilderFactory;
import com.milaboratory.util.Factory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Semi-thread-safe, fast map with Sequence as key.
 *
 * <p>Implementation based on lexical tree.</p>
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class TSSequenceTreeMap<S extends Sequence, T> implements Iterable<T> {
    protected final Node<T> root;
    protected final Alphabet alphabet;

    public TSSequenceTreeMap(Alphabet alphabet) {
        this.alphabet = alphabet;
        this.root = new Node<>((byte) -1, null, alphabet.codesCount());
    }

    /**
     * Returns the value associated with this sequence.
     *
     * @param key key
     * @return associated value or null if no object associated with this key
     */
    public T get(S key) {
        if (key.getAlphabet() != alphabet)
            throw new IllegalArgumentException("Sequence with different alphabet.");
        Node<T> node = root;
        for (int i = 0; i < key.size(); ++i)
            if ((node = node.next.get(key.codeAt(i))) == null)
                return null;
        return node.getValue();
    }

    /**
     * Tests weather this map contains specified key.
     *
     * @param key key to search
     * @return true if map contains such key
     */
    public boolean containsKey(S key) {
        return get(key) != null;
    }

    /**
     * Returns the value associated with this sequence, if there are no associated objects creates new using factory,
     * associate it with provided key and return it.<br/> This method is useful to atomically add objects to the map.
     * Like CAS operation (compare [with null] and set).<br/><br/> <b>Important: </b> there could be dummy invocations
     * of <code>createInputStream()</code> method of the factory. So, not every created by factory object will be
     * associated with some key.<br/> <b>Important: </b> Factory must never return null. If so, it will result in
     * infinite loop in some conditions.
     *
     * @param key key
     * @return associated value or new object created by factory
     */
    public T getOrCreate(S key, Factory<T> factory) {
        if (key.getAlphabet() != alphabet)
            throw new IllegalArgumentException("Sequence with different alphabet.");
        Node<T> node = root;
        for (int i = 0; i < key.size(); ++i)
            node = node.getOrCreateNode(key.codeAt(i));
        return node.getOrCreateValue(factory);
    }


    /**
     * Returns the value associated with this sequence, if there are no associated objects creates new using factory,
     * associate it with provided key and return it.<br/> This method is useful to atomically add objects to the map.
     * Like CAS operation (compare [with null] and set).<br/><br/> No dummy invocations of
     * <code>createInputStream()</code> method of the factory.<br/> This method invokes factory
     * <code>createInputStream()</code> method only if there is no value for provided key. It is a little bit slower
     * then {@link TSSequenceTreeMap#getOrCreate(com.milaboratory.core.sequence.Sequence,
     * com.milaboratory.util.Factory)}s, but you have predictable count of <code>createInputStream()</code> method
     * invocations.<br/><br/> <p/> <b>Important:</b> never mix invocations of this method with invocations of {@link
     * TSSequenceTreeMap#getOrCreate(Sequence, com.milaboratory.util.Factory)} in concurrent manner!<br/> <b>Important:
     * </b> Factory must never return null. If so, it will result in infinite loop in some conditions.
     *
     * @param key key
     * @return associated value or new object created by factory
     */
    public T getOrCreateSync(S key, Factory<T> factory) {
        if (key.getAlphabet() != alphabet)
            throw new IllegalArgumentException("Sequence with different alphabet.");
        Node<T> node = root;
        for (int i = 0; i < key.size(); ++i)
            node = node.getOrCreateNode(key.codeAt(i));
        return node.getOrCreateValueSync(factory);
    }

    /**
     * Associates a value with specified key.
     *
     * @param key   key
     * @param value value
     * @return old value
     */
    public T put(S key, T value) {
        if (key.getAlphabet() != alphabet)
            throw new IllegalArgumentException("Sequence with different alphabet.");
        Node<T> node = root;
        for (int i = 0; i < key.size(); ++i)
            node = node.getOrCreateNode(key.codeAt(i));
        return node.setValue(value);
    }

    /**
     * Removes a value with specified key. <b>Use this method if removing elements synchronously.</b><br/><br/>
     * <b>Important:</b> this method is thread-<b>un</b>safe. Must be invoked invoked synchronously.
     *
     * @param key key
     * @return true if key was found and removed; false if key wasn't found
     */
    public boolean removeClean(S key) {
        if (key.getAlphabet() != alphabet)
            throw new IllegalArgumentException("Sequence with different alphabet.");
        Node<T> node = root;
        for (int i = 0; i < key.size(); ++i)
            if ((node = node.next.get(key.codeAt(i))) == null)
                return false; //No necessary branches found
        //Necessary leaf found. Removing branch.
        node.setValue(null);
        while (node.parent != null && node.isFree()) {
            node.parent.next.set(node.code, null);
            node = node.parent;
        }
        return true;
    }

    /**
     * Removes a value with specified key. <b>Use this method if removing elements concurrently.</b><br/><br/>
     * <b>Important:</b> this method is thread-safe, but it leaves an empty branch. Empty branches createInputStream
     * memory leaks and slowes down iteration over map.<br/> To remove all empty branches (some kind of garbage
     * collection) invoke {@link TSSequenceTreeMap#removeEmptyBranches()} in synchronized block.
     *
     * @param key key
     * @return true if key was found and removed; false if key wasn't found
     */
    public boolean removeDirty(S key) {
        if (key.getAlphabet() != alphabet)
            throw new IllegalArgumentException("Sequence with different alphabet.");
        Node<T> node = root;
        for (int i = 0; i < key.size(); ++i)
            if ((node = node.next.get(key.codeAt(i))) == null)
                return false; //No necessary branches found
        //Set value returns previous value.
        //If previous value == null, the node's value was already being removed.
        return node.setValue(null) != null;
    }

    /**
     * Removes a value with specified key. <b>Use this method to concurrently remove elements created by {@link
     * TSSequenceTreeMap#getOrCreateSync(com.milaboratory.core.sequence.Sequence, com.milaboratory.util.Factory)}.</b><br/><br/>
     * <b>Important:</b> this method is thread-safe, but it leaves an empty branch. Empty branches createInputStream
     * memory leaks and slowes down iteration over map.<br/> To remove all empty branches (some kind of garbage
     * collection) invoke {@link TSSequenceTreeMap#removeEmptyBranches()} in synchronized block.
     *
     * @param key key
     * @return true if key was found and removed; false if key wasn't found
     */
    public boolean removeDirtySync(S key) {
        if (key.getAlphabet() != alphabet)
            throw new IllegalArgumentException("Sequence with different alphabet.");
        Node<T> node = root;
        for (int i = 0; i < key.size(); ++i)
            if ((node = node.next.get(key.codeAt(i))) == null)
                return false; //No necessary branches found
        synchronized (node) {
            //Set value returns previous value.
            //If previous value == null, the node's value was already being removed.
            return node.setValue(null) != null;
        }
    }


    /**
     * <b>Thread-unsafe.</b> Removes empty branches produced by {@link TSSequenceTreeMap#removeDirty(Sequence)} method.
     *
     * @return number of nodes removed
     */
    public int removeEmptyBranches() {
        final int codesCount = alphabet.codesCount(); //For performance
        Node<T> node = root; //Current node
        int i = 0; //Pointer to current branch of the node
        int removed = 0;
        while (node != null) {
            if (i == codesCount) {
                if (node.isFree()) {
                    //Removing current node!
                    i = node.code;
                    node = node.parent;
                    if (node != null)
                        node.next.set(i, null);
                    ++i;
                    ++removed;
                } else {
                    //Step back
                    i = node.code + 1;
                    node = node.parent;
                }
                continue;
            }
            if (node.next.get(i) == null)
                ++i; //Next branch
            else {
                //Step forward
                node = node.next.get(i);
                i = 0;
            }
        }
        return removed;
    }

    /**
     * To iterate over all entries stored in ths map. Key in entries is lazy, so it is possible to iterate over values
     * without additional performance loses.
     *
     * @return iterable
     */
    public Iterable<Map.Entry<S, T>> entrySet() {
        return new Iterable<Map.Entry<S, T>>() {
            @Override
            public java.util.Iterator<Map.Entry<S, T>> iterator() {
                return new EntryIterator<T, S>(root, alphabet.getBuilderFactory());
            }
        };
    }

    @Override
    public java.util.Iterator<T> iterator() {
        return new Iterator<>(new EntryIterator<T, Sequence>(root, alphabet.getBuilderFactory()));
        //return new Iterator<>(root, alphabet.codesCount());
    }

    /**
     * {@link java.util.Map.Entry} wrapper for a node.
     *
     * @param <T> type of objects stored in a tree
     * @param <S> sequence type
     */
    private static class Entry<T, S extends Sequence> implements Map.Entry<S, T> {
        private final Node<T> node;
        private final SequenceBuilderFactory<S> factory;
        private S sequence = null; //Lazy initialized sequence

        private Entry(Node<T> node, SequenceBuilderFactory<S> factory) {
            this.node = node;
            this.factory = factory;
        }

        @Override
        public S getKey() {
            if (sequence == null)
                sequence = node.buildSequence(factory);
            return sequence;
        }

        @Override
        public T getValue() {
            return node.getValue();
        }

        @Override
        public T setValue(T value) {
            return node.setValue(value);
        }
    }

    /**
     * Iterator over entries in this map.
     *
     * @param <T> type of stored objects
     * @param <S> type of key sequences
     */
    private static class EntryIterator<T, S extends Sequence> implements java.util.Iterator<Map.Entry<S, T>> {
        private Node<T> node;
        private Map.Entry<S, T> next;
        private final int codesCount;
        private final SequenceBuilderFactory<S> factory;

        public EntryIterator(Node<T> root, SequenceBuilderFactory<S> factory) {
            this.node = new Node<>((byte) -1, root, 0);
            this.codesCount = root.next.length();
            this.factory = factory;
            this.next = _next();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Map.Entry<S, T> next() {
            Map.Entry<S, T> ret = next;
            next = _next();
            return ret;
        }

        public final Map.Entry<S, T> _next() {
            byte i = (byte) (node.code + 1);
            node = node.parent;
            while (node != null) {
                if (i == codesCount)
                    if (node.getValue() != null)
                        return new Entry<T, S>(node, factory);
                    else {
                        i = (byte) (node.code + 1);
                        node = node.parent;
                        continue;
                    }
                if (node.next.get(i) == null)
                    ++i;
                else {
                    node = node.next.get(i);
                    i = 0;
                }
            }
            return null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(); //TODO implement (dirty remove)
        }
    }

    private static class Iterator<T> implements java.util.Iterator<T> {
        private final EntryIterator<T, ?> innreIterator;

        private Iterator(EntryIterator<T, ?> innreIterator) {
            this.innreIterator = innreIterator;
        }

        @Override
        public boolean hasNext() {
            return innreIterator.hasNext();
        }

        @Override
        public T next() {
            return innreIterator.next().getValue();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /*private Node<T> node;
        private T next;
        private final byte codesCount;

        public Iterator(Node<T> root, byte codesCount) {
            this.node = new Node<>((byte) -1, root, 0);
            this.next = _next();
            this.codesCount = codesCount;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public T next() {
            T ret = next;
            next = _next();
            return ret;
        }

        public final T _next() {
            byte i = (byte) (node.code + 1);
            node = node.parent;
            while (node != null) {
                if (i == codesCount)
                    if (node.getValue() != null)
                        return node.getValue();
                    else {
                        i = (byte) (node.code + 1);
                        node = node.parent;
                        continue;
                    }
                if (node.next.get(i) == null)
                    ++i;
                else {
                    node = node.next.get(i);
                    i = 0;
                }
            }
            return null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }*/
    }

    private final static class Node<T> {
        public final byte code;
        public final Node<T> parent;
        public final AtomicReferenceArray<Node<T>> next;
        private final AtomicReference<T> value = new AtomicReference<>(null);

        public Node(byte nucleotide, Node<T> parent, int codesCount) {
            this.code = nucleotide;
            this.parent = parent;
            this.next = new AtomicReferenceArray<>(codesCount);
        }

        public boolean isFree() {
            if (value.get() != null)
                return false;
            for (int i = 0; i < next.length(); ++i)
                if (next.get(i) != null)
                    return false;
            return true;
        }

        public T getValue() {
            return value.get();
        }

        public T setValue(T value) {
            return this.value.getAndSet(value);
        }

        /**
         * Build sequence guiding to this node. Determine size traversing down to the root node.
         *
         * @param factory factory to createInputStream sequence builder
         * @return sequence
         */
        public <S extends Sequence> S buildSequence(SequenceBuilderFactory<S> factory) {
            int size = 0;
            Node node = this;
            while ((node = node.parent) != null)
                size++;
            SequenceBuilder<S> builder = factory.create(size);
            node = this;
            builder.setCode(--size, node.code);
            while ((node = node.parent).parent != null)
                builder.setCode(--size, node.code);
            return builder.create();
        }

        public Node<T> getOrCreateNode(int code) {
            Node<T> node;
            if ((node = next.get(code)) == null) {
                Node<T> _node;
                if (next.compareAndSet(code, null, _node = new Node<>((byte) code, this, next.length()))) //Some created nodes will be left to GC.
                    node = _node; //Write to note variable only if new node was assigned to the array element
                else
                    node = next.get(code); //Else, read new value
            }
            return node;
        }

        public T getOrCreateValue(Factory<T> factory) {
            T v;
            if ((v = value.get()) == null)
                do {
                    T _v;
                    if (value.compareAndSet(null, _v = factory.create())) //Some created by factory objects will be left to GC.
                        v = _v;
                    else
                        v = value.get(); //if object was removed v == null
                } while (v == null); //race with remove
            return v; //v never == null, but value.get() could be null, if it was removed between previous and current lines
        }

        public T getOrCreateValueSync(Factory<T> factory) {
            T v;
            //Double check
            if ((v = value.get()) == null)
                synchronized (this) {
                    if ((v = value.get()) == null)
                        value.set(v = factory.create());
                }
            //v never == null, but value.get() could be null, if it was removed between previous and current lines
            //synchronization only for factory.createInputStream()
            return v;
        }
    }
}
