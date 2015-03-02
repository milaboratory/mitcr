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
package com.milaboratory.core.sequencing.io.fastq;

/**
 * Parses read metadata from Casava18 formatted header
 */
public class Casava18InfoProvider implements ReadInfoProvider {
    // HWUSI-EAS1814:40:1:4:9:1523:931 1:N:0:TGACCA
    @Override
    public ReadInfo getInfo(String description) {
        String[] split0 = description.split(" ");
        String[] split1 = split0[1].split(":");
        return new ReadInfoImpl(split0[0], (byte) (Byte.parseByte(split1[0], 10) - 1), split1[1].equals("Y"));
    }
}
