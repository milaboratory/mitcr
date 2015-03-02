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
package com.milaboratory.mitcr.cli;

import com.milaboratory.mitcr.statistics.AnalysisStatisticsAggregator;
import com.milaboratory.mitcr.statistics.CloneSetQualityControl;
import com.milaboratory.util.TablePrintStreamAdapter;

import java.text.DecimalFormat;

public class ReportExporter {
    public static void printHeader(TablePrintStreamAdapter table) {
        table.row("Input file", "Output file", "Total input sequences", "Bases Processed", "Total good sequences",
                "Sequencing information utilization, %", "Clones", "Out of frame clones, %", "Clones with stops, %",
                "Mapped LQ reads, % of good",
                "Clusterized clones, %", "V not determined clones, %", "J not determined clones, %",
                "V mappings found, % of total", "J mappings found, % of total", "ReClusterization ratio",
                "Reads per second", "Bases per second");
    }

    private static final DecimalFormat percentFormat = new DecimalFormat("##.##");

    public static void printRow(TablePrintStreamAdapter table, String inputName, String outputName,
                                CloneSetQualityControl qc, AnalysisStatisticsAggregator aggregator) {
        table.row(inputName, outputName, qc.getTotalReads(), aggregator == null ? "" : aggregator.getTotalBasesProcessed(),
                qc.getTotalGoodReads(),
                pc(qc.getTotalGoodReads(), qc.getTotalReads()),
                qc.getClones(),
                pc(qc.getOutOfFrameClones(), qc.getClones()),
                pc(qc.getWithStopsClones(), qc.getClones()),
                aggregator == null ? "" : pc(aggregator.getBadCDR3Assigned(), qc.getTotalGoodReads()),
                pc(qc.getClonesClusterized(),
                        qc.getClonesClusterized() + qc.getClones()),
                pc(qc.getVNotDeterminedClones(), qc.getClones()),
                pc(qc.getJNotDeterminedClones(), qc.getClones()),
                aggregator == null ? "" : pc(aggregator.getVMappingsFound(), qc.getTotalReads()),
                aggregator == null ? "" : pc(aggregator.getJMappingsFound(), qc.getTotalReads()),
                aggregator == null ? "" : aggregator.getReClusterizationRatioClones(),
                aggregator == null ? "" : percentFormat.format(aggregator.getReadsPerSecond()),
                aggregator == null ? "" : percentFormat.format(aggregator.getBasesPerSecond()));
    }

    private static String pc(long num, long denom) {
        return percentFormat.format(100.0 * num / denom);
    }
}
