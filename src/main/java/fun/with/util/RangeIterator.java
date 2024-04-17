package fun.with.util;

import fun.with.NumberCasting;
import fun.with.Ranges;

import java.util.Iterator;

public class RangeIterator<T extends Number> implements Iterator<T> {

    private final double doubleStop;
    private final double doubleStep;
    private final boolean positive;
    private final T start;
    private double doubleCurrent;

    public RangeIterator(Ranges<T> ranges) {
        this.doubleStop = ranges.getStop().doubleValue();
        this.doubleStep = ranges.getStep().doubleValue();
        this.doubleCurrent = ranges.getStart().doubleValue();
        this.positive = ranges.isPositive();
        this.start = ranges.getStart();
    }


    @Override
    public boolean hasNext() {
        if (this.positive) return this.doubleCurrent < this.doubleStop;
        return this.doubleCurrent > this.doubleStop;
    }

    @Override
    public T next() {
        double current = this.doubleCurrent;
        this.doubleCurrent += this.doubleStep;
        return NumberCasting.castLike(this.start, current);
    }
}
