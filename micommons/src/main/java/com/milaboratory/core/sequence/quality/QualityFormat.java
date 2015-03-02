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
package com.milaboratory.core.sequence.quality;

/**
 * A base class to store various sequencing quality formats.
 *
 * See corresponding Wikipedia page for details: <a href="http://en.wikipedia.org/wiki/FASTQ_format">link</a>.
 */
public enum QualityFormat {
    /**
     * Phred quality values encoded with 33 value offset. <p>Allowed quality score values range is 0-50.</p>
     */
    Phred33((byte) 33, (byte) 0, (byte) 50, "phred33"),
    /**
     * Phred quality values encoded with 64 value offset. <p>Allowed quality score values range is 0-41.</p>
     */
    Phred64((byte) 64, (byte) 0, (byte) 41, "phred64");
    private byte offset, minValue, maxValue;
    private String name;

    private QualityFormat(byte offset, byte minValue, byte maxVaule, String name) {
        this.offset = offset;
        this.minValue = minValue;
        this.maxValue = maxVaule;
        this.name = name;
    }

    public byte getMinValue() {
        return minValue;
    }

    public byte getMaxValue() {
        return maxValue;
    }

    public byte getOffset() {
        return offset;
    }

    public static QualityFormat fromXML(String xml) {
        for (QualityFormat format : values())
            if (format.name.equalsIgnoreCase(xml))
                return format;
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
