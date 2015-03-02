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
import com.milaboratory.mitcr.vdjmapping.ntree.NTreeNodeGeneratorBadMismatch3B;
import org.jdom.Element;

/**
 * Constructs {@link GoodBadNucleotideSequenceProvider}s for Illumina sequence analysis.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class IlluminaQualityInterpretationStrategy implements QualityInterpretationStrategy {
    private final byte qualityThreshold;

    public IlluminaQualityInterpretationStrategy(byte qualityThreshold) {
        this.qualityThreshold = qualityThreshold;
    }

    public byte getQualityThreshold() {
        return qualityThreshold;
    }

    @Override
    public NTreeNodeGenerator getGenerator() {
        return NTreeNodeGeneratorBadMismatch3B.INSTANCE;
    }

    @Override
    public GoodBadNucleotideSequenceProvider<NucleotideSQPair> getProviderForNucleotideSQPair() {
        return createForNucleotideSQPair(qualityThreshold);
    }

    @Override
    public GoodBadNucleotideSequenceProvider<SSequencingRead> getProviderForSRead() {
        return createForSSequencingRead(qualityThreshold);
    }

    @Override
    public Element asXML(Element e) {
        return e.addContent(new Element("illumina").addContent(String.valueOf(qualityThreshold)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IlluminaQualityInterpretationStrategy that = (IlluminaQualityInterpretationStrategy) o;

        if (qualityThreshold != that.qualityThreshold) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) qualityThreshold;
    }

    public static GoodBadNucleotideSequenceProvider<NucleotideSQPair> createForNucleotideSQPair(final byte threshold) {
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
                        return nucleotideSQPair.getQuality().value(position) < threshold;
                    }
                };
            }
        };
    }

    public static GoodBadNucleotideSequenceProvider<SSequencingRead> createForSSequencingRead(final byte threshold) {
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
                        return read.getData().getQuality().value(position) < threshold;
                    }
                };
            }
        };
    }
}
