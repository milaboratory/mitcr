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

import com.milaboratory.mitcr.clsexport.io.impl.AbstractIndexedContainerInput;
import com.milaboratory.mitcr.clsexport.io.impl.ByteArrayIndexedContainerInput;
import com.milaboratory.mitcr.clsexport.io.impl.FileIndexedContainerInput;
import com.milaboratory.mitcr.clsexport.io.impl.FileIndexedContainerOutput;
import com.milaboratory.mitcr.clsexport.io.serializers.SegmentGroupContainerIO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Root container of all information about known immunological recombination segments.
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public class SegmentLibrary {
    public static final int FILE_SIGNATURE = 0xEDEBB; // +1 after format change (08.02.2012)
    private Map<SpeciesSegmentGroup, SegmentGroupContainer> groups = new HashMap<>();

    /**
     * Creates empty segments library.
     */
    public SegmentLibrary() {
    }

    /**
     * Registers a new {@link SegmentGroupContainer}, no overwriting
     *
     * @param group segment group
     */
    public void registerGroup(SegmentGroupContainer group) {
        if (groups.containsKey(group.getSSGTuple()))
            throw new RuntimeException("Group already registered.");
        groups.put(group.getSSGTuple(), group);
    }

    /**
     * Registers a new {@link SegmentGroupContainer}, overwrites if segment exits
     *
     * @param group segment group
     */
    public void registerGroupForced(SegmentGroupContainer group) {
        groups.put(group.getSSGTuple(), group);
    }

    /**
     * Gets a container with segments for a specified species and segment group
     *
     * @param ssg specified species and segment group
     * @return a container with queried segments
     */
    public SegmentGroupContainer getGroup(SpeciesSegmentGroup ssg) {
        final SegmentGroupContainer container = groups.get(ssg);
        if (container == null)
            throw new RuntimeException("No container in library for " + ssg);
        return container;
    }

    /**
     * Gets a container with segments for a specified species and segment group
     *
     * @param species species
     * @param g       segment group
     * @return a container with queried segments
     */
    public SegmentGroupContainer getGroup(Species species, SegmentGroup g) {
        return getGroup(new SpeciesSegmentGroup(species, g));
    }

    /**
     * Gets a container with segments for a specified species and segment group
     *
     * @param species species
     * @param gene    gene to specify a segment group, e.g. TRB
     * @param type    type of segment to specify a segment group, e.g. Variable
     * @return a container with queried segments
     */
    public SegmentGroupContainer getGroup(Species species, Gene gene, SegmentGroupType type) {
        final SegmentGroup sg = SegmentGroup.get(gene, type);
        if (sg == null)
            return null;
        return getGroup(species, sg);
    }

    /**
     * Gets all registered {@link SegmentGroupContainer}s
     */
    public Collection<SegmentGroupContainer> getAllRegisteredGroups() {
        return groups.values();
    }

    /**
     * Saves all registered {@link SegmentGroupContainer}s to external library
     */
    public void saveLibraryToFile(String fileName) throws IOException {
        FileIndexedContainerOutput container = new FileIndexedContainerOutput(fileName, FILE_SIGNATURE);
        container.registerWriter(SegmentGroupContainerIO.INSTANCE);
        for (SegmentGroupContainer group : groups.values())
            container.write(group);
        container.close();
    }

    /**
     * Deserialization of segment library
     */
    public static SegmentLibrary readFromStream(InputStream stream) throws IOException {
        AbstractIndexedContainerInput container = new ByteArrayIndexedContainerInput(stream, FILE_SIGNATURE);
        SegmentLibrary sl = readFromContainer(container);
        return sl;
    }

    /**
     * Deserialization of segment library
     */
    public static SegmentLibrary readFromFile(File file) throws IOException {
        FileIndexedContainerInput container = new FileIndexedContainerInput(file, FILE_SIGNATURE);
        SegmentLibrary sl = readFromContainer(container);
        container.close();
        return sl;
    }

    /**
     * Deserialization of segment library
     */
    public static SegmentLibrary readFromFile(String fileName) throws IOException {
        FileIndexedContainerInput container = new FileIndexedContainerInput(fileName, FILE_SIGNATURE);
        SegmentLibrary sl = readFromContainer(container);
        container.close();
        return sl;
    }

    private static SegmentLibrary readFromContainer(AbstractIndexedContainerInput container) throws IOException {
        container.registerReader(SegmentGroupContainerIO.INSTANCE);
        SegmentLibrary library = new SegmentLibrary();
        for (int i = 0; i < container.size(); ++i)
            library.registerGroup((SegmentGroupContainer) container.read(i));
        return library;
    }
}
