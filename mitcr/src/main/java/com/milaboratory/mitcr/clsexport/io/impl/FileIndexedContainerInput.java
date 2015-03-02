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
package com.milaboratory.mitcr.clsexport.io.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class FileIndexedContainerInput extends AbstractIndexedContainerInput {
    private final File file;

    public FileIndexedContainerInput(File file) throws IOException {
        super(new RandomAccessFile(file, "r"));
        this.file = file;
    }

    public FileIndexedContainerInput(File file, int signature) throws IOException {
        super(new RandomAccessFile(file, "r"), signature);
        this.file = file;
    }

    public FileIndexedContainerInput(String fileName) throws IOException {
        this(new File(fileName));
    }

    public FileIndexedContainerInput(String fileName, int signature) throws IOException {
        this(new File(fileName), signature);
    }

    @Override
    public void close() throws IOException {
        ((RandomAccessFile) input).close();
    }

    @Override
    protected void seek(long address) throws IOException {
        ((RandomAccessFile) input).seek(address);
    }

    public File getFile() {
        return file;
    }
}
