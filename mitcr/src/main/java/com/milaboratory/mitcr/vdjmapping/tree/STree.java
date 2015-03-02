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

import com.milaboratory.core.segment.Allele;
import com.milaboratory.core.segment.SegmentGroupContainer;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.mitcr.util.evolver.Reactor;
import com.milaboratory.mitcr.vdjmapping.ntree.*;
import com.milaboratory.util.BitArray;

import java.util.List;

/**
 * Used in tree based segments mapping algorithms
 *
 * @author Dima
 */
public class STree {
    private NTreeNodeGenerator generator;
    private Node root;
    private int width, direction, from;
    private int maxLength = 0;
    private SegmentGroupContainer group;

    public STree(NTreeNodeGenerator generator, SegmentGroupContainer group, int from, int direction) {
        this.generator = generator;
        this.group = group;
        this.from = from;
        this.direction = direction;
        this.width = group.getAllelesCount();
        this.root = new Node(-1, width, null, (byte) -1);
        this.root.barcode.setAll();
        int coord;
        for (Allele allele : group.getAllelesList()) {
            NucleotideSequence sequence = allele.getSequence();
            coord = allele.getReferencePointPosition() + from;
            Node pointer = root;
            while (coord >= 0 && coord < sequence.size()) {
                byte code = sequence.codeAt(coord);
                pointer = pointer.createOrGetNext(code, width);
                if (pointer.coord > maxLength)
                    maxLength = pointer.coord;
                pointer.barcode.set(allele.getIndex());
                coord += direction;
            }
        }
        maxLength++;
    }

    public int getDirection() {
        return direction;
    }

    public int getFrom() {
        return from;
    }

    public int getWidth() {
        return width;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public SegmentGroupContainer getGroup() {
        return group;
    }

    public void performSearch(NucleotideInfoProvider provider, STreeResultHolder resultHolder) {
        if (resultHolder.width != resultHolder.width)
            throw new IllegalArgumentException();
        resultHolder.clean();
        Reactor<NTreeSlider<Node>, NucleotideInfo> reactor = resultHolder.getReactor(generator);
        BitArray barcode = new BitArray(width);
        for (List<NTreeSlider<Node>> sliders : reactor.generations(new NTreeSlider<>(root), provider, true, true)) {
            for (NTreeSlider<STree.Node> slider : sliders)
                barcode.or(slider.node.barcode);
            resultHolder.addBarcode(barcode, sliders);
            barcode.clearAll();
        }
        resultHolder.end();
    }

    public static class Node extends NTreeNode<Node> {
        public final BitArray barcode;
        public final int coord;

        public Node(int coord, int width, Node parent, byte nucleotide) {
            super(parent, nucleotide);
            this.next = new Node[4];
            this.barcode = new BitArray(width);
            this.coord = coord;
        }

        public Node createOrGetNext(byte code, int width) {
            if (next[code] == null)
                next[code] = new Node(coord + 1, width, this, code);
            return next[code];
        }
    }
}
