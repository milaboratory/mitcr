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
import com.milaboratory.core.sequencing.read.SSequencingRead;
import com.milaboratory.util.CompressionType;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

public class SFastqReaderTest {
    @Test
    public void test1() throws URISyntaxException, IOException {
        File sample = new File(ClassLoader.getSystemResource("sample_r1.fastq").toURI());
        File sampleGz = new File(ClassLoader.getSystemResource("sample_r1.fastq.gz").toURI());
        SFastqReader reader = new SFastqReader(sample, QualityFormat.Phred33, CompressionType.None);
        SFastqReader readerGz = new SFastqReader(sampleGz, QualityFormat.Phred33, CompressionType.GZIP);
        SSequencingRead read;
        while ((read = reader.take()) != null)
            Assert.assertEquals(read.getData().getSequence(), readerGz.take().getData().getSequence());

        Assert.assertEquals(null, readerGz.take());

        Assert.assertTrue(reader.isClosed());
        Assert.assertTrue(readerGz.isClosed());
    }

    @Test
    public void testGuess1() throws Exception {
        String[] files = {"solexa1.fastq.gz", "solexa2.fastq.gz", "solexa3.fastq.gz", "sample_r1.fastq.gz", "sample_r2.fastq.gz"};
        QualityFormat formats[] = {QualityFormat.Phred64, QualityFormat.Phred64, QualityFormat.Phred64,
                QualityFormat.Phred33, QualityFormat.Phred33};
        int reads[] = {10, 10, 10, 10, 10};

        for (int i = 0; i < files.length; ++i) {
            InputStream stream = ClassLoader.getSystemResource(files[i]).openStream();
            SFastqReader reader = new SFastqReader(stream, CompressionType.GZIP);
            Assert.assertEquals(formats[i], reader.getQualityFormat());
            int n = 0;
            while (reader.take() != null)
                ++n;
            Assert.assertEquals(n, reads[i]);
        }
    }
}
