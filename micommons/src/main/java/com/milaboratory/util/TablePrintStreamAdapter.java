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
package com.milaboratory.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;

public class TablePrintStreamAdapter implements AutoCloseable {
    private PrintStream innerPrintStream;
    private boolean rowStarted = false;

    public TablePrintStreamAdapter(OutputStream stream) {
        this.innerPrintStream = new PrintStream(stream);
    }

    public TablePrintStreamAdapter(PrintStream innerPrintStream) {
        this.innerPrintStream = innerPrintStream;
    }

    public TablePrintStreamAdapter(String fileName) throws FileNotFoundException {
        this(new PrintStream(fileName));
    }

    public TablePrintStreamAdapter(File file) throws FileNotFoundException {
        this(new PrintStream(file));
    }

    public void row(String... cells) {
        cells(cells);
        newRow();
    }

    public void row(Object... cells) {
        cells(cells);
        newRow();
    }

    public void cells(Object... cells) {
        for (Object cell : cells)
            cell(cell);
    }

    public void cells(String... cells) {
        for (String cell : cells)
            cell(cell);
    }

    public void cellsFromArray(Object cells) {
        if (!cells.getClass().isArray())
            throw new IllegalArgumentException();
        int length = Array.getLength(cells);
        for (int i = 0; i < length; ++i)
            cell(Array.get(cells, i));
    }

    public void cell(String cell) {
        if (rowStarted)
            getInnerPrintStream().print('\t');
        else
            rowStarted = true;
        getInnerPrintStream().print(cell);
    }

    public void cell(Object cell) {
        if (cell == null)
            cell("");
        else
            cell(cell.toString());
    }

    public void newRow() {
        rowStarted = false;
        getInnerPrintStream().println();
    }

    @Override
    public void close() {
        innerPrintStream.close();
    }

    public PrintStream getInnerPrintStream() {
        return innerPrintStream;
    }
}
