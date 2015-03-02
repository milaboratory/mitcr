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
package com.milaboratory.mitcr.clsexport;

import com.milaboratory.core.clone.Clone;
import com.milaboratory.core.clone.CloneCluster;
import com.milaboratory.core.clone.CloneSetClustered;
import com.milaboratory.mitcr.clsexport.io.impl.FileIndexedContainerOutput;
import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.SegmentGroupType;
import com.milaboratory.core.segment.Species;
import com.milaboratory.mitcr.pipeline.FullPipeline;
import com.milaboratory.mitcr.statistics.CloneSetQualityControl;

import java.io.IOException;
import java.util.*;

/**
 * For backward compatibility.
 */
public class ClsExporter {
    public static final int SIGNATURE = 0xBEF1EAC7;

    public static void export(FullPipeline fullPipeline, String analysisNote, String dataSetName, String fileName) throws IOException {
        final CloneSetClustered cloneSet = fullPipeline.getResult();
        FileIndexedContainerOutput container = new FileIndexedContainerOutput(fileName, SIGNATURE);
        container.registerWriter(SCloneSetMetadataIO.INSTANCE);
        container.registerWriter(CloneIO.INSTANCE);
        container.registerWriter(CloneClusterDataIO.INSTANCE);
        //container.registerWriter(SequencingReadLinkSetIO.INSTANCE);


        Map<String, Object> clsProperties = new HashMap<>();
        clsProperties.put("analysisNote", analysisNote);
        clsProperties.put("clonesCount", cloneSet.getParentCloneSet().getClones().size());
        clsProperties.put("clustersCount", cloneSet.getClones().size());
        clsProperties.put("sequencesProcessed", (int) cloneSet.getTotalCount());

        long clusterizedSequences = 0;
        for (CloneCluster cluster : cloneSet.getClones()) {
            for (Clone clone : cluster.getChildClones())
                clusterizedSequences += clone.getCount();
        }
        clsProperties.put("sequencesClusterized", (int) clusterizedSequences);

        CloneSetQualityControl qc = fullPipeline.getQC();
        clsProperties.put("qual.outOfFrameSequences", (int) qc.getOutOfFrameSequences());
        clsProperties.put("qual.p1", (int) qc.getP1());
        clsProperties.put("qual.m1", (int) qc.getM1());
        clsProperties.put("qual.vNotDetermined", (int) qc.getVNotDeterminedSequences());
        clsProperties.put("qual.jNotDetermined", (int) qc.getJNotDeterminedSequences());
        clsProperties.put("extractor.extractedCDR3", (int) cloneSet.getTotalCount());

        Map<String, Object> dsProperties = new HashMap<>();
        dsProperties.put("name", dataSetName);

        DataSetMetadata dataSetMetadata = new DataSetMetadata(new UUID(0, 0), 0, (int) fullPipeline.getTotal(), dsProperties);

        final int coreClones = cloneSet.getParentCloneSet().getClones().size();
        final Species species = cloneSet.getSegmentGroupContainer(SegmentGroupType.Variable).getSpecies();
        final Gene gene = cloneSet.getSegmentGroupContainer(SegmentGroupType.Variable).getGroup().getGene();

        //for (CloneCluster cluster : cloneSet.getClones())
        //    coreClones += cluster.getChildClones().size() + 1;

        byte[][] md5s;
        if (cloneSet.getSegmentGroupContainer(SegmentGroupType.Diversity) != null) {
            md5s = new byte[3][];
            md5s[2] = cloneSet.getSegmentGroupContainer(SegmentGroupType.Diversity).getMD5();
            clsProperties.put("dConnected", true); //???
        } else
            md5s = new byte[2][];

        md5s[0] = cloneSet.getSegmentGroupContainer(SegmentGroupType.Variable).getMD5();
        md5s[1] = cloneSet.getSegmentGroupContainer(SegmentGroupType.Joining).getMD5();

        SCloneSetMetadata metadata = new SCloneSetMetadata(clsProperties, dataSetMetadata, UUID.randomUUID(),
                1, coreClones, coreClones + 1, cloneSet.getClones().size(), species, gene,
                md5s);

        container.write(metadata);

        List<CloneClusterData> cloneClusterData = new ArrayList<>(cloneSet.getClones().size());
        int pointer = 0;
        for (CloneCluster cluster : cloneSet.getClones()) {
            container.write(cluster.getCentralClone());
            for (Clone clone : cluster.getChildClones())
                container.write(clone);
            cloneClusterData.add(new CloneClusterData(pointer, cluster.getChildClones().size() + 1, (int) cluster.getCount()));
            pointer += cluster.getChildClones().size() + 1;
        }

        for (CloneClusterData cluster : cloneClusterData)
            container.write(cluster);

        container.close();
    }
}
