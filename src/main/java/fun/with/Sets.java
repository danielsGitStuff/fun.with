package fun.with;

import fun.with.actions.ActionBiConsumer;
import fun.with.actions.ActionConsumer;
import fun.with.actions.ActionFunction;
import fun.with.actions.ActionPredicate;
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

    public Set<T> get() {
        return this.set;
    }


    @Override
    public <K, V> Maps<K, V> associate(ActionFunction<T, Pair<K, V>> association) {
        Map<K, V> m = new HashMap<>();
        for (T t : this.set) {
            Pair<K, V> pair = association.apply(t);
            m.put(pair.k(), pair.v());
        }
        return Maps.wrap(m);
    }

    @Override
    public <K> Maps<K, T> associateBy(ActionFunction<T, K> keySelector) {
        Map<K, T> m = new HashMap<>();
        for (T t : this.set) {
            K k = keySelector.apply(t);
            m.put(k, t);
        }
        return Maps.wrap(m);
    }

    @Override
    public <V> Maps<T, V> associateWith(ActionFunction<T, V> valueSelector) {
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
    public <K> Maps<K, Lists<T>> groupBy(ActionFunction<T, K> keySelector) {
        Map<K, Lists<T>> m = new HashMap<>();
        for (T t : this.set) {
            K k = keySelector.apply(t);
            m.computeIfAbsent(k, k1 -> Lists.empty());
            m.get(k).add(t);
        }
        return Maps.wrap(m);
    }

    @Override
    public <K, V> Maps<K, Lists<V>> groupBy(ActionFunction<T, K> keySelector, ActionFunction<T, V> valueSelector) {
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
    public Sets<T> forEach(ActionConsumer<? super T> consumer) {
        for (T t : this.set) {
            consumer.accept(t);
        }
        return this;
    }

    @Override
    public Sets<T> forEachIndexed(ActionBiConsumer<Integer, ? super T> consumer) {
        int index = 0;
        for (T t : this.set) {
            consumer.accept(index, t);
            index++;
        }
        return this;
    }

    @Override
    public Sets<T> filter(ActionPredicate<? super T> predicate) {
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

    public Lists<T> ls() {
        return Lists.wrap(this.set);
    }

    public Sets<T> subtract(Sets<T> others) {
        return this.filter(it -> !others.contains(it));
    }

    public Sets<T> union(Sets<T> others) {
        Sets<T> result = Sets.wrap(this.set);
        result.addAll(others);
        return result;
    }

    @Override
    public Sets<T> addAll(CollectionLike<T, ?> ts) {
        this.set.addAll(ts.getCollection());
        return this;
    }

    @Override
    public Collection<T> getCollection() {
        return this.set;
    }

    @Override
    public boolean allMatch(ActionPredicate<T> predicate) {
        for (T t : this.set) {
            if (!predicate.test(t))
                return false;
        }
        return true;    }

    @Override
    public String toString() {
        if (isEmpty()) return "S(0){}";
        StringBuilder b = new StringBuilder("S(").append(this.size()).append("){");
        final int lastIndex = this.set.size() - 1;
        int index = 0;
        for (T t : this.set) {
            b.append(t);
            if (index != lastIndex)
                b.append(",");
            index++;
        }
        return b.append("}").toString();
    }

    public Sets<T> intersection(Set<T> other) {
        Set<T> set = new HashSet<>(this.set);
        set.retainAll(other);
        return Sets.wrap(set);
    }

    public Sets<T> intersection(Sets<T> other) {
        return this.intersection(other.get());
    }

    @Override
    public boolean anyMatch(ActionPredicate<T> predicate) {
        for (T t : this.set) {
            if (predicate.test(t))
                return true;
        }
        return false;    }
}
