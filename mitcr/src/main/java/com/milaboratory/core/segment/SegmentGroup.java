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
package com.milaboratory.core.segment;

/**
 * Enum for main recombination segment groups. TRBV, TRBJ, etc...
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public enum SegmentGroup {
    TRAC(1, "TRAC", SegmentGroupType.Constant, Gene.TRA), TRAJ(2, "TRAJ", SegmentGroupType.Joining, Gene.TRA), TRAV(3, "TRAV", SegmentGroupType.Variable, Gene.TRA),

    TRBC(4, "TRBC", SegmentGroupType.Constant, Gene.TRB), TRBD(5, "TRBD", SegmentGroupType.Diversity, Gene.TRB),
    TRBJ(6, "TRBJ", SegmentGroupType.Joining, Gene.TRB), TRBV(7, "TRBV", SegmentGroupType.Variable, Gene.TRB),

    IGLC(8, "IGLC", SegmentGroupType.Constant, Gene.IGL), IGLJ(9, "IGLJ", SegmentGroupType.Joining, Gene.IGL), IGLV(10, "IGLV", SegmentGroupType.Variable, Gene.IGL),

    IGKC(11, "IGKC", SegmentGroupType.Constant, Gene.IGK), IGKJ(12, "IGKJ", SegmentGroupType.Joining, Gene.IGK), IGKV(13, "IGKV", SegmentGroupType.Variable, Gene.IGK),

    IGHC(14, "IGHC", SegmentGroupType.Constant, Gene.IGH), IGHD(15, "IGHD", SegmentGroupType.Diversity, Gene.IGH),
    IGHJ(16, "IGHJ", SegmentGroupType.Joining, Gene.IGH), IGHV(17, "IGHV", SegmentGroupType.Variable, Gene.IGH),

    TRGC(18, "TRGC", SegmentGroupType.Constant, Gene.TRG), TRGJ(19, "TRGJ", SegmentGroupType.Joining, Gene.TRG), TRGV(20, "TRGV", SegmentGroupType.Variable, Gene.TRG),

    TRDC(21, "TRDC", SegmentGroupType.Constant, Gene.TRD), TRDD(22, "TRDD", SegmentGroupType.Diversity, Gene.TRD),
    TRDJ(23, "TRDJ", SegmentGroupType.Joining, Gene.TRD), TRDV(24, "TRDV", SegmentGroupType.Variable, Gene.TRD);
    private static SegmentGroup[] grIndex = null;
    private final int index;
    private final String name;
    private final SegmentGroupType type;
    private final Gene gene;

    private SegmentGroup(int index, String name, SegmentGroupType type, Gene gene) {
        this.index = index;
        this.name = name;
        this.type = type;
        this.gene = gene;
    }

    public static SegmentGroup get(Gene g, SegmentGroupType type) {
        for (SegmentGroup sg : values())
            if (sg.gene == g && sg.type == type)
                return sg;
        return null;
    }

    public static SegmentGroup getFromName(String name) {
        name = name.toUpperCase();
        for (SegmentGroup group : values())
            if (group.name.equals(name))
                return group;
        return null;
    }

    public static SegmentGroup getSegmentGroupByIndex(int index) {
        if (grIndex == null) {
            int maxIndex = 0;
            for (SegmentGroup sg : SegmentGroup.values())
                if (maxIndex < sg.index)
                    maxIndex = sg.index;
            grIndex = new SegmentGroup[maxIndex + 1];
            for (SegmentGroup sg : SegmentGroup.values())
                grIndex[sg.index] = sg;
        }
        return grIndex[index];
    }

    public SegmentGroupType getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public Gene getGene() {
        return gene;
    }
}
