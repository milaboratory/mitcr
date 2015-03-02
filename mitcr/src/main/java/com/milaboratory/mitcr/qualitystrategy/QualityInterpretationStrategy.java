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

import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequencing.read.SSequencingRead;
import com.milaboratory.mitcr.vdjmapping.ntree.NTreeNodeGenerator;
import org.jdom.Element;

/**
 * An abstract class for interpreter of nucleotide quality used by segment mapping algorithms and low-quality reads
 * mapping procedure.
 *
 * <p>This class is immutable.</p>
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public interface QualityInterpretationStrategy {
    /**
     * A generator of tree nodes to be utilized by tree-based algorithms
     *
     * @return tree node generator
     */
    NTreeNodeGenerator getGenerator();

    /**
     * Gets an object that provides good/bad quality marks to {@link NucleotideSQPair}
     *
     * @return good/bad quality provider
     */
    GoodBadNucleotideSequenceProvider<NucleotideSQPair> getProviderForNucleotideSQPair();

    /**
     * Gets an object that provides good/bad quality marks to {@link SSequencingRead}
     *
     * @return good/bad quality provider
     */
    GoodBadNucleotideSequenceProvider<SSequencingRead> getProviderForSRead();

    /**
     * Serializes current strategy to XML
     *
     * @param e base element to add sub-elements to
     * @return {@code e} with added information
     */
    Element asXML(Element e);
}
