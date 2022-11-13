package fun.with;

import fun.with.util.NumberTransformer;
import fun.with.util.RangeIterator;

import java.util.Iterator;

public class Range<T extends Number> implements Iterable<T> {

    private final T start;
    private final T stop;
    private final T step;

    private final boolean positive;
    private final Class<? extends Number> type;

    public static <X extends Number> Range<X> of(X start, X stop, X step) {
        return new Range<>(start, stop, step);
    }

    public static <X extends Number> Range<X> of(X start, X stop) {
        int intStep = (stop.doubleValue() > start.doubleValue()) ? 1 : -1;
        X step = NumberTransformer.castLike(start, intStep);
        return new Range<>(start, stop, step);
    }

    public static <X extends Number> Range<X> of(X stop) {
        X start = NumberTransformer.castLike(stop, 0);
        X step = NumberTransformer.castLike(stop, 1);
        return new Range<>(start, stop, step);
    }

    private Range(T start, T stop, T step) {
        this.start = start;
        this.stop = stop;
        this.step = step;
        this.positive = this.stop.doubleValue() > this.start.doubleValue();
        this.type = start.getClass();
        Checks.check("step must go the same direction as start -> end", () -> this.step.doubleValue() > 0 == (this.stop.doubleValue() > this.start.doubleValue()));
    }

    public T getStart() {
        return start;
    }

    public T getStep() {
        return step;
    }

    public T getStop() {
        return stop;
    }

    public boolean isPositive() {
        return positive;
    }

    @Override
    public Iterator<T> iterator() {
        return new RangeIterator<>(this);
    }

    public Lists<T> ls() {
        return Lists.wrap(this.iterator());
    }

    public Class<? extends Number> getType() {
        return type;
    }

    public static void main(String[] args) {
        Range.of(2, 3).forEach(x -> System.out.println("Range.main " + x));
    }
}
