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
package com.milaboratory.mitcr.qualitystrategy;


import cc.redberry.pipe.Processor;

/**
 * Converter to {@link GoodBadNucleotideSequence} from other sequence container types. Converter should be compatible
 * with {@link com.milaboratory.mitcr.vdjmapping.ntree.NTreeNodeGenerator} used inside alignment algorithm.
 *
 * <p>See implementations for mode details.</p>
 *
 * @param <T> type of nucleotide sequence container
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public interface GoodBadNucleotideSequenceProvider<T> extends Processor<T, GoodBadNucleotideSequence> {
}
