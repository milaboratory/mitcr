package com.milaboratory.mitcr.clonegenerator;

//TODO documentation
public enum AccumulatorType {
    MaxStrict,
    MaxCompressed,
    AvrgStrict,
    AvrgCompressed;

    public static AccumulatorType get(boolean compressSegmentsStatistics, boolean returnAverageQuality) {
        if (returnAverageQuality)
            if (compressSegmentsStatistics)
                return AvrgCompressed;
            else
                return AvrgStrict;
        else if (compressSegmentsStatistics)
            return MaxCompressed;
        else
            return MaxStrict;
    }

    public static AccumulatorCloneFactory getFactory(AccumulatorType type) {
        if (type == null)
            throw new NullPointerException();

        switch (type) {
            case MaxCompressed:
                return AccumulatorCloneMaxCompressed.FACTORY;
            case MaxStrict:
                return AccumulatorCloneMaxStrict.FACTORY;
            case AvrgCompressed:
                return AccumulatorCloneAvrgCompressed.FACTORY;
            case AvrgStrict:
                return AccumulatorCloneAvrgStrict.FACTORY;
        }

        throw new UnsupportedOperationException("Not supported AccumulatorType.");
    }
}
