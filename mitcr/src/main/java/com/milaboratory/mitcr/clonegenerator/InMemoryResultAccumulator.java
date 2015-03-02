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
package com.milaboratory.mitcr.clonegenerator;

import cc.redberry.pipe.OutputPort;
import cc.redberry.pipe.util.IteratorOutputPortAdapter;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryResultAccumulator implements ResultsAccumulator {
    private List<CDR3ExtractionResult> results = new ArrayList<>();
    private final AtomicInteger count = new AtomicInteger();

    @Override
    public synchronized void put(CDR3ExtractionResult result) {
        results.add(result);
        count.getAndIncrement();
    }

    /**
     * Not synchronized properly. Just for status monitoring.
     */
    @Override
    public int getCount() {
        return count.get();
    }

    @Override
    public synchronized OutputPort<CDR3ExtractionResult> getBack() {
        Iterable<CDR3ExtractionResult> it = results;
        results = new ArrayList<>();
        count.set(0);
        return new IteratorOutputPortAdapter<CDR3ExtractionResult>(it);
    }
}
