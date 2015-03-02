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

import com.milaboratory.core.segment.Gene;
import com.milaboratory.core.segment.Species;
import com.milaboratory.mitcr.cdrextraction.CDR3ExtractorParameters;
import com.milaboratory.mitcr.cli.BuildInformationProvider;
import com.milaboratory.mitcr.clonegenerator.BasicCloneGeneratorParameters;
import com.milaboratory.mitcr.clonegenerator.CloneGeneratorParameters;
import com.milaboratory.mitcr.clonegenerator.CloneGeneratorParametersDeserializer;
import com.milaboratory.mitcr.clusterization.CloneClusterizationType;
import com.milaboratory.mitcr.qualitystrategy.DummyQualityInterpretationStrategy;
import com.milaboratory.mitcr.qualitystrategy.QualityInterpretationStrategy;
import com.milaboratory.mitcr.qualitystrategy.QualityInterpretationStrategyDeserializer;
import org.jdom.Element;

/**
 * A bulk set of parameters for miTCR pipeline
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class Parameters {
    public static final String FORMAT_VERSION = "1.0";
    private Gene gene;
    private Species species;
    private QualityInterpretationStrategy qualityInterpretationStrategy = new DummyQualityInterpretationStrategy(); //Do not take quality information into account while processing sequences
    private CDR3ExtractorParameters cdr3ExtractorParameters;
    private CloneGeneratorParameters cloneGeneratorParameters = new BasicCloneGeneratorParameters(); //Basic clone generator with default parameters.
    private CloneClusterizationType clusterizationType = CloneClusterizationType.None;
    private float maxClusterizationRatio = .2f;

    //Constructor with some defaults
    public Parameters() {
    }

    /**
     * Creates a set of parameters for miTCR pipeline
     *
     * @param gene                    the type of gene CDR3 belongs to
     * @param species                 parent species of the gene
     * @param cdr3ExtractorParameters parameter set for CDR3 extraction
     */
    public Parameters(Gene gene, Species species, CDR3ExtractorParameters cdr3ExtractorParameters) {
        this.gene = gene;
        this.species = species;
        this.cdr3ExtractorParameters = cdr3ExtractorParameters;
    }

    /**
     * Creates a set of parameters for miTCR pipeline
     *
     * @param gene                          the type of gene CDR3 belongs to
     * @param species                       parent species of the gene
     * @param qualityInterpretationStrategy a strategy to mark bad nucleotides in reads
     * @param cdr3ExtractorParameters       parameter set for CDR3 extraction
     * @param cloneGeneratorParameters      parameters for clone assembly from extracted CDR3s
     * @param clusterizationType            clone clusterization method
     * @param maxClusterizationRatio        threshold for clone count ratio for agglomeration (always < 1)
     */
    public Parameters(Gene gene, Species species, QualityInterpretationStrategy qualityInterpretationStrategy,
                      CDR3ExtractorParameters cdr3ExtractorParameters, CloneGeneratorParameters cloneGeneratorParameters,
                      CloneClusterizationType clusterizationType, float maxClusterizationRatio) {
        this.gene = gene;
        this.species = species;
        this.qualityInterpretationStrategy = qualityInterpretationStrategy;
        this.cdr3ExtractorParameters = cdr3ExtractorParameters;
        this.cloneGeneratorParameters = cloneGeneratorParameters;
        this.clusterizationType = clusterizationType;
        this.maxClusterizationRatio = maxClusterizationRatio;
    }

    public static Parameters fromXML(Element e) {
        //Extracting format information
        String format = e.getChildTextTrim("format");

        if (format == null) //Backward compatibility
            format = "1.0";

        //Checking for compatibility
        if (!format.equals(FORMAT_VERSION))
            throw new RuntimeException("Unsupported parameters format version.");

        Element clusterizer = e.getChild("clusterizer");
        return new Parameters(Gene.fromXML(e.getChildTextTrim("gene")),
                Species.valueOf(e.getChildTextTrim("species")),
                QualityInterpretationStrategyDeserializer.fromXML(e.getChild("qualityInterpretationStrategy")),
                CDR3ExtractorParameters.fromXML(e.getChild("cdr3Extractor")),
                CloneGeneratorParametersDeserializer.fromXML(e.getChild("cloneGenerator")),
                CloneClusterizationType.fromXML(clusterizer.getChildTextTrim("type")),
                Float.valueOf(clusterizer.getChildTextTrim("maxClusterizationRatio")));
    }

    public Element asXML() {
        return asXML(new Element("parameters"));
    }

    public Element asXML(Element e) {
        //Adding format information
        e.addContent(new Element("format").setText(FORMAT_VERSION));
        String version = BuildInformationProvider.getVersion();
        if (version != null)
            e.addContent(new Element("exportedByVersion").setText(version));
        e.addContent(new Element("gene").setText(gene.getXmlRepresentation()));
        e.addContent(new Element("species").setText(species.name()));
        e.addContent(qualityInterpretationStrategy.asXML(new Element("qualityInterpretationStrategy")));
        e.addContent(cdr3ExtractorParameters.toXML(new Element("cdr3Extractor")));
        e.addContent(cloneGeneratorParameters.asXML(new Element("cloneGenerator")));
        e.addContent(new Element("clusterizer")
                .addContent(new Element("type").setText(clusterizationType.getXmlRepresentation()))
                .addContent(new Element("maxClusterizationRatio").setText(String.valueOf(maxClusterizationRatio))));
        return e;
    }

    /**
     * Gets the type of gene CDR3 belongs to
     *
     * @return type of gene CDR3 belongs to
     */
    public Gene getGene() {
        return gene;
    }

    /**
     * Sets the type of gene CDR3 belongs to
     *
     * @param gene type of gene CDR3 belongs to
     */
    public void setGene(Gene gene) {
        this.gene = gene;
    }

    /**
     * Gets the species gene belongs to
     *
     * @return the species gene belongs to
     */
    public Species getSpecies() {
        return species;
    }

    /**
     * Sets the species gene belongs to
     *
     * @param species the species gene belongs to
     */
    public void setSpecies(Species species) {
        this.species = species;
    }

    /**
     * Gets the strategy to mark bad nucleotides in reads
     *
     * @return the strategy to mark bad nucleotides in reads
     */
    public QualityInterpretationStrategy getQualityInterpretationStrategy() {
        return qualityInterpretationStrategy;
    }

    /**
     * Sets the strategy to mark bad nucleotides in reads
     *
     * @param qualityInterpretationStrategy the strategy to mark bad nucleotides in reads
     */
    public void setQualityInterpretationStrategy(QualityInterpretationStrategy qualityInterpretationStrategy) {
        this.qualityInterpretationStrategy = qualityInterpretationStrategy;
    }

    /**
     * Gets the parameters of CDR3 extraction algorithm
     *
     * @return the parameters of CDR3 extraction algorithm
     */
    public CDR3ExtractorParameters getCDR3ExtractorParameters() {
        return cdr3ExtractorParameters;
    }

    /**
     * Sets  the parameters of CDR3 extraction algorithm
     *
     * @param cdr3ExtractorParameters the parameters of CDR3 extraction algorithm
     */
    public void setCDR3ExtractorParameters(CDR3ExtractorParameters cdr3ExtractorParameters) {
        this.cdr3ExtractorParameters = cdr3ExtractorParameters;
    }

    /**
     * Gets the parameters for clone assembly from extracted CDR3s
     *
     * @return the parameters for clone assembly from extracted CDR3s
     */
    public CloneGeneratorParameters getCloneGeneratorParameters() {
        return cloneGeneratorParameters;
    }

    /**
     * Sets  the parameters for clone assembly from extracted CDR3s
     *
     * @param cloneGeneratorParameters the parameters for clone assembly from extracted CDR3s
     */
    public void setCloneGeneratorParameters(CloneGeneratorParameters cloneGeneratorParameters) {
        this.cloneGeneratorParameters = cloneGeneratorParameters;
    }

    /**
     * Gets the clone clusterization method
     *
     * @return the clone clusterization method
     */
    public CloneClusterizationType getClusterizationType() {
        return clusterizationType;
    }

    /**
     * Sets the clone clusterization method and clusterization threshold
     *
     * @param clusterizationType     clone clusterization method
     * @param maxClusterizationRatio threshold for clone count ratio for agglomeration (always < 1)
     */
    public void setClusterizationType(CloneClusterizationType clusterizationType, float maxClusterizationRatio) {
        this.clusterizationType = clusterizationType;
        this.maxClusterizationRatio = maxClusterizationRatio;
    }

    /**
     * Sets the clone clusterization method
     *
     * @param clusterizationType lone clusterization method
     */
    public void setClusterizationType(CloneClusterizationType clusterizationType) {
        this.clusterizationType = clusterizationType;
    }

    /**
     * Gets the  threshold for clone count ratio for agglomeration
     *
     * @return threshold for clone count ratio for agglomeration
     */
    public float getMaxClusterizationRatio() {
        return maxClusterizationRatio;
    }

    /**
     * Sets the threshold for clone count ratio for agglomeration
     *
     * @param maxClusterizationRatio threshold for clone count ratio for agglomeration
     */
    public void setMaxClusterizationRatio(float maxClusterizationRatio) {
        this.maxClusterizationRatio = maxClusterizationRatio;
    }

    /**
     * Check if all parameters are defined
     *
     * @return true if all parameters are defined
     */
    boolean isAllParametersDefined() {
        return gene != null && species != null && qualityInterpretationStrategy != null &&
                cdr3ExtractorParameters != null && cloneGeneratorParameters != null && clusterizationType != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameters that = (Parameters) o;

        if (Float.compare(that.maxClusterizationRatio, maxClusterizationRatio) != 0) return false;
        if (!cdr3ExtractorParameters.equals(that.cdr3ExtractorParameters)) return false;
        if (!cloneGeneratorParameters.equals(that.cloneGeneratorParameters)) return false;
        if (clusterizationType != that.clusterizationType) return false;
        if (gene != that.gene) return false;
        if (!qualityInterpretationStrategy.equals(that.qualityInterpretationStrategy)) return false;
        if (species != that.species) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = gene.hashCode();
        result = 31 * result + species.hashCode();
        result = 31 * result + qualityInterpretationStrategy.hashCode();
        result = 31 * result + cdr3ExtractorParameters.hashCode();
        result = 31 * result + cloneGeneratorParameters.hashCode();
        result = 31 * result + clusterizationType.hashCode();
        result = 31 * result + (maxClusterizationRatio != +0.0f ? Float.floatToIntBits(maxClusterizationRatio) : 0);
        return result;
    }

    public Parameters clone() {
        return fromXML(this.asXML(new Element("a")));
    }
}
