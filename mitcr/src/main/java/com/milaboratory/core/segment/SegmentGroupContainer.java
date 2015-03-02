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

import com.milaboratory.mitcr.clsexport.io.serializers.SegmentGroupContainerIO;
import com.milaboratory.util.BitArray;
import com.milaboratory.util.NullOutputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main information container for single segment group and species pair (e.g. TRBV of Homo sapiens, TRAJ of Mus
 * musculus, etc...).
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class SegmentGroupContainer {
    private byte[] md5 = null;
    private final SpeciesSegmentGroup ssgTuple;
    private final Segment[] segments;
    private final Allele[] alleles;
    private final List<Segment> segmentsList;
    private final List<Allele> allelesList;
    private final Map<String, Segment> nameToSegment;
    private final Map<String, Allele> nameToAllele;

    /**
     * Gets the container with segments of given species and type
     *
     * @param species  species, e.g. hsa
     * @param group    group type, e.g. TRBV
     * @param segments list of segments to store
     */
    public SegmentGroupContainer(Species species, SegmentGroup group, List<Segment> segments) {
        if (segments.isEmpty())
            throw new RuntimeException("Empty segments list.");
        this.ssgTuple = new SpeciesSegmentGroup(species, group);
        int maxAlleleIndex = 0;
        int maxSegmentIndex = 0;
        for (Segment segment : segments) {
            segment.assignType(this);
            if (segment.getIndex() > maxSegmentIndex)
                maxSegmentIndex = segment.getIndex();
            for (Allele allele : segment.getAlleles())
                if (maxAlleleIndex < allele.getIndex())
                    maxAlleleIndex = allele.getIndex();
        }
        this.segments = new Segment[maxSegmentIndex + 1];
        this.alleles = new Allele[maxAlleleIndex + 1];
        for (Segment segment : segments) {
            if (this.segments[segment.getIndex()] != null)
                throw new RuntimeException("Duplicate segment indexes");
            this.segments[segment.getIndex()] = segment;
            for (Allele allele : segment.getAlleles()) {
                if (this.alleles[allele.getIndex()] != null)
                    throw new RuntimeException("Duplicate allele indexes");
                this.alleles[allele.getIndex()] = allele;
            }
        }
        this.nameToSegment = new HashMap<>(this.segments.length);
        for (Segment segment : this.segments) {
            if (segment == null)
                throw new IllegalArgumentException("Some segment index is missing.");
            nameToSegment.put(segment.getSegmentName(), segment);
        }
        this.nameToAllele = new HashMap<>(this.alleles.length);
        for (Allele allele : this.alleles) {
            if (allele == null)
                throw new IllegalArgumentException("Some allele index is missing.");
            nameToAllele.put(allele.getFullName(), allele);
        }
        segmentsList = Collections.unmodifiableList(Arrays.asList(this.segments));
        allelesList = Collections.unmodifiableList(Arrays.asList(this.alleles));

    }

    /**
     * Creates segment families based on segment annotation. E.g. TRBV12-3 and TRBV12-1 are grouped in one family
     *
     * @param onlyMulti return only segment families with more than one member
     * @return an array of segment families
     */
    public SegmentFamily[] createFamilies(boolean onlyMulti) {
        Map<String, SegmentFamily> map = new HashMap<String, SegmentFamily>();
        String fName;
        int delPosition;
        for (Segment s : segments) {
            delPosition = s.getSegmentName().lastIndexOf('-');
            if (delPosition == -1) {
                if (!onlyMulti)
                    map.put(s.getSegmentName(), new SegmentFamily(s, this));
            } else {
                fName = s.getSegmentName().substring(0, delPosition) + "-*";
                SegmentFamily family = map.get(fName);
                if (family == null) {
                    family = new SegmentFamily(fName, this);
                    map.put(fName, family);
                }
                family.add(s);
            }
        }
        if (!onlyMulti)
            return map.values().toArray(new SegmentFamily[0]);
        else {
            int count = 0;
            for (SegmentFamily fam : map.values())
                if (fam.getSegments().size() > 1)
                    ++count;
            SegmentFamily[] ret = new SegmentFamily[count];
            for (SegmentFamily fam : map.values())
                if (fam.getSegments().size() > 1)
                    ret[--count] = fam;
            return ret;
        }
    }

    /**
     * Constructor for IO.
     *
     * @param md5 md5 sum to check
     */
    public SegmentGroupContainer(Species species, SegmentGroup group, List<Segment> segments, byte[] md5) {
        this(species, group, segments);

        byte[] realMD5 = calculateMD5();
        if (!Arrays.equals(realMD5, md5))
            throw new MD5ChecksumTestFail();

        this.md5 = md5;
    }

    private byte[] calculateMD5() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            DigestOutputStream stream = new DigestOutputStream(new NullOutputStream(), md);
            DataOutputStream dos = new DataOutputStream(stream);
            SegmentGroupContainerIO.INSTANCE.writeWithoutMD5(dos, this);
            return md.digest();
        } catch (IOException ex) {
            Logger.getLogger(SegmentGroupContainer.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Impossible");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SegmentGroupContainer.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("No MD5 algorithm");
        }
    }

    /**
     * Converts allele barcode to segments barcode.
     *
     * @param allelesBarcode alleles barcode to convert
     * @return segments barcode
     */
    public BitArray convertToSegments(BitArray allelesBarcode) {
        if (allelesBarcode.size() != alleles.length)
            throw new IllegalArgumentException();
        BitArray result = new BitArray(segments.length);
        for (int i = 0; i < alleles.length; ++i)
            if (allelesBarcode.get(i))
                result.set(alleles[i].getSegment().getIndex());
        return result;
    }

    /**
     * Query alleles by binary barcode with length of total number of alleles
     *
     * @param barcode allele barcode
     * @return queried alleles
     */
    public Allele[] getAlleles(BitArray barcode) {
        if (barcode.size() != alleles.length)
            throw new IllegalArgumentException();
        Allele[] result = new Allele[barcode.bitCount()];
        int n = 0;
        for (int i = 0; i < alleles.length; ++i)
            if (barcode.get(i))
                result[n++] = alleles[i];
        return result;
    }

    /**
     * Query segments by binary barcode with length of total number of segments
     *
     * @param barcode segment barcode
     * @return queried segment
     */
    public Segment[] getSegments(BitArray barcode) {
        if (barcode.size() != segments.length)
            throw new IllegalArgumentException();
        Segment[] result = new Segment[barcode.bitCount()];
        int n = 0;
        for (int i = 0; i < segments.length; ++i)
            if (barcode.get(i))
                result[n++] = segments[i];
        return result;
    }

    /**
     * Get segment by its internal index
     *
     * @param index internal index of segment
     * @return queried segment
     */
    public Segment getSegment(int index) {
        return segments[index];
    }

    /**
     * Get allele by its internal index
     *
     * @param index internal index of allele
     * @return queried allele
     */
    public Allele getAllele(int index) {
        return alleles[index];
    }

    /**
     * Check if the container contains segment with specified index
     *
     * @param index index of segment
     * @return true if contains, otherwise false
     */
    public boolean containsSegment(int index) {
        return index >= 0 && index < segments.length;
    }

    /**
     * Check if the container contains allele with specified index
     *
     * @param index index of allele
     * @return true if contains, otherwise false
     */
    public boolean containsAllele(int index) {
        return index >= 0 && index < alleles.length;
    }

    /**
     * Get segment by its name
     *
     * @param name name of segment
     * @return queried segment
     */
    public Segment getSegmentByName(String name) {
        return nameToSegment.get(name);
    }

    /**
     * Get allele by its name
     *
     * @param name name of allele
     * @return queried allele
     */
    public Allele getAlleleByName(String name) {
        return nameToAllele.get(name);
    }

    /**
     * Gets list of all segments in this container
     *
     * @return list of all segments in this container
     */
    public List<Segment> getSegmentsList() {
        return segmentsList;
    }

    /**
     * Gets the number of all segments in this container
     *
     * @return number of all segments in this container
     */
    public int getSegmentCount() {
        return segments.length;
    }

    /**
     * Gets the number of all alleles in this container
     *
     * @return number of all alleles in this container
     */
    public int getAllelesCount() {
        return alleles.length;
    }

    /**
     * Gets MD5 sum to compare different containers
     */
    public byte[] getMD5() {
        if (md5 == null)
            md5 = calculateMD5();
        return md5;
    }

    /**
     * Gets the segment group (e.g. TRBV) of this container
     *
     * @return segment group of this container
     */
    public SegmentGroup getGroup() {
        return ssgTuple.group;
    }

    /**
     * Gets the species (e.g. Homo Sapiens) of this contaienr
     *
     * @return the species of this contaienr
     */
    public Species getSpecies() {
        return ssgTuple.species;
    }

    /**
     * Tuple of segment group and species
     *
     * @return tuple of segment group and species
     */
    public SpeciesSegmentGroup getSSGTuple() {
        return ssgTuple;
    }

    /**
     * Gets list of all alleles in this container
     *
     * @return list of all alleles in this container
     */
    public List<Allele> getAllelesList() {
        return allelesList;
    }

    /**
     * Gets the id of container (segment group and species)
     */
    public String getStringId() {
        return ssgTuple.toString();
    }

    /**
     * Checks if the container has specified segment
     *
     * @param segment segment to check
     * @return true if segment is in the container, otherwise false
     */
    public boolean contains(Segment segment) {
        return segments[segment.getIndex()] != segment;
    }

    /**
     * Checks if the container has specified allele
     *
     * @param allele allele to check
     * @return true if allele is in the container, otherwise false
     */
    public boolean contains(Allele allele) {
        return alleles[allele.getIndex()] == allele;
    }
}
