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
 * @author Dima
 */
public class NTreeSlider<N extends NTreeNode<N>> {
    public N node;
    public int goodSlides = 0;
    public int badSlides = 0;

    public NTreeSlider(N node) {
        this.node = node;
    }

    private NTreeSlider(NTreeSlider<N> slider) {
        this.node = slider.node;
        this.goodSlides = slider.goodSlides;
        this.badSlides = slider.badSlides;
    }

    public NTreeSlider<N> incrementSlide(boolean bad) {
        if (bad)
            badSlides++;
        else
            goodSlides++;
        return this;
    }

    public NTreeSlider<N> incrementGoodSlide() {
        goodSlides++;
        return this;
    }

    public NTreeSlider<N> incrementBadSlide() {
        badSlides++;
        return this;
    }

    public NTreeSlider<N> slide(byte code) {
        if (node.next[code] == null)
            return null;
        node = node.next[code];
        return this;
    }

    public boolean canSlide(byte code) {
        return node.next[code] != null;
    }

    public NTreeSlider<N> copy() {
        return new NTreeSlider<>(this);
    }
}
