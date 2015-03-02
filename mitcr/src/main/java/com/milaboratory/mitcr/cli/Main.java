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

import cc.redberry.pipe.OutputPort;
import cc.redberry.pipe.util.CountLimitingOutputPort;
import com.milaboratory.core.clone.CloneSetClustered;
import com.milaboratory.core.io.CloneSetIO;
import com.milaboratory.core.segment.DefaultSegmentLibrary;
import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.SegmentLibrary;
import com.milaboratory.core.segment.Species;
import com.milaboratory.core.sequence.quality.QualityFormat;
import com.milaboratory.core.sequencing.io.fastq.SFastqReader;
import com.milaboratory.core.sequencing.read.SSequencingRead;
import com.milaboratory.mitcr.clonegenerator.AccumulatorType;
import com.milaboratory.mitcr.clonegenerator.BasicCloneGeneratorParameters;
import com.milaboratory.mitcr.clonegenerator.LQFilteringOffCloneGeneratorParameters;
import com.milaboratory.mitcr.clonegenerator.LQMappingCloneGeneratorParameters;
import com.milaboratory.mitcr.clsexport.ClsExporter;
import com.milaboratory.mitcr.clusterization.CloneClusterizationType;
import com.milaboratory.mitcr.pipeline.FullPipeline;
import com.milaboratory.mitcr.pipeline.Parameters;
import com.milaboratory.mitcr.qualitystrategy.DummyQualityInterpretationStrategy;
import com.milaboratory.mitcr.qualitystrategy.IlluminaQualityInterpretationStrategy;
import com.milaboratory.mitcr.statistics.AnalysisStatisticsAggregator;
import com.milaboratory.util.CompressionType;
import com.milaboratory.util.SmartProgressReporter;
import com.milaboratory.util.TablePrintStreamAdapter;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.err;

public class Main {
    private static final Options options = new Options();
    private static final String PARAMETERS_SET_OPTION = "pset",
            EXPORT_OPTION = "export",
            SPECIES_OPTION = "species",
            GENE_OPTION = "gene",
            ERROR_CORECTION_LEVEL_OPTION = "ec",
            LQ_OPTION = "lq",
            QUALITY_THRESHOLD_OPTION = "quality",
            CLUSTERIZATION_OPTION = "pcrec",
            LIMIT_OPTION = "limit",
            REPORT_OPTION = "report",
            REPORTING_LEVEL_OPTION = "level",
            PHRED33_OPTION = "phred33",
            PHRED64_OPTION = "phred64",
            INCLUDE_CYS_PHE_OPTION = "cysphe",
            PRINT_DEBUG_OPTION = "debug",
            PRINT_VERSION_OPTION = "v",
            PRINT_HELP_OPTION = "h",
            THREADS_OPTION = "t",
            AVERAGE_QUALITY_OPTION = "average",
            COMPRESSED_OPTION = "compressed";
    private static final Map<String, Integer> orderingMap = new HashMap<>();

    public static void main(String[] args) {
        int o = 0;

        BuildInformation buildInformation = BuildInformationProvider.get();

        final boolean isProduction = "default".equals(buildInformation.scmBranch); // buildInformation.version != null && buildInformation.version.lastIndexOf("SNAPSHOT") < 0;

        orderingMap.put(PARAMETERS_SET_OPTION, o++);
        orderingMap.put(SPECIES_OPTION, o++);
        orderingMap.put(GENE_OPTION, o++);
        orderingMap.put(ERROR_CORECTION_LEVEL_OPTION, o++);
        orderingMap.put(QUALITY_THRESHOLD_OPTION, o++);
        orderingMap.put(AVERAGE_QUALITY_OPTION, o++);
        orderingMap.put(LQ_OPTION, o++);
        orderingMap.put(CLUSTERIZATION_OPTION, o++);
        orderingMap.put(INCLUDE_CYS_PHE_OPTION, o++);
        orderingMap.put(LIMIT_OPTION, o++);
        orderingMap.put(EXPORT_OPTION, o++);
        orderingMap.put(REPORT_OPTION, o++);
        orderingMap.put(REPORTING_LEVEL_OPTION, o++);
        orderingMap.put(PHRED33_OPTION, o++);
        orderingMap.put(PHRED64_OPTION, o++);
        orderingMap.put(THREADS_OPTION, o++);
        orderingMap.put(COMPRESSED_OPTION, o++);
        orderingMap.put(PRINT_HELP_OPTION, o++);
        orderingMap.put(PRINT_VERSION_OPTION, o++);
        orderingMap.put(PRINT_DEBUG_OPTION, o++);

        options.addOption(OptionBuilder.withArgName("preset name")
                .hasArg().withDescription("preset of pipeline parameters to use")
                .create(PARAMETERS_SET_OPTION));

        options.addOption(OptionBuilder.withArgName("species")
                .hasArg().withDescription("overrides species ['hs' for Homo sapiens, 'mm' for us Mus musculus] " +
                        "(default for built-in presets is 'hs')")
                .create(SPECIES_OPTION));

        options.addOption(OptionBuilder.withArgName("gene")
                .hasArg().withDescription("overrides gene: TRB or TRA (default value for built-in parameter sets is TRB)")
                .create(GENE_OPTION));

        options.addOption(OptionBuilder.withArgName("0|1|2")
                .hasArg().withDescription("overrides error correction level (0 = don't correct errors, 1 = correct sequenecing " +
                        "errors only (see -" + QUALITY_THRESHOLD_OPTION + " and -" + LQ_OPTION + " options for details), " +
                        "2 = also correct PCR errors (see -" + CLUSTERIZATION_OPTION + " option)")
                .create(ERROR_CORECTION_LEVEL_OPTION));


        options.addOption(OptionBuilder.withArgName("value")
                .hasArg().withDescription("overrides quality threshold value for segment alignment and bad quality sequences " +
                        "correction algorithms. 0 tells the program not to process quality information. (default is 25)")
                .create(QUALITY_THRESHOLD_OPTION));

        if (!isProduction)
            options.addOption(OptionBuilder.hasArg(false).withDescription("use this option to output average instead of " +
                    "maximal, quality for CDR3 nucleotide sequences. (Experimental option, use with caution.)")
                    .create(AVERAGE_QUALITY_OPTION));

        options.addOption(OptionBuilder.withArgName("map | drop").hasArg()
                .withDescription("overrides low quality CDR3s processing strategy (drop = filter off, " +
                        "map = map onto clonotypes created from the high quality CDR3s). This option makes no difference if " +
                        "quality threshold (-" + QUALITY_THRESHOLD_OPTION + " option) is set to 0, or error correction " +
                        "level (-" + ERROR_CORECTION_LEVEL_OPTION + ") is 0.")
                .create(LQ_OPTION));

        options.addOption(OptionBuilder.withArgName("smd | ete").hasArg()
                .withDescription("overrides the PCR error correction algorithm: smd = \"save my diversity\", " +
                        "ete = \"eliminate these errors\". Default value for built-in parameters is ete.")
                .create(CLUSTERIZATION_OPTION));


        options.addOption(OptionBuilder.withArgName("0|1")
                .hasArg().withDescription("overrides weather include bounding Cys & Phe into CDR3 sequence")
                .create(INCLUDE_CYS_PHE_OPTION));

        options.addOption(OptionBuilder.withArgName("# of reads")
                .hasArg().withDescription("limits the number of input sequencing reads, use this parameter to " +
                        "normalize several datasets or to have a glance at the data")
                .create(LIMIT_OPTION));

        options.addOption(OptionBuilder.withArgName("new name")
                .hasArg().withDescription("use this option to export presets to a local xml files")
                .create(EXPORT_OPTION));

        options.addOption(OptionBuilder.withArgName("file name")
                .hasArg().withDescription("use this option to write analysis report (summary) to file")
                .create(REPORT_OPTION));

        options.addOption(OptionBuilder.withArgName("1|2|3")
                .hasArg(true).withDescription("output detalization level (1 = simple, 2 = medium, 3 = full, this format " +
                        "could be deserialized using mitcr API). Affects only tab-delimited output. Default value is 3.")
                .create(REPORTING_LEVEL_OPTION));

        options.addOption(OptionBuilder
                .hasArg(false).withDescription("add this option if input file is in old illumina format with 64 byte offset for quality " +
                        "string (MiTCR will try to automatically detect file format if one of the \"-phredXX\" options is not provided)")
                .create(PHRED64_OPTION));

        options.addOption(OptionBuilder
                .hasArg(false).withDescription("add this option if input file is in Phred+33 format for quality values " +
                        "(MiTCR will try to automatically detect file format if one of the \"-phredXX\" options is not provided)")
                .create(PHRED33_OPTION));

        options.addOption(OptionBuilder.withArgName("threads")
                .hasArg().withDescription("specifies the number of CDR3 extraction threads (default = number of available CPU cores)")
                .create(THREADS_OPTION));

        if (!isProduction)
            options.addOption(OptionBuilder.hasArg(false).withDescription("use compressed data structures for storing individual " +
                    "clone segments statistics (from which arises the clone segment information). This option reduces required " +
                    "amount of memory, but introduces small stochastic errors into the algorithm which determines clone " +
                    "segments. (Experimental option, use with caution.)")
                    .create(COMPRESSED_OPTION));

        options.addOption(OptionBuilder.hasArg(false).withDescription("print this message")
                .create(PRINT_HELP_OPTION));

        options.addOption(OptionBuilder
                .hasArg(false).withDescription("print version information")
                .create(PRINT_VERSION_OPTION));

        options.addOption(OptionBuilder
                .hasArg(false).withDescription("print additional information about analysis process")
                .create(PRINT_DEBUG_OPTION));


        PosixParser parser = new PosixParser();

        try {
            long input_limit = -1;
            int threads = Runtime.getRuntime().availableProcessors();
            int reporting_level = 3;
            int ec_level = 2;

            CommandLine cl = parser.parse(options, args, true);
            if (cl.hasOption(PRINT_HELP_OPTION)) {
                printHelp();
                return;
            }

            boolean averageQuality = cl.hasOption(AVERAGE_QUALITY_OPTION), compressedAggregators = cl.hasOption(COMPRESSED_OPTION);

            if (cl.hasOption(PRINT_VERSION_OPTION)) {
                System.out.println("MiTCR by MiLaboratory, version: " + buildInformation.version);
                System.out.println("Branch: " + buildInformation.scmBranch);
                System.out.println("Built: " + buildInformation.buildDate + ", " + buildInformation.jdk + " JDK, " +
                        "build machine: " + buildInformation.builtBy);
                System.out.println("SCM changeset: " + buildInformation.scmChangeset + " (" + buildInformation.scmDate.replace("\"", "") + ")");
                return;
            }

            //Normal execution

            String paramName = cl.getOptionValue(PARAMETERS_SET_OPTION);

            if (paramName == null) {
                err.println("No parameters set is specified.");
                return;
            }


            Parameters params = ParametersIO.getParameters(paramName);

            if (params == null) {
                err.println("No parameters set found with name '" + paramName + "'.");
                return;
            }

            String value;

            if ((value = cl.getOptionValue(THREADS_OPTION)) != null)
                threads = Integer.decode(value);

            if ((value = cl.getOptionValue(REPORTING_LEVEL_OPTION)) != null)
                reporting_level = Integer.decode(value);

            if ((value = cl.getOptionValue(LIMIT_OPTION)) != null)
                input_limit = Long.decode(value);

            if ((value = cl.getOptionValue(GENE_OPTION)) != null)
                params.setGene(Gene.fromXML(value));

            if ((value = cl.getOptionValue(SPECIES_OPTION)) != null)
                params.setSpecies(Species.getFromShortName(value));


            if ((value = cl.getOptionValue(INCLUDE_CYS_PHE_OPTION)) != null) {
                if (value.equals("1"))
                    params.getCDR3ExtractorParameters().setIncludeCysPhe(true);
                else if (value.equals("0"))
                    params.getCDR3ExtractorParameters().setIncludeCysPhe(false);
                else {
                    err.println("Illegal value for -" + INCLUDE_CYS_PHE_OPTION + " parameter.");
                    return;
                }
            }

            if ((value = cl.getOptionValue(ERROR_CORECTION_LEVEL_OPTION)) != null) {
                int v = Integer.decode(value);
                ec_level = v;
                if (v == 0) {
                    params.setCloneGeneratorParameters(new BasicCloneGeneratorParameters());
                    params.setClusterizationType(CloneClusterizationType.None);
                } else if (v == 1) {
                    params.setCloneGeneratorParameters(new LQMappingCloneGeneratorParameters());
                    params.setClusterizationType(CloneClusterizationType.None);
                } else if (v == 2) {
                    params.setCloneGeneratorParameters(new LQMappingCloneGeneratorParameters());
                    params.setClusterizationType(CloneClusterizationType.OneMismatch, .1f);
                } else
                    throw new RuntimeException("This (" + v +
                            ") error correction level is not supported.");
            }


            if ((value = cl.getOptionValue(QUALITY_THRESHOLD_OPTION)) != null) {
                int v = Integer.decode(value);
                if (v == 0)
                    params.setQualityInterpretationStrategy(new DummyQualityInterpretationStrategy());
                else
                    params.setQualityInterpretationStrategy(new IlluminaQualityInterpretationStrategy((byte) v));
            }

            if ((value = cl.getOptionValue(LQ_OPTION)) != null)
                if (ec_level > 0)
                    switch (value) {
                        case "map":
                            params.setCloneGeneratorParameters(new LQMappingCloneGeneratorParameters(
                                    ((BasicCloneGeneratorParameters) params.getCloneGeneratorParameters()).getSegmentInformationAggregationFactor(),
                                    3, true));
                            break;
                        case "drop":
                            params.setCloneGeneratorParameters(new LQFilteringOffCloneGeneratorParameters(
                                    ((BasicCloneGeneratorParameters) params.getCloneGeneratorParameters()).getSegmentInformationAggregationFactor()));
                            break;
                        default:
                            throw new RuntimeException("Wrong value for -" + LQ_OPTION + " option.");
                    }


            if ((value = cl.getOptionValue(CLUSTERIZATION_OPTION)) != null)
                if (ec_level > 1) // == 2
                    switch (value) {
                        case "smd":
                            params.setClusterizationType(CloneClusterizationType.V2D1J2T3Explicit);
                            break;
                        case "ete":
                            params.setClusterizationType(CloneClusterizationType.OneMismatch);
                            break;
                        default:
                            throw new RuntimeException("Wrong value for -" + CLUSTERIZATION_OPTION + " option.");
                    }

            ((BasicCloneGeneratorParameters) params.getCloneGeneratorParameters()).setAccumulatorType(AccumulatorType.get(compressedAggregators, averageQuality));

            if ((value = cl.getOptionValue(EXPORT_OPTION)) != null) {
                //Exporting parameters
                ParametersIO.exportParameters(params, value);
                return;
            }

            String[] offArgs = cl.getArgs();

            if (offArgs.length == 0) {
                err.println("Input file not specified.");
                return;
            } else if (offArgs.length == 1) {
                err.println("Output file not specified.");
                return;
            } else if (offArgs.length > 2) {
                err.println("Unrecognized argument.");
                return;
            }

            String inputFileName = offArgs[0];
            String outputFileName = offArgs[1];


            File input = new File(inputFileName);

            if (!input.exists()) {
                err.println("Input file not found.");
                return;
            }

            //TODO This also done inside SFastqReader constructor
            CompressionType compressionType = CompressionType.None;
            if (inputFileName.endsWith(".gz"))
                compressionType = CompressionType.GZIP;

            QualityFormat format = null; // If variable remains null file format will be detected automatically
            if (cl.hasOption(PHRED33_OPTION))
                format = QualityFormat.Phred33;
            if (cl.hasOption(PHRED64_OPTION))
                if (format == null)
                    format = QualityFormat.Phred64;
                else {
                    err.println("Options: -" + PHRED33_OPTION + " and -" + PHRED64_OPTION + " are mutually exclusive");
                    return;
                }


            SFastqReader reads = format == null ?
                    new SFastqReader(input, compressionType) :
                    new SFastqReader(input, format, compressionType);

            OutputPort<SSequencingRead> inputToPipeline = reads;
            if (input_limit >= 0)
                inputToPipeline = new CountLimitingOutputPort<>(inputToPipeline, input_limit);

            SegmentLibrary library = DefaultSegmentLibrary.load();

            AnalysisStatisticsAggregator statisticsAggregator = new AnalysisStatisticsAggregator();

            FullPipeline pipeline = new FullPipeline(inputToPipeline, params, false, library);
            pipeline.setThreads(threads);
            pipeline.setAnalysisListener(statisticsAggregator);

            new Thread(new SmartProgressReporter(pipeline, err)).start(); // Printing status to the standard error stream

            pipeline.run();

            if (cl.hasOption(PRINT_DEBUG_OPTION)) {
                err.println("Memory = " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
                err.println("Clusterization: " + pipeline.getQC().getReadsClusterized() + "% of reads, " + pipeline.getQC().getClonesClusterized() + " % clones");
            }

            CloneSetClustered cloneSet = pipeline.getResult();

            if ((value = cl.getOptionValue(REPORT_OPTION)) != null) {
                File file = new File(value);
                TablePrintStreamAdapter table;
                if (file.exists())
                    table = new TablePrintStreamAdapter(new FileOutputStream(file, true));
                else {
                    table = new TablePrintStreamAdapter(file);
                    ReportExporter.printHeader(table);
                }
                //CloneSetQualityControl qc = new CloneSetQualityControl(library, params.getSpecies(), params.getGene(), cloneSet);
                ReportExporter.printRow(table, inputFileName, outputFileName, pipeline.getQC(), statisticsAggregator);
                table.close();
            }

            if (outputFileName.endsWith(".cls"))
                ClsExporter.export(pipeline, outputFileName.replace(".cls", "") + " " + new Date().toString(), input.getName(), outputFileName);
            else {
                //Dry run
                if (outputFileName.startsWith("-"))
                    return;

                ExportDetalizationLevel detalization = ExportDetalizationLevel.fromLevel(reporting_level);

                CompressionType compressionType1 = CompressionType.None;
                if (outputFileName.endsWith(".gz"))
                    compressionType1 = CompressionType.GZIP;
                CloneSetIO.exportCloneSet(outputFileName, cloneSet, detalization, params, input.getAbsolutePath(), compressionType1);
            }
        } catch (ParseException | RuntimeException | IOException e) {
            err.println("Error occurred in the analysis pipeline.");
            err.println();
            e.printStackTrace();
            //printHelp();
        }
    }

    public static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(new Comparator<Option>() {
            @Override
            public int compare(Option o1, Option o2) {
                return Integer.compare(orderingMap.get(o1.getOpt()),
                        orderingMap.get(o2.getOpt()));
            }
        });
        final String executable = System.getProperty("executable", "java -jar mitcr.jar");
        err.println("usage: " + executable + " -pset <preset name> [options] input_file output_file.cls");
        err.println("       " + executable + " -pset <preset name> [options] input_file output_file.txt");
        err.println("       " + executable + " -pset <preset name> [options] -export newPresetName");
        err.println();

        formatter.printOptions(new PrintWriter(err, true), 85, options, 2, 3);
        err.println();
    }
}
