package fun.with;

import fun.with.annotations.Unstable;
import fun.with.interfaces.Associate;
import fun.with.interfaces.CollectionLike;
import fun.with.interfaces.actions.*;
import fun.with.misc.Pair;
import fun.with.misc.Unique;

import java.util.*;

/**
 * Like {@link Lists} but keeps elements unique using a {@link HashMap}.
 *
 * @param <T>
 */
@Unstable(reason = "Not that mature yet")
public class UniqueLists<T> implements CollectionLike<T, UniqueLists<T>>, Associate<T> {

    protected final List<T> ls;
    protected Map<T, Integer> instance2indexMap = new HashMap<>();

    protected UniqueLists(List<T> ls) {
        this.ls = new ArrayList<>();
        for (T t : ls) {
            if (!instance2indexMap.containsKey(t)) {
                instance2indexMap.put(t, this.ls.size());
                this.ls.add(t);
            }
        }
    }

    public static <X> UniqueLists<X> empty() {
        return new UniqueLists<>(new ArrayList<>());
    }

    public T getOriginal(T t) {
        if (!this.instance2indexMap.containsKey(t)) {
            throw new IllegalCallerException("requested the reference of the 'original' object. But I don't have it :(");
        }
        return this.ls.get(this.instance2indexMap.get(t));
    }

    public List<T> get() {
        return ls;
    }

    public T get(int index) {
        return this.ls.get(index);
    }

    public Lists<T> ls() {
        return Lists.wrap(this.ls);
    }

    @Override
    public <K, V> Maps<K, V> associate(ActionFunction<T, Pair<K, V>> association) {
        return Lists.wrap(this.ls).associate(association);
    }

    @Override
    public <K, V> Maps<K, V> associateIndexed(ActionBiFunction<Integer, T, Pair<K, V>> association) {
        return Lists.wrap(this.ls).associateIndexed(association);
    }

    @Override
    public <K, V> Maps<K, V> associate(ActionFunction<T, Pair<K, V>> association, Map<K, V> m) {
        return Lists.wrap(this.ls).associate(association, m);
    }

    @Override
    public <K, V> Maps<K, V> associateIndexed(ActionBiFunction<Integer, T, Pair<K, V>> association, Map<K, V> m) {
        return Lists.wrap(this.ls).associateIndexed(association, m);
    }

    @Override
    public <K> Maps<K, T> associateBy(ActionFunction<T, K> keySelector) {
        return Lists.wrap(this.ls).associateBy(keySelector);
    }

    @Override
    public <V> Maps<T, V> associateWith(ActionFunction<T, V> valueSelector) {
        return Lists.wrap(this.ls).associateWith(valueSelector);
    }

    @Override
    public <V> Maps<T, V> associateWith(V v) {
        return Lists.wrap(this.ls).associateWith(v);
    }

    @Override
    public <K> Maps<K, T> associateByIndexed(ActionBiFunction<Integer, T, K> keySelector) {
        return Lists.wrap(this.ls).associateByIndexed(keySelector);
    }

    @Override
    public <V> Maps<T, V> associateWithIndexed(ActionBiFunction<Integer, T, V> valueSelector) {
        return Lists.wrap(this.ls).associateWithIndexed(valueSelector);
    }

    @Override
    public <K> Maps<K, Lists<T>> groupBy(ActionFunction<T, K> keySelector) {
        return Lists.wrap(this.ls).groupBy(keySelector);
    }

    @Override
    public <K, V> Maps<K, Lists<V>> groupBy(ActionFunction<T, K> keySelector, ActionFunction<T, V> valueSelector) {
        return Lists.wrap(this.ls).groupBy(keySelector, valueSelector);
    }

    @Override
    public <R> Lists<R> flatMap(ActionFunction<T, Lists<R>> f) {
        return Lists.wrap(this.ls).flatMap(f);
    }

    @Override
    public UniqueLists<T> forEach(ActionConsumer<? super T> consumer) {
        for (T t : this.ls) consumer.accept(t);
        return this;
    }

    @Override
    public UniqueLists<T> forEachIndexed(ActionBiConsumer<Integer, ? super T> consumer) {
        int index = 0;
        for (T t : this.ls) {
            consumer.accept(index, t);
            index++;
        }
        return this;
    }

    @Override
    public UniqueLists<T> filter(ActionPredicate<? super T> predicate) {
        List<T> ls = new ArrayList<>();
        for (T t : this.ls) {
            if (predicate.test(t)) ls.add(t);
        }
        return UniqueLists.wrap(ls);
    }

    public static <X> UniqueLists<X> wrap(List<X> ls) {
        return new UniqueLists<>(ls);
    }

    @Override
    public UniqueLists<T> add(T t) {
        if (!this.instance2indexMap.containsKey(t)) {
            this.instance2indexMap.put(t, this.ls.size());
            this.ls.add(t);
        }
        return this;
    }

    @Override
    public UniqueLists<T> addAll(CollectionLike<T, ?> ts) {
        this.addAll(ts.getCollection());
        return this;
    }

    @Override
    public UniqueLists<T> addAll(Collection<T> ts) {
        ts.forEach(this::add);
        return this;
    }

    @Override
    public UniqueLists<T> addAll(Lists<T> ts) {
        ts.get().forEach(this::add);
        return this;
    }

    @Override
    public UniqueLists<T> addTo(CollectionLike<T, UniqueLists<T>> other) {
        other.addAll(this.ls);
        return this;
    }

    @Override
    public UniqueLists<T> unique() {
        return this;
    }

    @Override
    public <X> UniqueLists<T> uniqueBy(ActionFunction<T, X> f) {
        return this.uniqueBy(f, null);
    }

    @Override
    public <X> UniqueLists<T> uniqueBy(ActionFunction<T, X> f, ActionBiFunction<T, T, T> collisionSelector) {
        return Unique.uniqueBy(this, UniqueLists::empty, f, collisionSelector);
    }

    @Override
    public boolean isEmpty() {
        return this.ls.isEmpty();
    }

    @Override
    public boolean notEmpty() {
        return CollectionLike.super.notEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return this.ls.iterator();
    }

    @Override
    public int size() {
        return this.ls.size();
    }

    @Override
    public boolean contains(T t) {
        return this.instance2indexMap.containsKey(t);
    }

    @Override
    public Collection<T> getCollection() {
        return this.ls;
    }

    @Override
    public boolean allMatch(ActionPredicate<T> predicate) {
        for (T t : this.ls) {
            if (!predicate.test(t))
                return false;
        }
        return true;
    }

    @Override
    public boolean anyMatch(ActionPredicate<T> predicate) {
        for (T t : this.ls) {
            if (predicate.test(t))
                return true;
        }
        return false;
    }

    @Override
    public UniqueLists<T> nonNull() {
        return this.filter(Objects::nonNull);
    }

}
