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
package com.milaboratory.mitcr;

import com.milaboratory.core.sequence.quality.QualityFormat;
import com.milaboratory.core.sequencing.io.SSequencingDataReader;
import com.milaboratory.core.sequencing.io.fastq.SFastqReader;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractorParameters;
import com.milaboratory.mitcr.cdrextraction.Strand;
import com.milaboratory.mitcr.vdjmapping.AlignmentDirection;
import com.milaboratory.mitcr.vdjmapping.DSegmentMapperParameters;
import com.milaboratory.mitcr.vdjmapping.VJSegmentMapperParameters;
import com.milaboratory.util.CompressionType;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class MiTCRTestUtils {
    /**
     * Very good sample.
     */
    public static SSequencingDataReader getSampleTReader() {
        try {
            return new SFastqReader(MiTCRTestUtils.class.getClassLoader().getSystemResourceAsStream("good_ds_test.fastq.gz"),
                    QualityFormat.Phred33, CompressionType.GZIP);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Different sample.
     */
    public static SSequencingDataReader getSampleFReader() {
        try {
            return new SFastqReader(MiTCRTestUtils.class.getClassLoader().getSystemResourceAsStream("cdr3_sample.fastq.gz"),
                    QualityFormat.Phred33, CompressionType.GZIP);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * Sample 'flex' class parameters
     */
    public static CDR3ExtractorParameters getFlexCDR3ExtractionParameters() {
        return new CDR3ExtractorParameters(new VJSegmentMapperParameters(AlignmentDirection.Both, -4, 1, 12, 3),
                new VJSegmentMapperParameters(AlignmentDirection.Both, -1, 4, 12, 2),
                new DSegmentMapperParameters(6), Strand.Both);
    }

    public static SSequencingDataReader getReaderFromString(String readsContent) throws IOException {
        return new SFastqReader(new ByteArrayInputStream(readsContent.getBytes()), QualityFormat.Phred33, CompressionType.None);
    }
}
