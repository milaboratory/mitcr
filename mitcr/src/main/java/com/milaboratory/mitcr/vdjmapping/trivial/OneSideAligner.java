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

package com.milaboratory.mitcr.vdjmapping.trivial;

import com.milaboratory.core.segment.Allele;
import com.milaboratory.core.segment.SegmentGroupContainer;
import com.milaboratory.mitcr.qualitystrategy.GoodBadNucleotideSequence;
import com.milaboratory.util.BitArray;

import java.util.Arrays;

/**
 * Continues alignment till the end of the sequence
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class OneSideAligner {
    public final static int INITIAL_SCORE = 3;
    private final static int MAX_LAG = 3;
    private final int goodCost, badCost;
    private final SegmentGroupContainer group;
    private final Allele[] alleles;
    private final int offset;
    private final int tolerance;
    private final int[] cache;
    private int lastScore;
    private int lastMatch, lastForwardMatch, lastBackwardMatch;

    public OneSideAligner(SegmentGroupContainer group, Allele[] alleles, int offset, int tolerance,
                          int goodCost, int badCost) {
        this.alleles = alleles;
        this.group = group;
        this.offset = offset;
        this.tolerance = tolerance;
        this.cache = new int[alleles.length];
        this.goodCost = goodCost;
        this.badCost = badCost;
    }

    public int getLastScore() {
        return lastScore;
    }

    public OneSideAlignmentResult buildResult(GoodBadNucleotideSequence sequence, int refPoint,
                                              int alignmentDirection) {
        final BitArray set = build(sequence, refPoint, alignmentDirection);
        int startPoint = refPoint + getCorrectedOffset(alignmentDirection);
        if (alignmentDirection == +1)
            return new OneSideAlignmentResult(refPoint, startPoint, lastMatch, startPoint,
                    determineContinuousBorder(set, sequence, refPoint, alignmentDirection),
                    lastScore, set);
        else if (alignmentDirection == -1)
            return new OneSideAlignmentResult(refPoint, lastMatch, startPoint, determineContinuousBorder(set, sequence,
                    refPoint, alignmentDirection), startPoint,
                    lastScore, set);
        else
            throw new RuntimeException();
    }

    public BitArray build(GoodBadNucleotideSequence sequence, int refPoint,
                          int alignmentDirection) {
        int[] score = cache;
        Arrays.fill(score, INITIAL_SCORE);
        return build(score, sequence, refPoint, alignmentDirection);
    }

    /*public BitArray build(BitArray initialBa, GoodBadNucleotideSequence sequence, int refPoint,
                          int alignmentDirection) {
        int[] score = cache;
        Arrays.fill(score, -1);
        for (int i : initialBa.getBits())
            score[i] = INITIAL_SCORE;
        return build(score, sequence, refPoint, alignmentDirection);
    }*/

    public OneSideAlignmentResult doubleSidedAlignmentResult(GoodBadNucleotideSequence sequence, int refPoint,
                                                             int masterAlignmentDirection) {
        final BitArray set = doubleSidedAlignment(sequence, refPoint, masterAlignmentDirection);
        return new OneSideAlignmentResult(refPoint, lastBackwardMatch, lastForwardMatch,
                determineContinuousBorder(set, sequence, refPoint, -1),
                determineContinuousBorder(set, sequence, refPoint, +1),
                lastScore, set);
    }

    public BitArray doubleSidedAlignment(GoodBadNucleotideSequence sequence, int refPoint,
                                         int masterAlignmentDirection) {
        //Initializing scores
        int[] score = cache;
        Arrays.fill(score, INITIAL_SCORE);

        //Forward pass (outside CDR3)
        buildScores(score, sequence, refPoint, masterAlignmentDirection);

        //Saving alignment-driven border
        if (masterAlignmentDirection == -1)
            lastBackwardMatch = lastMatch;
        else
            lastForwardMatch = lastMatch;

        //Backward pass (inside CDR3)
        buildScores(score, sequence, refPoint, -masterAlignmentDirection);

        //Saving alignment-driven border
        if (masterAlignmentDirection == 1)
            lastBackwardMatch = lastMatch;
        else
            lastForwardMatch = lastMatch;

        //Creating result
        BitArray ba = new BitArray(group.getAllelesCount());
        for (int i = 0; i < score.length; ++i)
            if (score[i] >= 0)
                ba.set(alleles[i].getIndex());

        return ba;
    }

    public BitArray build(int[] score, GoodBadNucleotideSequence sequence, int refPoint,
                          int alignmentDirection) {
        //SegmentGroupContainer group = this.group; //for performance
        buildScores(score, sequence, refPoint, alignmentDirection);
        BitArray ba = new BitArray(group.getAllelesCount());
        for (int i = 0; i < score.length; ++i)
            if (score[i] >= 0)
                ba.set(alleles[i].getIndex());
        int startPoint = refPoint + getCorrectedOffset(alignmentDirection);
        if (alignmentDirection == -1) {
            lastForwardMatch = startPoint;
            lastBackwardMatch = lastMatch;
        } else {
            lastForwardMatch = lastMatch;
            lastBackwardMatch = startPoint;
        }
        return ba;
    }

    /**
     * Performs a race between alleles.
     *
     * @param score    initial score
     * @param sequence sequence
     * @param refPoint reference point in sequence coords
     */
    public void buildScores(int[] score, GoodBadNucleotideSequence sequence, int refPoint,
                            int alignmentDirection) {
        final Allele[] alleles = this.alleles;
        byte nuc;
        boolean isBad;
        int i, alleleCoord;
        Allele allele;
        //This variable is always positive
        //At the end of the race (coordinate iteration) it contains maximum seen score
        int maxScore = 0;
        //Calculating initial max score
        //If run is initial maxScore == INITIAL_SCORE
        for (int s : score)
            if (maxScore < s)
                maxScore = s;

        //Flag to catch end of iteration
        boolean positivesExists = true,
                recalculatePositivesExists = false;

        int lastMatch = -1;

        //Start!!!
        for (int coord = getCorrectedOffset(alignmentDirection);
             coord >= -refPoint && coord < sequence.size() - refPoint;
             coord += alignmentDirection) {

            //Sequence nucleotide
            nuc = sequence.codeAt(coord + refPoint);
            isBad = sequence.isBad(coord + refPoint);

            //Clear flag
            positivesExists = false;
            recalculatePositivesExists = false;

            //Iteration through alleles
            for (i = 0; i < alleles.length; ++i) {
                //If this allele lose race it gets additional penalty for each
                //iteration where somebody collects points
                if (score[i] < 0) {
                    score[i] -= isBad ? badCost : goodCost;
                    continue;
                }

                //There are alleles in the play!
                positivesExists = true;
                allele = alleles[i];

                //Coord in allele
                alleleCoord = allele.getReferencePointPosition() + coord;
                if ((alleleCoord >= 0 && alleleCoord < allele.getSequence().size()) &&
                        (nuc == allele.getSequence().codeAt(alleleCoord))) {
                    lastMatch = coord + refPoint;
                    score[i] += isBad ? badCost : goodCost; //Nucleotide match!
                } else
                    score[i] -= isBad ? badCost : goodCost; //Mismatch!

                //Increasing max score
                if (maxScore < score[i])
                    maxScore = score[i];
            }

            //If all alleles lose
            if (!positivesExists)
                break;

            //Some alleles are totally behind others
            for (i = 0; i < alleles.length; ++i)
                if (score[i] >= 0 && maxScore - MAX_LAG > score[i]) {
                    score[i] = -1; //This one looses.
                    recalculatePositivesExists = true;
                }
        }
        //Race finished.

        //Very rare event
        if (recalculatePositivesExists) {
            positivesExists = false;
            for (int s : score)
                if (s > 0) {
                    positivesExists = true;
                    break;
                }
        }

        if (!positivesExists) { //If all alleles lose
            //Reward:
            //Calculate rewardAmount
            int rewardAmount = Integer.MIN_VALUE;
            for (int s : score)
                if (rewardAmount < s)
                    rewardAmount = s;
            rewardAmount = maxScore - rewardAmount;

            for (i = 0; i < alleles.length; ++i)
                score[i] += rewardAmount; //Allele with maximal value will get score == maxScore

            //TODO delete this assert
            boolean yn = false;
            for (i = 0; i < alleles.length; ++i)
                yn |= (score[i] == maxScore);
            if (!yn)
                throw new RuntimeException("GUU!!!");
        }

        //Calculating threshold.
        int treshold = maxScore;
        //Subtracting tolerance.
        treshold -= tolerance;

        for (i = 0; i < score.length; ++i) {
            if (score[i] < treshold && score[i] >= 0) //Positive, but not enough
                score[i] -= maxScore;
        }

        lastScore = maxScore;
        this.lastMatch = lastMatch;
    }

    public int getLastMatch() {
        return lastMatch;
    }

    public int getLastForwardMatch() {
        return lastForwardMatch;
    }

    public int getLastBackwardMatch() {
        return lastBackwardMatch;
    }

    public Allele[] getAlleles() {
        return alleles;
    }

    private int getCorrectedOffset(int alignmentDirection) {
        return offset - (1 - alignmentDirection) / 2;
    }

    public int determineContinuousBorder(BitArray set, GoodBadNucleotideSequence sequence,
                                         int refPoint, int alignmentDirection) {

        final Allele[] alleles = this.alleles;
        byte nuc;
        int allelePosition;
        Allele allele;

        int border = getCorrectedOffset(alignmentDirection);
        int mmPosition, position;

        for (int i = 0; i < alleles.length; ++i) {
            if (!set.get(alleles[i].getIndex()))
                continue;

            //Getting allele
            allele = alleles[i];

            mmPosition = Integer.MIN_VALUE;
            for (position = getCorrectedOffset(alignmentDirection);
                 position >= -refPoint && position < sequence.size() - refPoint;
                 position += alignmentDirection) {

                //Sequence nucleotide
                nuc = sequence.codeAt(position + refPoint);

                //Allele
                allelePosition = allele.getReferencePointPosition() + position;

                //End of sequence
                if (allelePosition < 0 || allelePosition >= allele.getSequence().size()) {
                    if (mmPosition != Integer.MIN_VALUE && Math.abs(position - mmPosition) <= 3)
                        position = mmPosition;
                    break;
                }

                //Mismatch
                if (nuc != allele.getSequence().codeAt(allelePosition))
                    if (mmPosition == Integer.MIN_VALUE)
                        mmPosition = position;
                    else {
                        if (Math.abs(position - mmPosition) <= 3)
                            position = mmPosition;
                        break;
                    }
            }

            if (Integer.compare(border, position) * alignmentDirection < 0)
                border = position;
        }

        return border + refPoint - alignmentDirection;
    }
}