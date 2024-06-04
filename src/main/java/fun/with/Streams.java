package fun.with;

import fun.with.actions.*;
import fun.with.annotations.Unstable;
import fun.with.interfaces.CollectionLike;
import fun.with.misc.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;


@Unstable(reason = "needs evaluation")
public class Streams<T> {

    //
    private final Stream<T> stream;
    private final boolean allowParallel;

    //
    private Streams(Stream<T> stream, boolean allowParallel) {
        this.stream = stream;
        this.allowParallel = allowParallel;
    }

    private Stream<Pair<Integer, T>> indexedStream() {
        Stream<T> stream = this.stream.sequential();
        int[] index = new int[]{0};
        return stream.map(t -> Pair.of(index[0]++, t));
    }


    public <X> Streams<X> mapIndexed(ActionBiFunction<Integer, ? super T, X> f) {
        Stream<Pair<Integer, T>> pairs = this.indexedStream();
        pairs = this.allowParallel ? pairs.parallel() : pairs;
        Stream<X> result = pairs.map(pair -> f.apply(pair.k(), pair.v()));
        return Streams.wrap(result);
    }

    /**
     * Uses xs as the underlying list. Changes to that list also occur in here though.
     *
     * @param xs
     * @param <X>
     * @return
     */
    public static <X> Streams<X> wrap(Stream<X> xs) {
        return new Streams<>(xs, false);
    }

    public static <X> Streams<X> wrap(Collection<X> xs) {
        return Streams.wrap(new ArrayList<>(xs));
    }

    public Lists<T> ls() {
        return this.stream.collect(Lists.collect());
    }

    public <R> Streams<R> flatMap(ActionFunction<T, Lists<R>> f) {
        Stream<R> result = this.stream.flatMap(t -> f.apply(t).get().stream());
        return Streams.wrap(result);
    }

    public void forEach(ActionConsumer<? super T> consumer) {
        this.stream.forEach(consumer::accept);
    }

    public void forEachIndexed(ActionBiConsumer<Integer, ? super T> consumer) {
        this.indexedStream().forEach(pair -> consumer.accept(pair.k(), pair.v()));
    }

    public Streams<T> filter(ActionPredicate<? super T> predicate) {
        return Streams.wrap(this.stream.filter(predicate::test));
    }

    public void addTo(CollectionLike<T, ?> other) {
        this.stream.forEach(other::add);
    }

    public Streams<T> filterIndexed(ActionBiPredicate<Integer, T> predicate){
        return this.mapIndexed(Pair::of).filter(integerTPair -> predicate.test(integerTPair.k(), integerTPair.v())).map(Pair::v);
    }

    public <X> Streams<X> map(ActionFunction<T,X> f){
        return Streams.wrap(this.stream.map(f::apply));
    }

    public Stream<T> get() {
        return this.stream;
    }

//    public Streams<T> unique() {
//        ConcurrentHashMap<T, Object> m = new ConcurrentHashMap<>();
//
//        return Streams.wrap(this.stream.map(t -> m.con));
//    }
//
//    @Override
//    public <X> Streams<T> uniqueBy(ActionFunction<T, X> f) {
//        return null;
//    }
//
//    @Override
//    public <X> Streams<T> uniqueBy(ActionFunction<T, X> f, ActionBiFunction<T, T, T> collisionSelector) {
//        return null;
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return false;
//    }
//
//    @Override
//    public Iterator<T> iterator() {
//        return null;
//    }
//
//    @Override
//    public int size() {
//        return 0;
//    }
//
//    @Override
//    public boolean contains(T t) {
//        return false;
//    }
//
//    @Override
//    public Collection<T> getCollection() {
//        return List.of();
//    }
//
//    @Override
//    public boolean allMatch(ActionPredicate<T> predicate) {
//        return false;
//    }
//
//    @Override
//    public boolean anyMatch(ActionPredicate<T> predicate) {
//        return false;
//    }
//
//    @Override
//    public Streams<T> nonNull() {
//        return null;
//    }
}
