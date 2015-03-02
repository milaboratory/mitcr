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
package com.milaboratory.core.io;

import com.milaboratory.core.clone.Clone;
import com.milaboratory.core.clone.CloneImpl;
import com.milaboratory.core.clone.CloneSet;
import com.milaboratory.core.clone.CloneSetImpl;
import com.milaboratory.core.segment.*;
import com.milaboratory.core.sequence.NucleotideSQPair;
import com.milaboratory.core.sequence.nucleotide.NucleotideSequence;
import com.milaboratory.core.sequence.quality.QualityFormat;
import com.milaboratory.core.sequence.quality.SequenceQualityPhred;
import com.milaboratory.mitcr.cli.ExportDetalizationLevel;
import com.milaboratory.mitcr.cli.ParametersIO;
import com.milaboratory.mitcr.pipeline.Parameters;
import com.milaboratory.util.BitArray;
import com.milaboratory.util.CompressionType;
import com.milaboratory.util.TablePrintStreamAdapter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class CloneSetIO {
    public static final String CURRENT_FULL_MAGIC_PREFIX = "MiTCRFullExportV1.1";

    private CloneSetIO() {
    }

    public static void printHeader(TablePrintStreamAdapter table, ExportDetalizationLevel detalization) {
        switch (detalization) {
            case Full:
                table.row("Read count", "Percentage", "CDR3 nucleotide sequence", "CDR3 nucleotide quality", "Min quality",
                        "CDR3 amino acid sequence", "V alleles", "V segments", "J alleles", "J segments",
                        "D alleles", "D segments", "Last V nucleotide position ", "First D nucleotide position", "Last D nucleotide position",
                        "First J nucleotide position", "VD insertions", "DJ insertions", "Total insertions");
                return;
            case Medium:
                table.row("Read count", "Percentage", "CDR3 nucleotide sequence",
                        "CDR3 amino acid sequence", "V segments", "J segments",
                        "D segments", "Last V nucleotide position ", "First D nucleotide position", "Last D nucleotide position",
                        "First J nucleotide position", "VD insertions", "DJ insertions", "Total insertions");
                return;
            case Simple:
                table.row("Read count", "Percentage", "CDR3 nucleotide sequence",
                        "CDR3 amino acid sequence", "V segments", "J segments",
                        "D segments");
                return;
        }
    }

    public static void printClone(TablePrintStreamAdapter table, Clone clone, ExportDetalizationLevel detalization) {
        switch (detalization) {
            case Full:
                table.row(clone.getCount(), clone.getPart(), clone.getCDR3().getSequence(), clone.getCDR3().getQuality(),
                        clone.getCDR3().getQuality().minValue(),
                        clone.getCDR3AA(), clone.getVAlleles(), clone.getVSegments(), clone.getJAlleles(), clone.getJSegments(),
                        clone.getDAlleles(), clone.getDSegments(), clone.getVEnd(), clone.getDStart(), clone.getDEnd(),
                        clone.getJStart(), clone.insertionsVD(), clone.insertionsDJ(), clone.insertionsTotal());
                return;
            case Medium:
                table.row(clone.getCount(), clone.getPart(), clone.getCDR3().getSequence(),
                        clone.getCDR3AA(), clone.getVSegments(), clone.getJSegments(),
                        clone.getDSegments(), clone.getVEnd(), clone.getDStart(), clone.getDEnd(),
                        clone.getJStart(), clone.insertionsVD(), clone.insertionsDJ(), clone.insertionsTotal());
                return;
            case Simple:
                table.row(clone.getCount(), clone.getPart(), clone.getCDR3().getSequence(),
                        clone.getCDR3AA(), clone.getVSegments(), clone.getJSegments(),
                        clone.getDSegments());
        }
    }

    public static void exportCloneSet(TablePrintStreamAdapter table, CloneSet cloneSet,
                                      ExportDetalizationLevel detalization) {
        exportCloneSet(table, cloneSet, detalization, null, null);
    }

    public static void exportCloneSet(TablePrintStreamAdapter table, CloneSet cloneSet,
                                      ExportDetalizationLevel detalization, Parameters params, String inputAddress) {
        if (detalization == ExportDetalizationLevel.Full) {
            table.row(CURRENT_FULL_MAGIC_PREFIX, cloneSet.getSpecies().name(),
                    cloneSet.getGene().getXmlRepresentation(), inputAddress,
                    params == null ?
                            null : ParametersIO.exportParametersToString(params));
        }
        printHeader(table, detalization);
        for (Clone clone : cloneSet.getClones())
            printClone(table, clone, detalization);
    }

    public static void exportCloneSet(PrintStream stream, CloneSet cloneSet,
                                      ExportDetalizationLevel detalization) {
        exportCloneSet(stream, cloneSet, detalization, null, null);
    }

    public static void exportCloneSet(PrintStream stream, CloneSet cloneSet,
                                      ExportDetalizationLevel detalization, Parameters params, String inputAddress) {
        exportCloneSet(new TablePrintStreamAdapter(stream), cloneSet, detalization, params, inputAddress);
    }

    public static void exportCloneSet(String fileName, CloneSet cloneSet,
                                      ExportDetalizationLevel detalization, CompressionType ct) throws IOException {
        exportCloneSet(fileName, cloneSet, detalization, null, null, ct);
    }

    public static void exportCloneSet(String fileName, CloneSet cloneSet,
                                      ExportDetalizationLevel detalization, Parameters params, String inputAddress, CompressionType ct) throws IOException {
        try (PrintStream ps = new PrintStream(ct.createOutputStream(new FileOutputStream(fileName)))) {
            exportCloneSet(ps, cloneSet, detalization, params, inputAddress);
        }
    }

    //TODO fix

    /**
     * Reads clone set formatted as tab-delimited table using {@link com.milaboratory.core.io.CloneSetIO#exportCloneSet(String,
     * com.milaboratory.core.clone.CloneSet, ExportDetalizationLevel)} with detalization level equals to {@link
     * ExportDetalizationLevel#Full}.
     *
     * @param fileName file name
     * @return resulting clone set
     */
    public static CloneSet importCloneSet(String fileName) throws IOException {
        CompressionType ct = CompressionType.None;
        if (fileName.endsWith(".gz"))
            ct = CompressionType.GZIP;
        return importCloneSet(new FileInputStream(fileName), ct);
    }

    public static CloneSet importCloneSet(InputStream stream) throws IOException {
        return importCloneSet(stream, CompressionType.None);
    }

    public static CloneSet importCloneSet(InputStream stream, CompressionType ct) throws IOException {
        return importCloneSet(ct.createInputStream(stream), DefaultSegmentLibrary.load());
    }

    public static CloneSet importCloneSet(InputStream stream, SegmentLibrary library) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        String line = reader.readLine();

        //Checking MagicPrefix
        int format = -1;
        if (line.startsWith(CloneSetIO.CURRENT_FULL_MAGIC_PREFIX))
            format = 1;

        if (line.startsWith("MiTCRFullExportV1.0"))
            format = 0;

        if (format == -1)
            throw new IOException("Wrong file format.");

        String[] splitLine = line.split("\t");

        //Retrieving gene and species from header
        Species species;
        try {
            species = Species.valueOf(splitLine[1]);
        } catch (IllegalArgumentException iae) {
            throw new IOException("Wrong file format.");
        }
        Gene gene = Gene.fromXML(splitLine[2]);
        if (gene == null)
            throw new IOException("Wrong file format.");

        //Redundant check
        if ((splitLine = reader.readLine().split("\t")).length != 19)
            throw new IOException("Bad number of columns in header.");

        /*
            Format summary:

                0 clone.getCount(),
                1 clone.getPart(),
                2 clone.getCDR3().getSequence(),
                3 clone.getCDR3().getQuality(),
                4 clone.getCDR3().getQuality().minValue(),
                5 clone.getCDR3AA(),
                6 clone.getVAlleles(),
                7 clone.getVSegments(),
                8 clone.getJAlleles(),
                9 clone.getJSegments(),
                10 clone.getDAlleles(),
                11 clone.getDSegments(),
                12 clone.getVEnd(),
                13 clone.getDStart(),
                14 clone.getDEnd(),
                15 clone.getJStart(),
                16 clone.insertionsVD(),
                17 clone.insertionsDJ(),
                18 clone.insertionsTotal()
        */


        int id = 0;

        //SegmentLibrary library = DefaultSegmentLibrary.load();
        final SegmentGroupContainer vContainer = library.getGroup(species, gene, SegmentGroupType.Variable),
                jContainer = library.getGroup(species, gene, SegmentGroupType.Joining),
                dContainer = gene.hasDSegment() ? library.getGroup(species, gene, SegmentGroupType.Diversity) : null;

        List<Clone> clones = new ArrayList<>();
        CloneImpl clone;
        while ((line = reader.readLine()) != null) {
            splitLine = line.split("\t");

            //Retrieving reads count
            long count = Long.parseLong(splitLine[0]);

            //Retrieving segments for current clone
            BitArray vBarcode = new BitArray(vContainer.getAllelesCount());
            for (String vSegmentName : splitLine[6].split(","))
                vBarcode.set(vContainer.getAlleleByName(vSegmentName.trim()).getIndex());

            BitArray jBarcode = new BitArray(jContainer.getAllelesCount());
            for (String jSegmentName : splitLine[8].split(","))
                jBarcode.set(jContainer.getAlleleByName(jSegmentName.trim()).getIndex());

            BitArray dBarcode = null;
            if (dContainer != null) {
                dBarcode = new BitArray(dContainer.getAllelesCount());
                if (!splitLine[10].isEmpty())
                    for (String dSegment : splitLine[10].split(","))
                        dBarcode.set(dContainer.getAlleleByName(dSegment.trim()).getIndex());
            }
            BitArray[] segmentBarcodes = new BitArray[]{vBarcode, jBarcode, dBarcode};

            //Retrieving segments layout for current clone
            int[] segmentCoords;
            if (Integer.parseInt(splitLine[13]) > -1)
                segmentCoords = new int[]{Integer.parseInt(splitLine[12]), Integer.parseInt(splitLine[13]),
                        Integer.parseInt(splitLine[14]), Integer.parseInt(splitLine[15])};
            else
                segmentCoords = new int[]{Integer.parseInt(splitLine[12]), Integer.parseInt(splitLine[15])};

            clone = new CloneImpl(id++, new NucleotideSQPair(new NucleotideSequence(splitLine[2]),
                    new SequenceQualityPhred(splitLine[3], QualityFormat.Phred33)), count, segmentBarcodes, segmentCoords);
            clones.add(clone);
        }

        //Creating clone set
        return new CloneSetImpl<>(clones, gene, species, vContainer, jContainer, dContainer);
    }
}
