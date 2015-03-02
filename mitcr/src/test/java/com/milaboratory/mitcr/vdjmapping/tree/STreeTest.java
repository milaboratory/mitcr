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
package com.milaboratory.mitcr.vdjmapping.tree;

import org.junit.Ignore;

/**
 * @author Dima
 */
@Ignore
public class STreeTest {
    /**private static SegmentLibrary library;
     private static STree treeMismatch, treeDeletion;
     private final static SegmentGroup group = SegmentGroup.TRBV;
     private static SegmentGroupContainer container;

     @BeforeClass public static void setUpClass() throws Exception {
     library = SegmentLibrary.readFromFile("segments.gsl");
     treeMismatch = new STree(new NTreeNodeGeneratorBadMismatch(), library.getContainer(Species.HomoSapiens, group), 0, -1);
     treeDeletion = new STree(new NTreeNodeGeneratorBadDeletion(), library.getContainer(Species.HomoSapiens, group), 0, -1);
     container = library.getContainer(Species.HomoSapiens, group);
     //for (Allele allele : container.getAllelesList()) {
     //    NucleotideSequence ns = new NucleotideSubSequence(allele.getSequence(), allele.getReferencePointPosition() - 30, 31);
     //    System.out.println(ns);
     //}
     }

     @AfterClass public static void tearDownClass() throws Exception {
     }

     @Test public void trueSelfSearchTest() {
     int length = 30;
     boolean[] badArray = new boolean[length];
     Arrays.fill(badArray, false);
     STreeResultHolder resultHolder = new STreeResultHolder(treeMismatch);
     for (Allele allele : container.getAllelesList()) {
     NucleotideSequence target = new NucleotideSubSequence(allele.getSequence(), allele.getReferencePointPosition() - length + 1, length);
     DummyNIP nip = new DummyNIP(target, badArray);
     treeMismatch.performSearch(nip, resultHolder);
     assertEquals(resultHolder.size, length);
     }
     }

     @Test public void falseSelfSearchTest() {
     int length = 30;
     int mmPoint = length - 2;
     boolean[] badArray = new boolean[length];
     Arrays.fill(badArray, false);
     STreeResultHolder resultHolder = new STreeResultHolder(treeMismatch);
     for (Allele allele : container.getAllelesList()) {
     NucleotideSequence target = new NucleotideSubSequence(allele.getSequence(), allele.getReferencePointPosition() - length + 1, length);
     Bit2Array storage = NucleotideSequenceImpl.storageFromSequence(target);
     storage.set(mmPoint, storage.get(mmPoint) ^ 0x3);
     target = NucleotideSequenceImpl.fromStorage(storage);
     DummyNIP nip = new DummyNIP(target, badArray);
     treeMismatch.performSearch(nip, resultHolder);
     assertTrue(resultHolder.size < length);
     }
     }

     @Test public void trueSelfSearchWithMismatchTest() {
     int length = 30;
     int mmPoint = length - 2;
     boolean[] badArray = new boolean[length];
     Arrays.fill(badArray, false);
     badArray[mmPoint] = true;
     STreeResultHolder resultHolder = new STreeResultHolder(treeMismatch);
     for (Allele allele : container.getAllelesList()) {
     NucleotideSequence target = new NucleotideSubSequence(allele.getSequence(), allele.getReferencePointPosition() - length + 1, length);
     Bit2Array storage = NucleotideSequenceImpl.storageFromSequence(target);
     storage.set(mmPoint, storage.get(mmPoint) ^ 0x3);
     target = NucleotideSequenceImpl.fromStorage(storage);
     DummyNIP nip = new DummyNIP(target, badArray);
     treeMismatch.performSearch(nip, resultHolder);
     assertEquals(resultHolder.size, length);
     }
     }

     @Test public void trueSelfSearchWithDeletionAndCoordMapTest() {
     int length = 30;
     int insertionPoint = length - 2;
     int badTreeCoord = length - insertionPoint - 1;
     int goodTreeCoord = badTreeCoord + 3;
     boolean[] badArray = new boolean[length + 1];
     Arrays.fill(badArray, false);
     badArray[insertionPoint] = true;
     STreeResultHolder resultHolder = new STreeResultHolder(treeMismatch);
     for (Allele allele : container.getAllelesList()) {
     NucleotideSequence target = new NucleotideSubSequence(allele.getSequence(), allele.getReferencePointPosition() - length + 1, length);
     Bit2Array storage = new Bit2Array(length + 1);
     for (int i = 0; i < insertionPoint; ++i)
     storage.set(i, target.codeAt(i));
     storage.set(insertionPoint, target.codeAt(insertionPoint));
     for (int i = insertionPoint; i < length; ++i)
     storage.set(i + 1, target.codeAt(i));
     target = NucleotideSequenceImpl.fromStorage(storage);
     DummyNIP nip = new DummyNIP(target, badArray);
     treeDeletion.performSearch(nip, resultHolder);
     assertEquals(resultHolder.tryMapTreeCorrd(badTreeCoord, resultHolder.barcodeEvolution[resultHolder.size - 1]), -1);
     assertEquals(resultHolder.tryMapTreeCorrd(goodTreeCoord, resultHolder.barcodeEvolution[resultHolder.size - 1]), goodTreeCoord + 1);
     assertEquals(resultHolder.size, length + 1);
     }
     }

     public void simpleTest() {
     //gaacccgacagctttctatctctgtg
     //|25  |20  |15  |10  |5   |0
     //
     NucleotideSequence target = NucleotideSequenceImpl.fromSequence("gaacccgacagctttctatctctgtg");
     boolean[] badArray = new boolean[target.size()];
     Arrays.fill(badArray, false);
     //badArray[23] = true;
     DummyNIP nip = new DummyNIP(target, badArray);
     STreeResultHolder resultHolder = new STreeResultHolder(treeMismatch);
     treeMismatch.performSearch(nip, resultHolder);
     int i = 0;
     }

     public static class DummyNIP implements NucleotideInfoProvider {
     private NucleotideInfo info = new NucleotideInfo();
     private NucleotideSequence sequence;
     private boolean[] badArray;
     private int pointer = 0;

     public DummyNIP(String sequence, boolean[] badArray) {
     this(NucleotideSequenceImpl.fromSequence(sequence), badArray);
     }

     public DummyNIP(NucleotideSequence sequence, boolean[] badArray) {
     if (sequence.size() != badArray.length)
     throw new IllegalArgumentException();
     this.sequence = sequence;
     this.badArray = badArray;
     this.pointer = sequence.size() - 1;
     }

     @Override public NucleotideInfo next() {
     if (pointer == -1)
     return null;
     info.bad = badArray[pointer];
     info.code = sequence.codeAt(pointer);
     pointer--;
     return info;
     }
     }*/
}
