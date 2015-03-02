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
import com.milaboratory.mitcr.vdjmapping.ntree.NTreeNodeGeneratorMatchOnly;
import org.jdom.Element;

/**
 * Constructs dummy (all good) {@link com.milaboratory.mitcr.qualitystrategy.GoodBadNucleotideSequenceProvider}s.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class DummyQualityInterpretationStrategy implements QualityInterpretationStrategy {
    public DummyQualityInterpretationStrategy() {
    }

    @Override
    public NTreeNodeGenerator getGenerator() {
        return NTreeNodeGeneratorMatchOnly.INSTANCE;
    }

    @Override
    public GoodBadNucleotideSequenceProvider<NucleotideSQPair> getProviderForNucleotideSQPair() {
        return createForNucleotideSQPair();
    }

    @Override
    public GoodBadNucleotideSequenceProvider<SSequencingRead> getProviderForSRead() {
        return createForSSequencingRead();
    }

    @Override
    public Element asXML(Element e) {
        return e.addContent(new Element("dummy"));
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return 2;
    }

    public static GoodBadNucleotideSequenceProvider<NucleotideSQPair> createForNucleotideSQPair() {
        return new GoodBadNucleotideSequenceProvider<NucleotideSQPair>() {
            @Override
            public GoodBadNucleotideSequence process(final NucleotideSQPair nucleotideSQPair) {
                return new GoodBadNucleotideSequence() {
                    @Override
                    public byte codeAt(int position) {
                        return nucleotideSQPair.getSequence().codeAt(position);
                    }

                    @Override
                    public int size() {
                        return nucleotideSQPair.size();
                    }

                    @Override
                    public boolean isBad(int position) {
                        return false;
                    }
                };
            }
        };
    }

    public static GoodBadNucleotideSequenceProvider<SSequencingRead> createForSSequencingRead() {
        return new GoodBadNucleotideSequenceProvider<SSequencingRead>() {
            @Override
            public GoodBadNucleotideSequence process(final SSequencingRead read) {
                return new GoodBadNucleotideSequence() {
                    @Override
                    public byte codeAt(int position) {
                        return read.getData().getSequence().codeAt(position);
                    }

                    @Override
                    public int size() {
                        return read.getData().size();
                    }

                    @Override
                    public boolean isBad(int position) {
                        return false;
                    }
                };
            }
        };
    }
}
