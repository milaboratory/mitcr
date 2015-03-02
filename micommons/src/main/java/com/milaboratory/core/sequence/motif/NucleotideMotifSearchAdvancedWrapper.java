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

package com.milaboratory.core.sequence.motif;

public class NucleotideMotifSearchAdvancedWrapper<T> {
    private final NucleotideMotifSearchAdvanced search;
    private final NucleotideSQPairProvider<T> sqPairProvider;
    private final LowQualityIndicatorProvider<T> lqiProvider;

    public NucleotideMotifSearchAdvancedWrapper(NucleotideMotifSearchAdvanced search, NucleotideSQPairProvider<T> sqPairProvider, LowQualityIndicatorProvider<T> lqiProvider) {
        this.search = search;
        this.sqPairProvider = sqPairProvider;
        this.lqiProvider = lqiProvider;
    }

    public int search(T object) {
        return search.search(sqPairProvider.provide(object).getSequence(), lqiProvider.provide(object));
    }

    public int search(T object, int from, int to) {
        return search.search(sqPairProvider.provide(object).getSequence(), lqiProvider.provide(object),
                from, to);
    }

    public NucleotideMotifSearchAdvanced getAdvancedSearch() {
        return search;
    }

    public NucleotideMotif getMotif() {
        return search.getMotif();
    }
}
