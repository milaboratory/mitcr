package com.milaboratory.core.sequence;

public abstract class AbstractSequenceBuilder<S extends Sequence> implements SequenceBuilder<S> {
    protected byte[] data;

    protected AbstractSequenceBuilder(int length) {
        this.data = new byte[length];
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public void setCode(int position, byte code) {
        data[position] = code;
    }
}
