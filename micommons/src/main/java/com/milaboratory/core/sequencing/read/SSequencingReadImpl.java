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
package com.milaboratory.core.sequencing.read;

import com.milaboratory.core.sequence.NucleotideSQPair;

/**
 * Main implementation of SSequencingRead.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class SSequencingReadImpl implements SSequencingRead {
    private final long id;
    private final String description;
    private final NucleotideSQPair pair;

    public SSequencingReadImpl(NucleotideSQPair pair) {
        this("", pair, 0);
    }

    public SSequencingReadImpl(String description, NucleotideSQPair pair, long id) {
        this.description = description;
        this.pair = pair;
        this.id = id;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public NucleotideSQPair getData() {
        return pair;
    }
}
