package fun.with;

import fun.with.interfaces.Associate;
import fun.with.interfaces.CollectionLike;

import java.util.*;
import java.util.function.*;

public class Lists<T> implements CollectionLike<T, Lists<T>>, Associate<T> {

    private final List<T> ls;

    private Lists(List<T> ls) {
        this.ls = ls;
    }

    public static <X> Lists<X> wrap(Collection<X> xs) {
        return new Lists<>(new ArrayList<>(xs));
    }

    public static <X, Y> Lists<Pair<X, Y>> zip(Lists<X> xs, Lists<Y> ys) {
        return xs.mapIndexed((index, x) -> Pair.of(x, ys.get(index)));
    }

    public <X> Lists<X> mapIndexed(BiFunction<Integer, ? super T, X> f) {
        List<X> ls = new ArrayList<>(this.ls.size());
        int index = 0;
        for (T t : this.ls) {
            ls.add(f.apply(index, t));
            index++;
        }
        return Lists.wrap(ls);
    }

    public T get(int index) {
        return this.ls.get(index);
    }

    public static <X> Lists<X> wrap(List<X> xs) {
        return new Lists<>(xs);
    }

    public static <X, Y> Lists<Pair<X, Y>> zip(List<X> xs, Lists<Y> ys) {
        return Lists.wrap(xs).mapIndexed((index, x) -> Pair.of(x, ys.get(index)));
    }

    public static <X, Y> Lists<Pair<X, Y>> zip(Lists<X> xs, List<Y> ys) {
        return xs.mapIndexed((index, x) -> Pair.of(x, ys.get(index)));
    }

    public static <X, Y> Lists<Pair<X, Y>> zip(List<X> xs, List<Y> ys) {
        return Lists.wrap(xs).mapIndexed((index, x) -> Pair.of(x, ys.get(index)));
    }

    public static <X> Lists<X> wrap(Iterator<X> xs) {
        List<X> ls = new ArrayList<>();
        while (xs.hasNext()) {
            ls.add(xs.next());
        }
        return new Lists<>(ls);
    }

    public static <X> Lists<X> wrap(X[] xs) {
        return new Lists<>(Arrays.asList(xs));
    }

    public static <X> Lists<X> of(X... xs) {
        return new Lists<>(Arrays.asList(xs));
    }

    public String join(final String separator) {
        StringBuilder b = new StringBuilder();
        forEachIndexed((i, t) -> {
            b.append(t);
            if (i < this.size() - 1) {
                b.append(separator);
            }
        });
        return b.toString();
    }

    public <X> Lists<Pair<T, X>> zip(Lists<X> other) {
        if (other == null)
            throw new RuntimeException("other list was null");
        return this.zip(other.ls);
    }

    public <X> Lists<Pair<T, X>> zip(List<X> other) {
        if (other == null)
            throw new RuntimeException("other list was null");
        if (this.ls.size() != other.size())
            throw new RuntimeException("other list had size " + other.size() + " but was expected to be " + this.size());
        Lists<Pair<T, X>> ls = Lists.wrap(new ArrayList<>(this.size()));
        int index = 0;
        for (T t : this.ls) {
            ls.ls.add(new Pair<>(t, other.get(index)));
            index++;
        }
        return ls;
    }

    public <R> Lists<R> flatMap(Function<T, Lists<R>> f) {
        Lists<R> ls = Lists.empty();
        for (T t : this.ls) {
            ls.addAll(f.apply(t));
        }
        return ls;
    }

    public static <X> Lists<X> empty() {
        return new Lists<>(new ArrayList<>());
    }

    public List<T> get() {
        return this.ls;
    }

    /**
     * @param n how many elements to return
     * @return the first n elements
     */
    public Lists<T> take(int n) {
        return Lists.wrap(this.ls.subList(0, n));
    }

    public Lists<T> subList(int start, int stop) {
        return Lists.wrap(this.ls.subList(start, stop));
    }

    public Lists<T> reverse() {
        List<T> ls = new ArrayList<>(this.ls.size());
        if (this.isEmpty()) {
            return Lists.empty();
        }
        for (int i = this.ls.size() - 1; i >= 0; i--) {
            ls.add(this.ls.get(i));
        }
        return Lists.wrap(ls);
    }

    @Override
    public Lists<T> forEach(Consumer<? super T> consumer) {
        for (T t : this.ls) {
            consumer.accept(t);
        }
        return this;
    }

    @Override
    public Lists<T> forEachIndexed(BiConsumer<Integer, ? super T> consumer) {
        int index = 0;
        for (T t : this.ls) {
            consumer.accept(index, t);
            index++;
        }
        return this;
    }

    @Override
    public Lists<T> filter(Predicate<? super T> predicate) {
        List<T> ls = new ArrayList<>();
        for (T t : this.ls) {
            if (predicate.test(t)) {
                ls.add(t);
            }
        }
        return Lists.wrap(ls);
    }

    @Override
    public Lists<T> add(T t) {
        this.ls.add(t);
        return this;
    }

    @Override
    public Lists<T> addAll(Collection<T> ts) {
        this.ls.addAll(ts);
        return this;
    }

    @Override
    public Lists<T> addAll(CollectionLike<T, ?> ts) {
        this.ls.addAll(ts.getCollection());
        return this;
    }

    @Override
    public Lists<T> addAll(Lists<T> ts) {
        this.ls.addAll(ts.ls);
        return this;
    }

    @Override
    public Lists<T> addTo(CollectionLike<T, Lists<T>> other) {
        other.addAll(this);
        return this;
    }

    @Override
    public Lists<T> unique() {
        return Lists.wrap(new LinkedHashSet<>(this.ls));
    }

    public UniqueLists<T> toUniqueLists() {
        return UniqueLists.wrap(this.ls);
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
        return this.ls.contains(t);
    }

    @Override
    public Collection<T> getCollection() {
        return this.ls;
    }

    public <X> Lists<X> map(Function<? super T, X> f) {
        List<X> ls = new ArrayList<>(this.ls.size());
        for (T t : this.ls) {
            ls.add(f.apply(t));
        }
        return Lists.wrap(ls);
    }

    @Override
    public <K, V> Maps<K, V> associate(Function<T, Pair<K, V>> association) {
        Map<K, V> m = new HashMap<>();
        for (T t : this.ls) {
            Pair<K, V> p = association.apply(t);
            m.put(p.k(), p.v());
        }
        return Maps.wrap(m);
    }

    @Override
    public <K> Maps<K, T> associateBy(Function<T, K> keySelector) {
        Map<K, T> m = new HashMap<>();
        for (T t : this.ls) {
            m.put(keySelector.apply(t), t);
        }
        return Maps.wrap(m);
    }

    @Override
    public <V> Maps<T, V> associateWith(Function<T, V> valueSelector) {
        Map<T, V> m = new HashMap<>();
        for (T t : this.ls) {
            m.put(t, valueSelector.apply(t));
        }
        return Maps.wrap(m);
    }

    @Override
    public <V> Maps<T, V> associateWith(V v) {
        Map<T, V> m = new HashMap<>();
        for (T t : this.ls) {
            m.put(t, v);
        }
        return Maps.wrap(m);
    }

    @Override
    public <K> Maps<K, Lists<T>> groupBy(Function<T, K> keySelector) {
        Map<K, Lists<T>> m = new HashMap<>();
        for (T t : this.ls) {
            K k = keySelector.apply(t);
            m.computeIfAbsent(k, k1 -> Lists.empty());
            m.get(k).add(t);
        }
        return Maps.wrap(m);
    }

    @Override
    public <K, V> Maps<K, Lists<V>> groupBy(Function<T, K> keySelector, Function<T, V> valueSelector) {
        Map<K, Lists<V>> m = new HashMap<>();
        for (T t : this.ls) {
            K k = keySelector.apply(t);
            V v = valueSelector.apply(t);
            m.computeIfAbsent(k, k1 -> Lists.empty());
            m.get(k).add(v);
        }
        return Maps.wrap(m);
    }

    public <X extends Comparable<X>> Lists<T> sortBy(Function<T, X> propertySelector) {
        Comparator<T> comparator = (a, b) -> {
            X aa = propertySelector.apply(a);
            X bb = propertySelector.apply(b);
            return aa.compareTo(bb);
        };
        return this.sort(comparator);
    }

    public Lists<T> sort(Comparator<T> comparator) {
        List<T> ls = new ArrayList<>(this.ls);
        ls.sort(comparator);
        return Lists.wrap(ls);
    }

    public Permutations<T> permute() {
        return new Permutations<T>(this);
    }

    @Override
    public int hashCode() {
        return ls.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Lists<?> lists = (Lists<?>) o;

        return ls.equals(lists.ls);
    }

    @Override
    public String toString() {
        if (isEmpty()) return "[]";
        StringBuilder b = new StringBuilder("[");
        final int lastIndex = this.ls.size() - 1;
        int index = 0;
        for (T t : this.ls) {
            b.append(t);
            if (index != lastIndex)
                b.append(",");
            index++;
        }
        return b.append("]").toString();
    }

    public T first() {
        return this.ls.get(0);
    }

    public T last() {
        return this.ls.get(this.size() - 1);
    }

    public Lists<T> repeat(int n) {
        List<T> ls = new ArrayList<>(this.size() * n);
        for (int i = 0; i < n; i++) {
            ls.addAll(this.ls);
        }
        return Lists.wrap(ls);
    }

    public Sets<T> sets() {
        return Sets.wrap(this.ls);
    }

    public Lists<T> copy() {
        return Lists.wrap(new ArrayList<>(this.ls));
    }

    /**
     * @param n how many elements to drop
     * @return all but the first n elements
     */
    public Lists<T> drop(int n) {
        return Lists.wrap(ls.subList(n, ls.size()));
    }
}
