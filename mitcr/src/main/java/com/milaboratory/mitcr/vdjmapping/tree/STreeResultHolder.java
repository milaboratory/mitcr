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
package com.milaboratory.mitcr.vdjmapping.tree;

import com.milaboratory.mitcr.util.evolver.Reactor;
import com.milaboratory.mitcr.vdjmapping.ntree.NTreeNodeGenerator;
import com.milaboratory.mitcr.vdjmapping.ntree.NTreeSlider;
import com.milaboratory.mitcr.vdjmapping.ntree.NucleotideInfo;
import com.milaboratory.util.BitArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Dima
 */
public class STreeResultHolder {
    private static final int initialCapacity = 5;
    private Reactor<NTreeSlider, NucleotideInfo> reactor;
    public int[] alignmentsLengths;
    public int[] alignmentsScore;
    private BitArray ones;
    public BitArray[] barcodeEvolution = new BitArray[initialCapacity];
    private List<STree.Node>[] nodesEvolution = new List[initialCapacity];
    private int[] lastOccuredCoord;
    public int width;
    public int size;
    public int maxScore;

    public STreeResultHolder(STree sTree) {
        this.width = sTree.getWidth();
        lastOccuredCoord = new int[sTree.getMaxLength()];
        alignmentsLengths = new int[width];
        alignmentsScore = new int[width];
        ones = new BitArray(width);
        ones.setAll();
        clean();
        for (int i = 0; i < initialCapacity; ++i) {
            barcodeEvolution[i] = new BitArray(width);
            nodesEvolution[i] = new ArrayList<>();
        }
    }

    private void ensureCapacity(int minCapacity) {
        int oldCapacity = barcodeEvolution.length;
        if (oldCapacity < minCapacity) {
            int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity)
                newCapacity = minCapacity;
            barcodeEvolution = Arrays.copyOf(barcodeEvolution, newCapacity);
            nodesEvolution = Arrays.copyOf(nodesEvolution, newCapacity);
            for (int i = oldCapacity; i < newCapacity; ++i) {
                barcodeEvolution[i] = new BitArray(width);
                nodesEvolution[i] = new ArrayList<>();
            }
        }
    }

    public int getMaxMappedCoord() {
        return getTreeCoordFromSequence(size - 1);
    }

    public int getTreeCoordFromSequence(int seqCoord) {
        return nodesEvolution[seqCoord].get(0).coord;
    }

    public int tryMapTreeCorrd(int coord) {
        return tryMapTreeCorrd(coord, ones);
    }

    public int tryMapTreeCorrd(int coord, BitArray mask) {
        if (coord < 0 || coord >= lastOccuredCoord.length)
            throw new IllegalArgumentException("STree does not containe coord. [" + coord + "]");
        int result;
        int pointer = result = lastOccuredCoord[coord];
        if (result == -1)
            return -1;
        pointer--;
        while (pointer >= coord) {
            for (STree.Node node : nodesEvolution[pointer])
                if (node.barcode.intersects(mask) && node.coord == coord)
                    return -1;
            pointer--;
        }
        return result;
    }

    Reactor<NTreeSlider<STree.Node>, NucleotideInfo> getReactor(NTreeNodeGenerator generator) {
        if (reactor == null)
            reactor = new Reactor<>(generator);
        return (Reactor) reactor;
    }

    public final void clean() {
        size = 0;
        maxScore = 0;
        Arrays.fill(alignmentsLengths, -1);
        Arrays.fill(lastOccuredCoord, -1);
        Arrays.fill(alignmentsScore, 0);
    }

    public void end() {
        if (size == 0)
            return;
        for (int i = 0; i < width; ++i) {
            if (barcodeEvolution[size - 1].get(i))
                alignmentsLengths[i] = size;
            if (alignmentsScore[i] > maxScore)
                maxScore = alignmentsScore[i];
        }
    }

    public void addBarcode(BitArray bitArray, List<NTreeSlider<STree.Node>> sliders) {
        ensureCapacity(size + 1);
        BitArray current = barcodeEvolution[size];
        BitArray previous = (size == 0 ? ones : barcodeEvolution[size - 1]);
        List<STree.Node> currentNodesList = nodesEvolution[size];
        currentNodesList.clear();
        for (NTreeSlider<STree.Node> slider : sliders) {
            currentNodesList.add(slider.node);
            for (int i = 0; i < width; ++i)
                if (slider.node.barcode.get(i))
                    alignmentsScore[i] = slider.goodSlides;
            if (slider.node.coord >= 0)
                lastOccuredCoord[slider.node.coord] = size;
        }
        current.loadValueFrom(bitArray);
        current.xor(previous);
        for (int i = 0; i < width; ++i)
            if (current.get(i)) {
                assert (alignmentsLengths[i] == -1);
                alignmentsLengths[i] = size;
            }
        current.loadValueFrom(bitArray);
        size++;
    }
}
