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
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractionResult;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractorListener;
import com.milaboratory.mitcr.clonegenerator.CloneGeneratorListener;
import com.milaboratory.mitcr.clusterization.ClusterizationListener;
import com.milaboratory.mitcr.vdjmapping.VJMapperListener;
import com.milaboratory.mitcr.vdjmapping.VJSegmentMappingResult;

import java.util.ArrayList;
import java.util.List;

public class AnalysisListenerCombiner implements AnalysisListener {
    AnalysisListener[] listeners;

    public AnalysisListenerCombiner(AnalysisListener... listeners) {
        this.listeners = listeners;
    }

    @Override
    public VJMapperListener getVListener() {
        List<VJMapperListener> ls = new ArrayList<>(listeners.length);
        VJMapperListener l;
        for (AnalysisListener listener : listeners)
            if ((l = listener.getVListener()) != null)
                ls.add(l);
        if (ls.isEmpty())
            return null;
        else if (ls.size() == 1)
            return ls.get(0);
        else
            return new VJMapperListenerCombiner(ls);
    }

    @Override
    public VJMapperListener getJListener() {
        List<VJMapperListener> ls = new ArrayList<>(listeners.length);
        VJMapperListener l;
        for (AnalysisListener listener : listeners)
            if ((l = listener.getJListener()) != null)
                ls.add(l);
        if (ls.isEmpty())
            return null;
        else if (ls.size() == 1)
            return ls.get(0);
        else
            return new VJMapperListenerCombiner(ls);
    }

    private static class VJMapperListenerCombiner implements VJMapperListener {
        VJMapperListener[] listeners;

        private VJMapperListenerCombiner(List<VJMapperListener> listeners) {
            this.listeners = listeners.toArray(new VJMapperListener[listeners.size()]);
        }

        @Override
        public void mappingFound(VJSegmentMappingResult result, Object source) {
            for (VJMapperListener listener : listeners)
                listener.mappingFound(result, source);
        }

        @Override
        public void mappingDropped(VJSegmentMappingResult result, Object source) {
            for (VJMapperListener listener : listeners)
                listener.mappingDropped(result, source);
        }

        @Override
        public void noMapping(Object source) {
            for (VJMapperListener listener : listeners)
                listener.noMapping(source);
        }
    }

    @Override
    public CDR3ExtractorListener getCDR3ExtractorListener() {
        List<CDR3ExtractorListener> ls = new ArrayList<>(listeners.length);
        CDR3ExtractorListener l;
        for (AnalysisListener listener : listeners)
            if ((l = listener.getCDR3ExtractorListener()) != null)
                ls.add(l);
        if (ls.isEmpty())
            return null;
        else if (ls.size() == 1)
            return ls.get(0);
        else
            return new CDR3ExtractorListenerCombiner(ls);
    }

    private static class CDR3ExtractorListenerCombiner implements CDR3ExtractorListener {
        CDR3ExtractorListener[] listeners;

        private CDR3ExtractorListenerCombiner(List<CDR3ExtractorListener> listeners) {
            this.listeners = listeners.toArray(new CDR3ExtractorListener[listeners.size()]);
        }

        @Override
        public void cdr3Extracted(CDR3ExtractionResult result, Object input) {
            for (CDR3ExtractorListener listener : listeners)
                listener.cdr3Extracted(result, input);
        }

        @Override
        public void cdr3NotExtracted(CDR3ExtractionResult result, Object input) {
            for (CDR3ExtractorListener listener : listeners)
                listener.cdr3NotExtracted(result, input);
        }
    }

    @Override
    public CloneGeneratorListener getCloneGeneratorListener() {
        List<CloneGeneratorListener> ls = new ArrayList<>(listeners.length);
        CloneGeneratorListener l;
        for (AnalysisListener listener : listeners)
            if ((l = listener.getCloneGeneratorListener()) != null)
                ls.add(l);
        if (ls.isEmpty())
            return null;
        else if (ls.size() == 1)
            return ls.get(0);
        else
            throw new UnsupportedOperationException();
    }

    @Override
    public ClusterizationListener getClusterizationListener() {
        List<ClusterizationListener> ls = new ArrayList<>(listeners.length);
        ClusterizationListener l;
        for (AnalysisListener listener : listeners)
            if ((l = listener.getClusterizationListener()) != null)
                ls.add(l);
        if (ls.isEmpty())
            return null;
        else if (ls.size() == 1)
            return ls.get(0);
        else
            throw new UnsupportedOperationException();
    }

    @Override
    public void analysisStarted(Parameters parameters, SegmentLibrary library, int threads) {
        for (AnalysisListener listener : listeners)
            listener.analysisStarted(parameters, library, threads);
    }

    @Override
    public void cdr3ExtractionFinished() {
        for (AnalysisListener listener : listeners)
            listener.cdr3ExtractionFinished();
    }

    @Override
    public void beforeClusterization(CloneSet cloneSet) {
        for (AnalysisListener listener : listeners)
            listener.beforeClusterization(cloneSet);
    }

    @Override
    public void afterClusterization(CloneSetClustered clusterizedCloneSet) {
        for (AnalysisListener listener : listeners)
            listener.afterClusterization(clusterizedCloneSet);
    }
}
