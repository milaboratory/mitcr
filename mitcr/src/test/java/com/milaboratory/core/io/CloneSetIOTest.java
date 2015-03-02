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
package com.milaboratory.core.io;

import com.milaboratory.core.clone.Clone;
import com.milaboratory.core.clone.CloneSet;
import com.milaboratory.mitcr.cli.ExportDetalizationLevel;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;

import static com.milaboratory.core.io.CloneSetIO.exportCloneSet;
import static com.milaboratory.core.io.CloneSetIO.importCloneSet;

public class CloneSetIOTest {
    @Test
    public void testExportImport() throws Exception {
        //Reading test cloneSet
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("test_cloneSet.txt.gz");
        GZIPInputStream unzippedStream = new GZIPInputStream(stream);
        CloneSet cloneSet = importCloneSet(unzippedStream);
        unzippedStream.close();

        //Creating set of clones
        HashSet<Clone> clones = new HashSet<>(cloneSet.getClones());

        //Writing and then reading the clone set
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        exportCloneSet(new PrintStream(os), cloneSet, ExportDetalizationLevel.Full);
        final byte[] result = os.toByteArray();
        final ByteArrayInputStream is = new ByteArrayInputStream(result);
        final CloneSet testCloneSet = importCloneSet(is);

        //Testing that deserialization result is the same as serialization
        Assert.assertEquals(cloneSet.getTotalCount(), testCloneSet.getTotalCount());
        Assert.assertTrue(clones.containsAll(testCloneSet.getClones()));
    }
}
