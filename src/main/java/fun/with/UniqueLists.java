package fun.with;

import fun.with.annotations.Unstable;
import fun.with.interfaces.Associate;
import fun.with.interfaces.CollectionLike;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    public <K, V> Maps<K, V> associate(Function<T, Pair<K, V>> association) {
        return Lists.wrap(this.ls).associate(association);
    }

    @Override
    public <K> Maps<K, T> associateBy(Function<T, K> keySelector) {
        return Lists.wrap(this.ls).associateBy(keySelector);
    }

    @Override
    public <V> Maps<T, V> associateWith(Function<T, V> valueSelector) {
        return Lists.wrap(this.ls).associateWith(valueSelector);
    }

    @Override
    public <V> Maps<T, V> associateWith(V v) {
        return Lists.wrap(this.ls).associateWith(v);
    }

    @Override
    public <K> Maps<K, Lists<T>> groupBy(Function<T, K> keySelector) {
        return Lists.wrap(this.ls).groupBy(keySelector);
    }

    @Override
    public <K, V> Maps<K, Lists<V>> groupBy(Function<T, K> keySelector, Function<T, V> valueSelector) {
        return Lists.wrap(this.ls).groupBy(keySelector, valueSelector);
    }

    @Override
    public UniqueLists<T> forEach(Consumer<? super T> consumer) {
        for (T t : this.ls) consumer.accept(t);
        return this;
    }

    @Override
    public UniqueLists<T> forEachIndexed(BiConsumer<Integer, ? super T> consumer) {
        int index = 0;
        for (T t : this.ls) {
            consumer.accept(index, t);
            index++;
        }
        return this;
    }

    @Override
    public UniqueLists<T> filter(Predicate<? super T> predicate) {
        List<T> ls = this.ls.stream().filter(predicate).collect(Collectors.toList());
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
    public boolean isEmpty() {
        return this.ls.isEmpty();
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
}
