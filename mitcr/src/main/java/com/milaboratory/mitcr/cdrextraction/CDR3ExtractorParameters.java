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
package com.milaboratory.mitcr.cdrextraction;

import com.milaboratory.mitcr.vdjmapping.DSegmentMapperParameters;
import com.milaboratory.mitcr.vdjmapping.VJSegmentMapperParameters;
import org.jdom.Element;

/**
 * A class to hold parameters for CDR3 extraction algorithm
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class CDR3ExtractorParameters {
    private Strand strand;
    private int upperCDR3LengthThreshold = 100; //inclusive
    private int lowerCDR3LengthThreshold = 10; //inclusive
    private boolean includeCysPhe = true;
    private VJSegmentMapperParameters vMapperParameters, jMapperParameters;
    private DSegmentMapperParameters dMapperParameters;

    /**
     * A default constructor with no set parameters. For compatibility with groovy.
     */
    public CDR3ExtractorParameters() {
    }

    /**
     * Creates and object holding parameters for CDR3 extraction algorithm
     *
     * @param vMapperParameters parameters for V segment mapping algorithm
     * @param jMapperParameters parameters for J segment mapping algorithm
     * @param dMapperParameters parameters for D segment mapping algorithm
     * @param strand            mapping direction
     */
    public CDR3ExtractorParameters(VJSegmentMapperParameters vMapperParameters,
                                   VJSegmentMapperParameters jMapperParameters,
                                   DSegmentMapperParameters dMapperParameters,
                                   Strand strand, boolean includeCysPhe) {
        this.vMapperParameters = vMapperParameters;
        this.jMapperParameters = jMapperParameters;
        this.dMapperParameters = dMapperParameters;
        this.strand = strand;
        this.includeCysPhe = includeCysPhe;
    }

    /**
     * Creates and object holding parameters for CDR3 extraction algorithm
     *
     * @param vMapperParameters parameters for V segment mapping algorithm
     * @param jMapperParameters parameters for J segment mapping algorithm
     * @param dMapperParameters parameters for D segment mapping algorithm
     * @param strand            mapping direction
     */
    public CDR3ExtractorParameters(VJSegmentMapperParameters vMapperParameters,
                                   VJSegmentMapperParameters jMapperParameters,
                                   DSegmentMapperParameters dMapperParameters, Strand strand) {
        this(vMapperParameters, jMapperParameters, dMapperParameters, strand, true);
    }

    //TODO remove mike

    /**
     * Creates and object holding parameters for CDR3 extraction algorithm from an XML element
     *
     * @param e a corresponding XML element
     */
    /*public CDR3ExtractorParameters(Element element) {
        this.vMapperParameters = VJSegmentMapperParameters.fromXML(element.getChild("vMapper"));
        this.jMapperParameters = VJSegmentMapperParameters.fromXML(element.getChild("jMapper"));
        Element d = element.getChild("dMapper");
        if (d != null)
            this.dMapperParameters = new DSegmentMapperParameters(d);
    }*/
    public static CDR3ExtractorParameters fromXML(Element e) {
        return new CDR3ExtractorParameters(VJSegmentMapperParameters.fromXML(e.getChild("v")),
                VJSegmentMapperParameters.fromXML(e.getChild("j")),
                DSegmentMapperParameters.fromXML(e.getChild("d")),
                Strand.fromXML(e.getChildTextTrim("strand")),
                e.getChild("includeCysPhe") != null);
    }

    /**
     * Stores CDR3 extraction algorithm parameters to a XML element
     *
     * @param e a XML element to update
     * @return updated XML element
     */
    public Element toXML(Element e) {
        e.addContent(vMapperParameters.asXML(new Element("v")));
        e.addContent(jMapperParameters.asXML(new Element("j")));
        if (dMapperParameters != null)
            e.addContent(dMapperParameters.asXML(new Element("d")));

        e.addContent(new Element("upperCDR3LengthThreshold").setText(String.valueOf(upperCDR3LengthThreshold)));
        e.addContent(new Element("lowerCDR3LengthThreshold").setText(String.valueOf(lowerCDR3LengthThreshold)));
        e.addContent(new Element("strand").setText(strand.getXmlRepresentation()));

        if (includeCysPhe)
            e.addContent(new Element("includeCysPhe"));
        return e;
    }

    public boolean getIncludeCysPhe() {
        return includeCysPhe;
    }

    public void setIncludeCysPhe(boolean includeCysPhe) {
        this.includeCysPhe = includeCysPhe;
    }

    /**
     * Returns D mapper parameters
     *
     * @return D mapper parameters
     */
    public DSegmentMapperParameters getDMapperParameters() {
        return dMapperParameters;
    }

    /**
     * Returns J mapper parameters
     *
     * @return J mapper parameters
     */
    public VJSegmentMapperParameters getJMapperParameters() {
        return jMapperParameters;
    }

    /**
     * Returns V mapper parameters
     *
     * @return V mapper parameters
     */
    public VJSegmentMapperParameters getVMapperParameters() {
        return vMapperParameters;
    }

    /**
     * Gets upper limit on CDR3 length (inclusive)
     *
     * @return upper limit on CDR3 length (inclusive)
     */
    public int getUpperCDR3LengthThreshold() {
        return upperCDR3LengthThreshold;
    }

    /**
     * Sets upper limit on CDR3 length (inclusive)
     */
    public void setUpperCDR3LengthThreshold(int upperCDR3LengthThreshold) {
        this.upperCDR3LengthThreshold = upperCDR3LengthThreshold;
    }

    /**
     * Gets lower limit on CDR3 length (inclusive)
     *
     * @return lower limit on CDR3 length (inclusive)
     */
    public int getLowerCDR3LengthThreshold() {
        return lowerCDR3LengthThreshold;
    }

    /**
     * Sets lower limit on CDR3 length (inclusive)
     */
    public void setLowerCDR3LengthThreshold(int lowerCDR3LengthThreshold) {
        this.lowerCDR3LengthThreshold = lowerCDR3LengthThreshold;
    }

    /**
     * Gets mapping direction
     *
     * @return mapping direction
     */
    public Strand getStrand() {
        return strand;
    }

    /**
     * Sets mapping direction
     *
     * @param strand mapping direction
     */
    public void setStrand(Strand strand) {
        this.strand = strand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CDR3ExtractorParameters that = (CDR3ExtractorParameters) o;

        if (includeCysPhe != that.includeCysPhe) return false;
        if (lowerCDR3LengthThreshold != that.lowerCDR3LengthThreshold) return false;
        if (upperCDR3LengthThreshold != that.upperCDR3LengthThreshold) return false;
        if (dMapperParameters != null ? !dMapperParameters.equals(that.dMapperParameters) : that.dMapperParameters != null)
            return false;
        if (!jMapperParameters.equals(that.jMapperParameters)) return false;
        if (strand != that.strand) return false;
        if (!vMapperParameters.equals(that.vMapperParameters)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = strand.hashCode();
        result = 31 * result + upperCDR3LengthThreshold;
        result = 31 * result + lowerCDR3LengthThreshold;
        result = 31 * result + (includeCysPhe ? 1 : 0);
        result = 31 * result + vMapperParameters.hashCode();
        result = 31 * result + jMapperParameters.hashCode();
        result = 31 * result + (dMapperParameters != null ? dMapperParameters.hashCode() : 0);
        return result;
    }
}
