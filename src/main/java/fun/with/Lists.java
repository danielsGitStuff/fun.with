package fun.with;

import fun.with.actions.*;
import fun.with.interfaces.Associate;
import fun.with.interfaces.CollectionLike;
import fun.with.misc.Unique;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * <ul>
 * <li>'from' methods take elements of something that is already some kind of iterable and hold its content in a new list.</li>
 * <li>'of' methods take one or more individual elements and put them into a list.</li>
 * <li>'wrap' methods just reference the list argument such that changes that occur to the argument list also affect the current instance of Lists.</li>
 * </ul>
 *
 * @param <T>
 */
public class Lists<T> implements CollectionLike<T, Lists<T>>, Associate<T> {

    public static <X> Collector<X, ?, Lists<X>> collect() {
        return new Collector<X, Lists<X>, Lists<X>>() {
            @Override
            public Supplier<Lists<X>> supplier() {
                return Lists::empty;
            }

            @Override
            public BiConsumer<Lists<X>, X> accumulator() {
                return Lists::add;
            }

            @Override
            public BinaryOperator<Lists<X>> combiner() {
                return (left, right) -> {
                    left.addAll(right);
                    return left;
                };
            }

            @Override
            public Function<Lists<X>, Lists<X>> finisher() {
                return xLists -> xLists;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));
            }
        };
    }

    private final List<T> ls;

    private Lists(List<T> ls) {
        this.ls = ls;
    }


    public static <X, Y> Lists<Pair<X, Y>> zip(Lists<X> xs, Lists<Y> ys) {
        return xs.mapIndexed((index, x) -> Pair.of(x, ys.get(index)));
    }

    public <X> Lists<X> mapIndexed(ActionBiFunction<Integer, ? super T, X> f) {
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


    public static <X, Y> Lists<Pair<X, Y>> zip(List<X> xs, Lists<Y> ys) {
        return Lists.wrap(xs).mapIndexed((index, x) -> Pair.of(x, ys.get(index)));
    }

    public static <X, Y> Lists<Pair<X, Y>> zip(Lists<X> xs, List<Y> ys) {
        return xs.mapIndexed((index, x) -> Pair.of(x, ys.get(index)));
    }

    public static <X, Y> Lists<Pair<X, Y>> zip(List<X> xs, List<Y> ys) {
        return Lists.wrap(xs).mapIndexed((index, x) -> Pair.of(x, ys.get(index)));
    }

    /**
     * creates a new {@link Lists} with everything still in the iterator
     *
     * @param xs
     * @param <X>
     * @return
     */
    public static <X> Lists<X> from(Iterator<X> xs) {
        List<X> ls = new ArrayList<>();
        while (xs.hasNext()) {
            ls.add(xs.next());
        }
        return new Lists<>(ls);
    }

    /**
     * Uses xs as the underlying list. Changes to that list also occur in here though.
     *
     * @param xs
     * @param <X>
     * @return
     */
    public static <X> Lists<X> wrap(List<X> xs) {
        return new Lists<>(xs);
    }

    public static <X> Lists<X> wrap(Collection<X> xs) {
        return Lists.wrap(new ArrayList<>(xs));
    }

    /**
     * Creates a new list with everything in xs. No reference to xs is kept.
     *
     * @param xs
     * @param <X>
     * @return
     */
    public static <X> Lists<X> from(Collection<X> xs) {
        return Lists.wrap(new ArrayList<>(xs));
    }

    /**
     * Creates a new list with everything in xs.
     *
     * @param xs
     * @param <X>
     * @return
     */
    public static <X> Lists<X> of(X... xs) {
        return new Lists<>(new ArrayList<>(Arrays.asList(xs)));
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

    public <R> Lists<R> flatMap(ActionFunction<T, Lists<R>> f) {
        Lists<R> ls = Lists.empty();
        for (T t : this.ls) {
            ls.addAll(f.apply(t));
        }
        return ls;
    }

    /**
     * Creates an empty list.
     *
     * @param <X>
     * @return
     */
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
    public Lists<T> forEach(ActionConsumer<? super T> consumer) {
        for (T t : this.ls) {
            consumer.accept(t);
        }
        return this;
    }

    @Override
    public Lists<T> forEachIndexed(ActionBiConsumer<Integer, ? super T> consumer) {
        int index = 0;
        for (T t : this.ls) {
            consumer.accept(index, t);
            index++;
        }
        return this;
    }

    @Override
    public Lists<T> filter(ActionPredicate<? super T> predicate) {
        List<T> ls = new ArrayList<>();
        for (T t : this.ls) {
            if (predicate.test(t)) {
                ls.add(t);
            }
        }
        return Lists.wrap(ls);
    }

    public Lists<T> filterIndexed(ActionBiPredicate<Integer, ? super T> predicate) {
        List<T> ls = new ArrayList<>();
        int index = 0;
        for (T t : this.ls) {
            if (predicate.test(index++, t)) {
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
        return Lists.from(new LinkedHashSet<>(this.ls));
    }

    @Override
    public <X> Lists<T> uniqueBy(ActionFunction<T, X> f) {
        return this.uniqueBy(f, null);
    }

    @Override
    public <X> Lists<T> uniqueBy(ActionFunction<T, X> f, ActionBiFunction<T, T, T> collisionSelector) {
        return Unique.uniqueBy(this, Lists::empty, f, collisionSelector);
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

    public <X> Lists<X> map(ActionFunction<? super T, X> f) {
        List<X> ls = new ArrayList<>(this.ls.size());
        for (T t : this.ls) {
            ls.add(f.apply(t));
        }
        return Lists.wrap(ls);
    }

    public Lists<Lists<T>> reshape(Integer numRows) {
        if (!(this.size() % numRows == 0))
            throw new RuntimeException("list has size " + this.size() + " which is not divisible by " + numRows + ".");
        Lists<Lists<T>> yys = Lists.empty();
        int count = 0;
        Lists<T> ys = Lists.empty();
        for (T t : this.ls) {
            ys.add(t);
            count++;
            if (count >= numRows) {
                yys.add(ys);
                ys = Lists.empty();
                count = 0;
            }
        }
        return yys;
    }

    @Override
    public <K, V> Maps<K, V> associate(ActionFunction<T, Pair<K, V>> association, Map<K, V> m) {
        for (T t : this.ls) {
            Pair<K, V> p = association.apply(t);
            m.put(p.k(), p.v());
        }
        return Maps.wrap(m);
    }

    @Override
    public <K, V> Maps<K, V> associateIndexed(ActionBiFunction<Integer, T, Pair<K, V>> association, Map<K, V> m) {
        int index = 0;
        for (T t : this.ls) {
            Pair<K, V> p = association.apply(index++, t);
            m.put(p.k(), p.v());
        }
        return Maps.wrap(m);
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
    public <K> Maps<K, T> associateBy(ActionFunction<T, K> keySelector) {
        Map<K, T> m = new HashMap<>();
        for (T t : this.ls) {
            m.put(keySelector.apply(t), t);
        }
        return Maps.wrap(m);
    }

    @Override
    public <V> Maps<T, V> associateWith(ActionFunction<T, V> valueSelector) {
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
    public <K> Maps<K, T> associateByIndexed(ActionBiFunction<Integer, T, K> keySelector) {
        int index = 0;
        Map<K, T> m = new HashMap<>();
        for (T t : this.ls) {
            m.put(keySelector.apply(index++, t), t);
        }
        return Maps.wrap(m);
    }

    @Override
    public <V> Maps<T, V> associateWithIndexed(ActionBiFunction<Integer, T, V> valueSelector) {
        int index = 0;
        Map<T, V> m = new HashMap<>();
        for (T t : this.ls) {
            m.put(t, valueSelector.apply(index++, t));
        }
        return Maps.wrap(m);
    }

    @Override
    public <K> Maps<K, Lists<T>> groupBy(ActionFunction<T, K> keySelector) {
        Map<K, Lists<T>> m = new HashMap<>();
        for (T t : this.ls) {
            K k = keySelector.apply(t);
            m.computeIfAbsent(k, k1 -> Lists.empty());
            m.get(k).add(t);
        }
        return Maps.wrap(m);
    }

    @Override
    public <K, V> Maps<K, Lists<V>> groupBy(ActionFunction<T, K> keySelector, ActionFunction<T, V> valueSelector) {
        Map<K, Lists<V>> m = new HashMap<>();
        for (T t : this.ls) {
            K k = keySelector.apply(t);
            V v = valueSelector.apply(t);
            m.computeIfAbsent(k, k1 -> Lists.empty());
            m.get(k).add(v);
        }
        return Maps.wrap(m);
    }

    public <X extends Comparable<X>> Lists<T> sortBy(ActionFunction<T, X> propertySelector) {
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
        if (isEmpty()) return "L(0)[]";
        StringBuilder b = new StringBuilder("l(").append(this.size()).append(")[");
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
        return Sets.of(this.ls);
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

    public Lists<T> intersection(CollectionLike<T, ?> other) {
        return this.intersection(other.getCollection());
    }

    public Lists<T> intersection(Collection<T> other) {
        List<T> ls = new ArrayList<>();
        Set<T> set = new HashSet<>(other);
        for (T t : this.ls) {
            if (set.contains(t))
                ls.add(t);
        }
        return Lists.wrap(ls);
    }

    public Lists<T> intersection(T... ts) {
        Set<T> set = new HashSet<>(Arrays.asList(ts));
        return this.intersection(set);
    }

    public T second() {
        return this.ls.get(1);
    }

    public T third() {
        return this.ls.get(2);
    }

    public <X> Lists<X> cast(Class<X> clazz) {
        return this.map(t -> (X) t);
    }

    public <X> Lists<T> applySequence(Lists<X> xs, ActionBiFunction<X, T, T> f) {
        List<T> ys = new ArrayList<>(this.ls.size());
        for (T t : this.ls) {
            T current = t;
            for (X x : xs.ls) {
                current = f.apply(x, current);
            }
            ys.add(current);
        }
        return Lists.wrap(ys);
    }

    public <X> X applyTo(X x, ActionBiFunction<X, T, X> f) {
        X current = x;
        for (T t : this.ls) {
            current = f.apply(current, t);
        }
        return current;
    }

    /**
     * Get n random samples from this list.
     *
     * @param n
     * @return
     */
    public Lists<T> sample(int n) {
        if (this.size() < n) throw new RuntimeException("Cannot sample " + n + " times from a list with " + this.size() + " elements");
        List<T> ls = new ArrayList<>(this.ls);
        Collections.shuffle(ls);
        return Lists.wrap(ls).take(n);
    }

    public Double sum(ActionFunction<T, Double> f) {
        Double sum = 0.0;
        for (T t : this.ls) {
            sum += f.apply(t);
        }
        return sum;
    }
}
