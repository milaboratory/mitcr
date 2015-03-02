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

/*
 */
package com.milaboratory.core.sequencing.read;

import com.milaboratory.core.sequence.NucleotideSQPair;

/**
 * Main implementation of PSequencingRead.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class PSequencingReadImpl implements PSequencingRead {
    private final long id;
    private final String[] descriptions;
    private final NucleotideSQPair[] data;

    public PSequencingReadImpl(long id, String[] descriptions, NucleotideSQPair[] data) {
        this.id = id;
        this.descriptions = descriptions;
        this.data = data;
    }

    public PSequencingReadImpl(long id, String description0, String description1,
                               NucleotideSQPair data0, NucleotideSQPair data1) {
        this.id = id;
        this.descriptions = new String[2];
        this.data = new NucleotideSQPair[2];
        this.descriptions[0] = description0;
        this.descriptions[1] = description1;
        this.data[0] = data0;
        this.data[1] = data1;
    }

    public PSequencingReadImpl(SSequencingRead read0, SSequencingRead read1) {
        if (read0.id() != read1.id())
            throw new IllegalArgumentException();
        this.id = read0.id();
        this.descriptions = new String[2];
        this.data = new NucleotideSQPair[2];
        this.descriptions[0] = read0.getDescription();
        this.descriptions[1] = read1.getDescription();
        this.data[0] = read0.getData();
        this.data[1] = read1.getData();
    }

    @Override
    public SSequencingRead getSingleRead(int idInPair) {
        switch (idInPair) {
            case 0:
                return new FirstRead();
            case 1:
                return new SecondRead();
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public NucleotideSQPair getData(int idInPair) {
        if (!(idInPair == 0 || idInPair == 1))
            throw new IndexOutOfBoundsException();
        return data[idInPair];
    }

    @Override
    public long id() {
        return id;
    }

    private class FirstRead implements SSequencingRead {
        @Override
        public String getDescription() {
            return descriptions[0];
        }

        @Override
        public NucleotideSQPair getData() {
            return data[0];
        }

        @Override
        public long id() {
            return id;
        }
    }

    private class SecondRead implements SSequencingRead {
        @Override
        public String getDescription() {
            return descriptions[1];
        }

        @Override
        public NucleotideSQPair getData() {
            return data[1];
        }

        @Override
        public long id() {
            return id;
        }
    }
}
