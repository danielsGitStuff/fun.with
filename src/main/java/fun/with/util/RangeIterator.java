package fun.with.util;

import fun.with.Range;

import java.util.Iterator;

public class RangeIterator<T extends Number> implements Iterator<T> {

    private final Range<T> range;
    private final double doubleStart;
    private final double doubleStop;
    private final double doubleStep;
    private final boolean positive;
    private final T start;
    private double doubleCurrent;

    public RangeIterator(Range<T> range) {
        this.range = range;
        this.doubleStart = range.getStart().doubleValue();
        this.doubleStop = range.getStop().doubleValue();
        this.doubleStep = range.getStep().doubleValue();
        this.doubleCurrent = this.doubleStart;
        this.positive = range.isPositive();
        this.start = range.getStart();
    }


    @Override
    public boolean hasNext() {
        if (this.positive) return this.doubleCurrent <= this.doubleStop;
        return this.doubleCurrent >= this.doubleStop;
    }

    @Override
    public T next() {
        double current = this.doubleCurrent;
        this.doubleCurrent += this.doubleStep;
        return NumberTransformer.castLike(this.start, current);
    }
}
