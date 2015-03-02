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
package com.milaboratory.mitcr.pipeline;

import com.milaboratory.core.clone.CloneSet;
import com.milaboratory.core.clone.CloneSetClustered;
import com.milaboratory.core.segment.SegmentLibrary;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractorListener;
import com.milaboratory.mitcr.clonegenerator.CloneGeneratorListener;
import com.milaboratory.mitcr.clusterization.ClusterizationListener;
import com.milaboratory.mitcr.vdjmapping.VJMapperListener;

public class DefaultAnalysisListener implements AnalysisListener {
    @Override
    public VJMapperListener getVListener() {
        return null;
    }

    @Override
    public VJMapperListener getJListener() {
        return null;
    }

    @Override
    public CDR3ExtractorListener getCDR3ExtractorListener() {
        return null;
    }

    @Override
    public CloneGeneratorListener getCloneGeneratorListener() {
        return null;
    }

    @Override
    public ClusterizationListener getClusterizationListener() {
        return null;
    }

    @Override
    public void analysisStarted(Parameters parameters, SegmentLibrary library, int threads) {
    }

    @Override
    public void cdr3ExtractionFinished() {
    }

    @Override
    public void beforeClusterization(CloneSet cloneSet) {
    }

    @Override
    public void afterClusterization(CloneSetClustered clusterizedCloneSet) {
    }
}
