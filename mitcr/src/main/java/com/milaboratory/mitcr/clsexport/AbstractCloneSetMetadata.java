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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbstractCloneSetMetadata {
    protected Map<String, Object> properties;
    protected DataSetMetadata dataSetMetadata;
    protected UUID uuid;
    protected final int format;
    protected int clonesOffset, clonesCount, clustersOffset, clustersCount;

    public AbstractCloneSetMetadata(Map<String, Object> properties, DataSetMetadata dataSetMetadata, UUID uuid, int clonesOffset, int clonesCount, int clustersOffset, int clustersCount,
                                    //public AbstractCloneSetMetadata(Map<String, Object> properties, UUID uuid, int clonesOffset, int clonesCount, int clustersOffset, int clustersCount,
                                    int format) {
        this.properties = properties;
        this.dataSetMetadata = dataSetMetadata;
        this.uuid = uuid;
        this.clonesOffset = clonesOffset;
        this.clonesCount = clonesCount;
        this.clustersOffset = clustersOffset;
        this.clustersCount = clustersCount;
        this.format = format;
    }

    public AbstractCloneSetMetadata(AbstractCloneSetMetadata metadata, int format) {
        properties = metadata.properties;
        dataSetMetadata = metadata.dataSetMetadata;
        uuid = metadata.uuid;
        this.format = format;
        this.clonesOffset = metadata.clonesOffset;
        this.clonesCount = metadata.clonesCount;
        this.clustersOffset = metadata.clustersOffset;
        this.clustersCount = metadata.clustersCount;
    }

    public DataSetMetadata getDataSetMetadata() {
        return dataSetMetadata;
    }

    public Map<String, Object> getProperties() {
        if (properties == null)
            properties = new HashMap<String, Object>();
        return properties;
    }

    /*public void setDataSetMetadata(DataSetMetadata dataSetMetadata) {
        this.dataSetMetadata = dataSetMetadata;
    }*/

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getClonesCount() {
        return clonesCount;
    }

    public void setClonesCount(int clonesCount) {
        this.clonesCount = clonesCount;
    }

    public int getClonesOffset() {
        return clonesOffset;
    }

    public void setClonesOffset(int clonesOffset) {
        this.clonesOffset = clonesOffset;
    }

    public int getClustersCount() {
        return clustersCount;
    }

    public void setClustersCount(int clustersCount) {
        this.clustersCount = clustersCount;
    }

    public int getClustersOffset() {
        return clustersOffset;
    }

    public void setClustersOffset(int clustersOffset) {
        this.clustersOffset = clustersOffset;
    }

    public int getSequencingLinksOffset() {
        return this.clustersOffset + this.clustersCount;
    }
}
