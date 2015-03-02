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
package com.milaboratory.core.sequencing.io.fasta;

import com.milaboratory.core.sequencing.WrongStructureException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A reader for a stream of {@link FastaItem}s
 */
public class FastaItemsReader {
    private InputStream innerStream;
    private String lastLine;
    private BufferedReader reader;
    private boolean outputLastLine = false;

    public FastaItemsReader(InputStream stream) {
        this.innerStream = stream;
        this.reader = new BufferedReader(new InputStreamReader(stream));
    }

    public FastaItem read() throws IOException {
        String line = readLine();
        if (line == null)
            return null;
        if (line.charAt(0) != '>')
            throw new WrongStructureException();
        String description = line.substring(1);
        StringBuilder sequence = new StringBuilder();
        while (true) {
            line = readLine();
            if (line == null)
                break;
            if (line.charAt(0) == '>') {
                outputLastLine = true;
                break;
            }
            line = line.trim();
            sequence.append(line);
        }
        return new FastaItem(description, sequence.toString().toCharArray());
    }

    private String readLine() throws IOException {
        if (outputLastLine) {
            outputLastLine = false;
            return lastLine;
        }
        String line = reader.readLine();
        if (line != null && line.isEmpty())
            return readLine();
        return lastLine = line;
    }

    public void close() throws IOException {
        reader.close();
    }
}