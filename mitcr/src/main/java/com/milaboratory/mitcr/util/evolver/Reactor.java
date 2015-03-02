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
package com.milaboratory.mitcr.util.evolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class to perform generations evolution under certain conditions sequence.<br/> Methods of this class are thread
 * unsafe.
 *
 * @param <I> individuals type
 * @param <C> conditions type
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class Reactor<I, C> {
    private List<I> currentGeneration;
    private List<I> nextGeneration;
    private int bufferPointer;
    private List<List<I>> generationsBuffer = new ArrayList<>();
    private ConditionsProvider<C> conditionsProvider;
    private Generator<I, C>[] generators;

    public Reactor(Generator<I, C>... generators) {
        this.generators = generators;
    }

    public List<I> getCurrentGeneration() {
        return (List<I>) currentGeneration;
    }

    public void setCurrentGeneration(List<I> currentGeneration) {
        this.currentGeneration = currentGeneration;
    }

    private boolean generate() {
        C condition = conditionsProvider.next();
        if (condition == null)
            return false;
        List<I> temp;
        for (Generator<I, C> generator : generators) {
            //Clear place for the next generation
            nextGeneration.clear();
            //Performing current stage of generation exchange
            generator.generate(currentGeneration, nextGeneration, condition);
            //Swaping generations
            temp = currentGeneration;
            currentGeneration = nextGeneration;
            nextGeneration = temp;
        }
        return true;
    }

    //    public List<I>[] toLastGenerationTracking(int numberOfGenerations, List<I> zeroGeneration, ConditionsProvider<C> conditionsProvider) {
    //        this.currentGeneration = zeroGeneration;
    //        this.conditionsProvider = conditionsProvider;
    //        return toLastGenerationTracking(numberOfGenerations);
    //    }
    //
    //    public List<I>[] toLastGenerationTracking(int numberOfGenerations) {
    //        bufferPointer = -1;
    //        List<I>[] generations = new List[numberOfGenerations];
    //        generations[0] = currentGeneration;
    //        while (true) {
    //            nextGeneration = generations[numberOfGenerations - 1];
    //            if (nextGeneration == null)
    //                nextGeneration = generationFromBuffer();
    //            if (!generate() || currentGeneration.isEmpty())
    //                return generations;
    //            System.arraycopy(generations, 0, generations, 1, numberOfGenerations - 1); //Shifting array
    //            generations[0] = currentGeneration; //Writing new generation to array
    //        }
    //    }
    private List<I> generationFromBuffer() {
        if ((++bufferPointer) == generationsBuffer.size())
            generationsBuffer.add(new ArrayList<I>());
        return generationsBuffer.get(bufferPointer);
    }

    private void resetBuffer() {
        bufferPointer = -1;
    }

    private void _initFromOne(I zeroElement) {
        this.currentGeneration = generationFromBuffer();
        this.currentGeneration.clear();
        this.currentGeneration.add(zeroElement);
    }

    private void _initFromList(List<I> zeroGeneration) {
        this.currentGeneration = generationFromBuffer();
        this.currentGeneration.clear();
        this.currentGeneration.addAll(zeroGeneration);
    }

    /**
     * Evolve zeroGeneration under provided conditions sequence until last (null) condition will not be provided or
     * until all elements will die.
     *
     * @param zeroGeneration     zero generation
     * @param conditionsProvider provider of conditions sequence
     * @return generation after last evolution step. Could be empty list, if all elements are dead.
     */
    public List<I> toLastCondition(List<I> zeroGeneration, ConditionsProvider<C> conditionsProvider) {
        resetBuffer();
        this.conditionsProvider = conditionsProvider;
        _initFromList(zeroGeneration);
        return _lastCondition();
    }

    /**
     * Evolve generation contains one element under provided conditions sequence until last (null) condition will be
     * provided or until all elements will die.
     *
     * @param zeroElement        content of zero generation
     * @param conditionsProvider provider of conditions sequence
     * @return generation after last evolution step. Could be empty list, if all elements are dead.
     */
    public List<I> toLastCondition(I zeroElement, ConditionsProvider<C> conditionsProvider) {
        resetBuffer();
        this.conditionsProvider = conditionsProvider;
        _initFromOne(zeroElement);
        return _lastCondition();
    }

    private List<I> _lastCondition() {
        this.nextGeneration = generationFromBuffer();
        while (generate()) ;
        return currentGeneration;
    }

    /**
     * Creates {@link Iterable} to iterate through evolving generations under provided conditions sequence. <br/>
     * Iteration will stop if last (null) condition will be provided, or all elements will die. Last option will work
     * only if stopOnEmpty flag is set.
     *
     * @param zeroGeneration     zero generation
     * @param conditionsProvider provider of conditions sequence
     * @param skipFirst          skip zeroGeneration in iteration. So, if this flag is set, first iterated generation
     *                           will be the next generation after zero generation.
     * @param stopOnEmpty        see description
     * @return see description
     */
    public Iterable<List<I>> generations(List<I> zeroGeneration, ConditionsProvider<C> conditionsProvider, final boolean skipFirst, final boolean stopOnEmpty) {
        resetBuffer();
        this.conditionsProvider = conditionsProvider;
        _initFromList(zeroGeneration);
        return _generations(skipFirst, stopOnEmpty);
    }

    /**
     * Creates {@link Iterable} to iterate through evolving generations under provided conditions sequence. <br/>
     * Iteration will stop if last (null) condition will be provided, or all elements will die. Last option will work
     * only if stopOnEmpty flag is set.
     *
     * @param zeroElement        content of zero generation
     * @param conditionsProvider provider of conditions sequence
     * @param skipFirst          skip zeroGeneration in iteration. So, if this flag is set, first iterated generation
     *                           will be the next generation after zero generation.
     * @param stopOnEmpty        see description
     * @return see description
     */
    public Iterable<List<I>> generations(I zeroElement, ConditionsProvider<C> conditionsProvider, final boolean skipFirst, final boolean stopOnEmpty) {
        resetBuffer();
        this.conditionsProvider = conditionsProvider;
        _initFromOne(zeroElement);
        return _generations(skipFirst, stopOnEmpty);
    }

    private Iterable<List<I>> _generations(final boolean skipFirst, final boolean stopOnEmpty) {
        this.nextGeneration = generationFromBuffer();
        return new Iterable<List<I>>() {
            @Override
            public java.util.Iterator<List<I>> iterator() {
                return new Iterator(skipFirst, stopOnEmpty);
            }
        };
    }

    private class Iterator implements java.util.Iterator<List<I>> {
        private boolean needGeneration = false;
        private boolean notEnded = true;
        private boolean stopOnEmpty;

        public Iterator(boolean skipFirst, boolean stopOnEmpty) {
            this.needGeneration = skipFirst;
            this.stopOnEmpty = stopOnEmpty;
        }

        @Override
        public boolean hasNext() {
            if (needGeneration) {
                needGeneration = false;
                notEnded = generate();
            }
            return notEnded && !(stopOnEmpty && currentGeneration.isEmpty());
        }

        @Override
        public List<I> next() {
            needGeneration = true;
            return currentGeneration;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
