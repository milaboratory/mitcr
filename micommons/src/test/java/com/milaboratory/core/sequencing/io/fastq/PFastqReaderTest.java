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
package com.milaboratory.core.sequencing.io.fastq;

import com.milaboratory.core.sequence.quality.QualityFormat;
import com.milaboratory.core.sequencing.io.PSequencingDataReader;
import com.milaboratory.core.sequencing.read.PSequencingRead;
import com.milaboratory.util.CompressionType;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class PFastqReaderTest {
    @Test
    public void test0() throws URISyntaxException, IOException {
        File sampleR1 = new File(ClassLoader.getSystemResource("sample_r1.fastq").toURI());
        File sampleR2 = new File(ClassLoader.getSystemResource("sample_r2.fastq").toURI());
        PSequencingDataReader reader = new PFastqReader(sampleR1, sampleR2, QualityFormat.Phred33, CompressionType.None,
                new Casava18InfoProvider(), true, true);
        PSequencingRead read;
        int count = 0;
        while ((read = reader.take()) != null) {
            ++count;
        }
        Assert.assertEquals(8, count);
    }

    @Test
    public void test1() throws URISyntaxException, IOException {
        File sampleR1 = new File(ClassLoader.getSystemResource("sample_r1.fastq").toURI());
        File sampleR2 = new File(ClassLoader.getSystemResource("sample_r2.fastq").toURI());
        PSequencingDataReader reader = new PFastqReader(sampleR1, sampleR2, QualityFormat.Phred33, CompressionType.None,
                new Casava18InfoProvider(), true, false);
        PSequencingRead read;
        int count = 0;
        while ((read = reader.take()) != null) {
            ++count;
        }
        Assert.assertEquals(10, count);
    }
}
