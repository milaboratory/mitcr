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

/**
 * Main meta data container for data sets.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 */
public class DataSetMetadata {
    private UUID uuid;
    private int recordsOffset;
    private int recordsCount;
    private Map<String, Object> properties;

    public DataSetMetadata(UUID uuid, int recordsOffset, int recordsCount, Map<String, Object> properties) {
        this.uuid = uuid;
        this.recordsOffset = recordsOffset;
        this.recordsCount = recordsCount;
        this.properties = properties;
    }

    public int getRecordsCount() {
        return recordsCount;
    }

    public int getRecordsOffset() {
        return recordsOffset;
    }

    public static void setRecordsCount(DataSetMetadata metadataObject, int value) {
        metadataObject.recordsCount = value;
    }

    public Map<String, Object> getProperties() {
        Map<String, Object> ret = new HashMap<>(properties);
        return ret;
    }

    /*public MetadataContainer getPropertiesContainer() {
        return MetadataContainer.wrap(properties);
    }*/

    public UUID getUuid() {
        return uuid;
    }
}
