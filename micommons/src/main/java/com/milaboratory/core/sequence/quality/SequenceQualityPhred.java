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
package com.milaboratory.core.sequence.quality;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Implementation sequence quality.
 *
 * <p>Phred sequence quality scores.</p>
 *
 * @author Bolotin Dmitriy (bolotin.dmitriy@gmail.com)
 * @author Shugay Mikhail (mikhail.shugay@gmail.com)
 */
public final class SequenceQualityPhred implements Serializable {
    private static final long serialVersionUID = 1L;
    private final byte[] data;

    /**
     * Creates a phred sequence quality from a Sanger formatted quality string (33 based).
     *
     * @param string
     */
    public SequenceQualityPhred(String string) {
        this(string, 33);
    }

    /**
     * Creates a phred sequence quality from a string formatted with corresponding offset.
     *
     * @param string
     */
    public SequenceQualityPhred(String string, int offset) {
        this.data = string.getBytes();
        for (int i = this.data.length - 1; i >= 0; --i)
            this.data[i] -= offset;
    }

    /**
     * Creates a phred sequence quality from a string formatted with corresponding offset.
     *
     * @param string
     */
    public SequenceQualityPhred(String string, QualityFormat format) {
        this(string, format.getOffset());
    }

    public byte[] getInnerData() {
        return data.clone();
    }

    /**
     * Creates a phred sequence quality containing only given values of quality.
     *
     * @param value  value to fill the quality values with
     * @param length size of quality string
     */
    public SequenceQualityPhred(byte value, int length) {
        data = new byte[length];
        Arrays.fill(data, value);
    }

    /**
     * Creates quality object from raw quality score values.
     *
     * @param data raw quality score values
     */
    public SequenceQualityPhred(byte[] data) {
        this.data = data.clone();
    }

    /**
     * Constructor for factory method.
     */
    private SequenceQualityPhred(byte[] data, boolean unsafe) {
        this.data = data;
    }

    /**
     * Get the log10 of probability of error (e.g. nucleotide substitution) at given sequence point
     *
     * @param coord coordinate in sequence
     * @return log10 of probability of error
     */
    public float log10ProbabilityOfErrorAt(int coord) {
        return -((float) data[coord]) / 10;
    }

    /**
     * Get probability of error (e.g. nucleotide substitution) at given sequence point
     *
     * @param coord coordinate in sequence
     * @return probability of error
     */
    public float probabilityOfErrorAt(int coord) {
        return (float) Math.pow(10.0, -(data[coord]) / 10);
    }

    /**
     * Get the raw sequence quality value (in binary format) at given sequence point
     *
     * @param coord coordinate in sequence
     * @return raw sequence quality value
     */
    public byte value(int coord) {
        return data[coord];
    }

    /**
     * Returns the worst sequence quality value
     *
     * @return worst sequence quality value
     */
    public byte minValue() {
        byte min = Byte.MAX_VALUE;
        for (byte b : data)
            if (b < min)
                min = b;
        return min;
    }

    /**
     * Gets quality values in reverse order
     *
     * @return quality values in reverse order
     */
    public SequenceQualityPhred reverse() {
        return new SequenceQualityPhred(reverseTransformQualityStorage(data));
    }

    /**
     * Helper method.
     */
    private static byte[] reverseTransformQualityStorage(byte[] quality) {
        byte[] newData = new byte[quality.length];
        int reverseCoord = quality.length - 1;
        for (int coord = 0; coord < quality.length; ++coord, --reverseCoord)
            //reverseCoord = quality.length - 1 - coord;
            newData[coord] = quality[reverseCoord];

        assert reverseCoord == -1;

        return newData;
    }

    /**
     * Returns substring of current quality scores line.
     *
     * @param from inclusive
     * @param to   exclusive
     * @return substring of current quality scores line
     */
    public SequenceQualityPhred getRange(int from, int to) {
        return new SequenceQualityPhred(Arrays.copyOfRange(data, from, to), true);
    }

    /**
     * Returns size of quality array
     *
     * @return size of quality array
     */
    public int size() {
        return data.length;
    }

    @Override
    public String toString() {
        return encodeToString(33);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SequenceQualityPhred that = (SequenceQualityPhred) o;

        if (!Arrays.equals(data, that.data)) return false;

        return true;
    }

    /**
     * Encodes current quality line with given offset. Common values for offset are 33 and 64.
     *
     * @param offset offset
     * @return bytes encoded quality values
     */
    public byte[] encode(int offset) {
        if (offset < 0 || offset > 70)
            throw new IllegalArgumentException();

        byte[] copy = new byte[data.length];
        for (int i = copy.length - 1; i >= 0; --i)
            copy[i] += data[i] + offset;
        return copy;
    }

    /**
     * Encodes current quality line with given offset. Common values for offset are 33 and 64.
     *
     * @param offset offset
     * @return encoded quality values
     */
    public String encodeToString(int offset) {
        return new String(encode(offset));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data) * 31 + 17;
    }

    /**
     * Returns a copy of inner byte array.
     *
     * @param quality quality
     * @return values
     */
    public static byte[] getContent(SequenceQualityPhred quality) {
        return quality.data.clone();
    }

    public static byte[] parse(QualityFormat format, byte[] data, boolean check) {
        //For performance
        final byte offset = format.getOffset(), from = format.getMinValue(), to = format.getMaxValue();
        byte[] res = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] = (byte) (data[i] - offset);

            if (check &&
                    (res[i] < from || res[i] > to))
                throw new WrongQualityStringException(((char) (data[i])) + " [" + res[i] + "]");

            //if (data[i] < qualityCodeFrom)
            //    if (!lowerUnSafe)
            //        throw new WrongQualityStringException(((char) (data[i] + qualityCodeOffset)) + " [" + data[i] + "]");
            //    else
            //        data[i] = (byte) qualityCodeFrom;
        }
        return res;
    }

    /**
     * Factory method for the SequenceQualityPhred object. It performs all necessary range checks if required.
     *
     * @param format format of encoded quality values
     * @param data   byte with encoded quality values
     * @param check  determines whether range check is required
     * @return quality line object
     * @throws WrongQualityStringException if encoded value are out of range and checking is enabled
     */
    public static SequenceQualityPhred create(QualityFormat format, byte[] data, boolean check) {
        return new SequenceQualityPhred(parse(format, data, check), true);
    }
}
