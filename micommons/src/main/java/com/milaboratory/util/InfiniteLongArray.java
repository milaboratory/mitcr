package com.milaboratory.util;

import java.util.Arrays;

public class InfiniteLongArray {
    private long[] data;
    private int size = 0;

    private InfiniteLongArray(long[] data, int size) {
        this.data = data;
        this.size = size;
    }

    public InfiniteLongArray() {
        data = new long[10];
    }

    private void ensureCapacity(int minCapacity) {
        int oldCapacity = data.length;
        if (oldCapacity < minCapacity) {
            int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity)
                newCapacity = minCapacity;
            data = Arrays.copyOf(data, newCapacity);
        }
    }

    public void ensureSize(int minSize) {
        if (size < minSize) {
            ensureCapacity(minSize);
            size = minSize;
        }
    }

    public void set(int index, long value) {
        ensureSize(index + 1);
        data[index] = value;
    }

    public void add(int index, long value) {
        ensureSize(index + 1);
        data[index] += value;
    }

    public void add(int offset, InfiniteLongArray array) {
        ensureSize(offset + array.size);
        for (int i = array.size - 1; i >= 0; --i)
            data[i + offset] += array.data[i];
    }

    public void add(InfiniteLongArray array) {
        add(0, array);
    }

    public void addOne(int index) {
        ensureSize(index + 1);
        data[index]++;
    }

    /**
     * If there are no elements in array returns {@link Integer#MAX_VALUE}.
     */
    public int firstNonZeroElement() {
        for (int i = 0; i < size; ++i)
            if (data[i] != 0)
                return i;
        return Integer.MAX_VALUE;
    }

    public long sum() {
        long sum = 0;
        for (int i = 0; i < size; ++i)
            sum += data[i];
        return sum;
    }

    public long get(int index) {
        if (index < 0)
            throw new IndexOutOfBoundsException();
        if (index >= size)
            return 0L;
        return data[index];
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return Arrays.toString(Arrays.copyOf(data, size));
    }

    @Override
    public InfiniteLongArray clone() {
        return new InfiniteLongArray(data.clone(), size);
    }
}
