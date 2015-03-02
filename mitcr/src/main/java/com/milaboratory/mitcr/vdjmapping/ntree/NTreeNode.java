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
package com.milaboratory.mitcr.vdjmapping.ntree;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public abstract class NTreeNode<N extends NTreeNode> {
    public N parent;
    public N[] next;// = (N[]) new NTreeNode[4];
    public byte nucleotide;

    public NTreeNode(N parent, byte nucleotide) {
        this.parent = parent;
        this.nucleotide = nucleotide;
    }
}
