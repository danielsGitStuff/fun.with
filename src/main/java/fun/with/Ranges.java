package fun.with;

import fun.with.misc.Checks;
import fun.with.util.RangeIterator;

import java.util.Iterator;

public class Ranges<T extends Number> implements Iterable<T> {

    private final T start;
    private final T stop;
    private final T step;

    private final boolean positive;
    private final Class<? extends Number> type;

    /**
     *
     * @param start is inclusive
     * @param stop is exclusive
     * @param step
     */

    private Ranges(T start, T stop, T step) {
        this.start = start;
        this.stop = stop;
        this.step = step;
        this.positive = this.stop.doubleValue() > this.start.doubleValue();
        this.type = start.getClass();
        Checks.check("step must go the same direction as start -> end", () -> this.step.doubleValue() > 0 == (this.stop.doubleValue() > this.start.doubleValue()));
    }

    /**
     *
     * @param start is inclusive
     * @param stop is exclusive
     * @param step
     * @return
     * @param <X>
     */
    public static <X extends Number> Ranges<X> of(X start, X stop, X step) {
        return new Ranges<>(start, stop, step);
    }

    public static <X extends Number> Ranges<X> of(X stop) {
        X start = NumberCasting.castLike(stop, 0);
        X step = NumberCasting.castLike(stop, 1);
        return new Ranges<>(start, stop, step);
    }

    public static void main(String[] args) {
        Ranges.of(2, 3).forEach(x -> System.out.println("Range.main " + x));
        Ranges.of(1.0,3.6,.5).forEach(System.out::println);
    }

    /**
     * Returns a Range of (including) start and (excluding) stop.
     * @param start inclusive
     * @param stop exclusive
     * @return
     * @param <X>
     */
    public static <X extends Number> Ranges<X> of(X start, X stop) {
        int intStep = (stop.doubleValue() > start.doubleValue()) ? 1 : -1;
        X step = NumberCasting.castLike(start, intStep);
        return new Ranges<>(start, stop, step);
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

    public Lists<T> ls() {
        return Lists.from(this.iterator());
    }

    @Override
    public Iterator<T> iterator() {
        return new RangeIterator<>(this);
    }

    public Class<? extends Number> getType() {
        return type;
    }
}
