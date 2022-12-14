package fun.with;

import fun.with.interfaces.Associate;
import fun.with.interfaces.CollectionLike;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Sets<T> implements CollectionLike<T, Sets<T>>, Associate<T> {

    private final Set<T> set;

    private Sets(Set<T> set) {
        this.set = set;
    }

    public static <X> Sets<X> wrap(X... xs) {
        return Sets.wrap(Arrays.asList(xs));
    }

    public static <X> Sets<X> wrap(Collection<X> xs) {
        return new Sets<>(new HashSet<>(xs));
    }


    @Override
    public <K, V> Maps<K, V> associate(Function<T, Pair<K, V>> association) {
        Map<K, V> m = new HashMap<>();
        for (T t : this.set) {
            Pair<K, V> pair = association.apply(t);
            m.put(pair.k(), pair.v());
        }
        return Maps.wrap(m);
    }

    @Override
    public <K> Maps<K, T> associateBy(Function<T, K> keySelector) {
        Map<K, T> m = new HashMap<>();
        for (T t : this.set) {
            K k = keySelector.apply(t);
            m.put(k, t);
        }
        return Maps.wrap(m);
    }

    @Override
    public <V> Maps<T, V> associateWith(Function<T, V> valueSelector) {
        Map<T, V> m = new HashMap<>();
        for (T t : this.set) {
            V v = valueSelector.apply(t);
            m.put(t, v);
        }
        return Maps.wrap(m);
    }

    @Override
    public <V> Maps<T, V> associateWith(V v) {
        Map<T, V> m = new HashMap<>();
        for (T t : this.set) {
            m.put(t, v);
        }
        return Maps.wrap(m);
    }

    @Override
    public <K> Maps<K, Lists<T>> groupBy(Function<T, K> keySelector) {
        Map<K, Lists<T>> m = new HashMap<>();
        for (T t : this.set) {
            K k = keySelector.apply(t);
            m.computeIfAbsent(k, k1 -> Lists.empty());
            m.get(k).add(t);
        }
        return Maps.wrap(m);
    }

    @Override
    public <K, V> Maps<K, Lists<V>> groupBy(Function<T, K> keySelector, Function<T, V> valueSelector) {
        Map<K, Lists<V>> m = new HashMap<>();
        for (T t : this.set) {
            K k = keySelector.apply(t);
            V v = valueSelector.apply(t);
            m.computeIfAbsent(k, k1 -> Lists.empty());
            m.get(k).add(v);
        }
        return Maps.wrap(m);
    }

    @Override
    public Sets<T> forEach(Consumer<? super T> consumer) {
        for (T t : this.set) {
            consumer.accept(t);
        }
        return this;
    }

    @Override
    public Sets<T> forEachIndexed(BiConsumer<Integer, ? super T> consumer) {
        int index = 0;
        for (T t : this.set) {
            consumer.accept(index, t);
            index++;
        }
        return this;
    }

    @Override
    public Sets<T> filter(Predicate<? super T> predicate) {
        Set<T> set = new HashSet<>();
        for (T t : this.set) {
            if (predicate.test(t)) {
                set.add(t);
            }
        }
        return Sets.wrap(set);
    }

    @Override
    public Sets<T> add(T t) {
        this.set.add(t);
        return this;
    }

    @Override
    public Sets<T> addAll(Collection<T> collection) {
        this.set.addAll(collection);
        return this;
    }

    @Override
    public Sets<T> addAll(Lists<T> ts) {
        this.set.addAll(ts.get());
        return this;
    }

    @Override
    public Sets<T> addTo(CollectionLike<T, Sets<T>> other) {
        other.addAll(this.set);
        return this;
    }

    @Override
    public Sets<T> unique() {
        return this;
    }

    @Override
    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return this.set.iterator();
    }

    @Override
    public int size() {
        return this.set.size();
    }

    @Override
    public boolean contains(T t) {
        return this.set.contains(t);
    }
}
