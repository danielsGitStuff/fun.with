package fun.with;

import fun.with.actions.*;
import fun.with.interfaces.Associate;
import fun.with.interfaces.CollectionLike;
import fun.with.misc.Pair;
import fun.with.misc.Unique;

import java.util.*;

public class Sets<T> implements CollectionLike<T, Sets<T>>, Associate<T> {

    private final Set<T> set;

    private Sets(Set<T> set) {
        this.set = set;
    }

    public static <X> Sets<X> of(X... xs) {
        return Sets.of(Arrays.asList(xs));
    }

    public static <X> Sets<X> of(Collection<X> xs) {
        return new Sets<>(new HashSet<>(xs));
    }

    public static <X> Sets<X> wrap(Set<X> xs) {
        return new Sets<>(xs);
    }

    public Set<T> get() {
        return this.set;
    }

    @Override
    public <K, V> Maps<K, V> associate(ActionFunction<T, Pair<K, V>> association) {
        return this.associate(association, new HashMap<>());
    }

    @Override
    public <K, V> Maps<K, V> associateIndexed(ActionBiFunction<Integer, T, Pair<K, V>> association) {
        return this.associateIndexed(association, new HashMap<>());
    }

    @Override
    public <K, V> Maps<K, V> associate(ActionFunction<T, Pair<K, V>> association, Map<K, V> m) {
        for (T t : this.set) {
            Pair<K, V> pair = association.apply(t);
            m.put(pair.k(), pair.v());
        }
        return Maps.wrap(m);
    }

    @Override
    public <K, V> Maps<K, V> associateIndexed(ActionBiFunction<Integer, T, Pair<K, V>> association, Map<K, V> m) {
        int index = 0;
        for (T t : this.set) {
            Pair<K, V> pair = association.apply(index++, t);
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
    public <K> Maps<K, T> associateByIndexed(ActionBiFunction<Integer, T, K> keySelector) {
        int index = 0;
        Map<K, T> m = new HashMap<>();
        for (T t : this.set) {
            K k = keySelector.apply(index++, t);
            m.put(k, t);
        }
        return Maps.wrap(m);
    }

    @Override
    public <V> Maps<T, V> associateWithIndexed(ActionBiFunction<Integer, T, V> valueSelector) {
        int index = 0;
        Map<T, V> m = new HashMap<>();
        for (T t : this.set) {
            V v = valueSelector.apply(index++, t);
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

    public <X> Sets<X> map(ActionFunction<? super T, X> actionFunction) {
        Sets<X> xs = Sets.empty();
        for (T t : this.set) {
            X x = actionFunction.apply(t);
            xs.add(x);
        }
        return xs;
    }

    public <X> Sets<X> mapIndexed(ActionBiFunction<Integer, ? super T, X> actionFunction) {
        Sets<X> xs = Sets.empty();
        int index = 0;
        for (T t : this.set) {
            X x = actionFunction.apply(index, t);
            xs.add(x);
            index++;
        }
        return xs;
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
        return Sets.of(set);
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
    public <X> Sets<T> uniqueBy(ActionFunction<T, X> f) {
        return this.uniqueBy(f, null);
    }

    @Override
    public <X> Sets<T> uniqueBy(ActionFunction<T, X> f, ActionBiFunction<T, T, T> collisionSelector) {
        return Unique.uniqueBy(this, Sets::empty, f, collisionSelector);
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
        return Lists.from(this.set);
    }

    public Sets<T> subtract(Sets<T> others) {
        return this.filter(it -> !others.contains(it));
    }

    public Sets<T> union(Sets<T> others) {
        Sets<T> result = Sets.of(this.set);
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
        return true;
    }

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
        return Sets.of(set);
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
        return false;
    }


    public static <X> Sets<X> empty() {
        return new Sets<>(new HashSet<>());
    }

    public void print() {
        System.out.println(this.toString());
    }

    public Sets<T> clear() {
        this.set.clear();
        return this;
    }

    public Sets<T> remove(T t) {
        this.set.remove(t);
        return this;
    }
}
