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

import com.milaboratory.core.clone.Clone;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult;

import java.util.concurrent.atomic.AtomicLong;

public class TestCGListener implements CloneGeneratorListener {
    private final AtomicLong clonesCreated = new AtomicLong(), readsAssignedTotal = new AtomicLong(),
            additionalReads = new AtomicLong(), readsDropped = new AtomicLong();
    long i;

    @Override
    public void newCloneCreated(Clone clone, CDR3ExtractionResult cdr3ExtractionResult) {
        clonesCreated.incrementAndGet();
    }

    @Override
    public void assignedToClone(Clone clone, CDR3ExtractionResult cdr3ExtractionResult, boolean isAdditional) {
        readsAssignedTotal.incrementAndGet();
        if (isAdditional)
            additionalReads.incrementAndGet();
    }

    @Override
    public void cdr3Dropped(CDR3ExtractionResult cdr3ExtractionResult) {
        readsDropped.incrementAndGet();
    }

    public long getClonesCreated() {
        return clonesCreated.get();
    }

    public long getReadsAssignedTotal() {
        return readsAssignedTotal.get();
    }

    public long getAdditionalReads() {
        return additionalReads.get();
    }

    public long getReadsDropped() {
        return readsDropped.get();
    }
}
