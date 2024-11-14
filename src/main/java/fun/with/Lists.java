package fun.with;

import fun.with.interfaces.actions.*;
import fun.with.interfaces.Associate;
import fun.with.interfaces.CollectionLike;
import fun.with.misc.Checks;
import fun.with.misc.Pair;
import fun.with.misc.Unique;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Overview of 'creation' methods:
 * <ul>
 * <li>'from' methods take elements of something that is already some kind of iterable and hold its content in a new list.</li>
 * <li>'of' methods take one or more individual elements and put them into a list.</li>
 * <li>'wrap' methods just reference the list argument such that changes that occur to the argument list also affect the current instance of Lists.</li>
 * </ul>
 *  Generally speaking, all methods that modify, add or remove single elements and addAll() modify the internal list.
 *  Methods intended to modify multiple values or deliver sub lists, like map() or filter() return new lists.
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
        index = index < 0 ? this.size() + index : index;
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

    @Override
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

    public boolean allMatchIndexed(ActionBiPredicate<Integer, T> predicate) {
        int idx = 0;
        for (T t : this.ls) {
            if (!predicate.test(idx, t))
                return false;
            idx++;
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
    public Lists<T> nonNull() {
        return this.filter(Objects::nonNull);
    }

    @Override
    public Stream<T> stream() {
        return this.ls.stream();
    }

    public <X> Lists<X> map(ActionFunction<? super T, X> f) {
        List<X> ls = new ArrayList<>(this.ls.size());
        for (T t : this.ls) {
            ls.add(f.apply(t));
        }
        return Lists.wrap(ls);
    }

    public Lists<Lists<T>> reshape(Integer numRows) {
        Checks.check("list has size " + this.size() + " which is not divisible by " + numRows + ".", () -> (this.size() % numRows) == 0);
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
        Checks.check("Cannot call first() on an empty List.", () -> !this.isEmpty());
        return this.ls.get(0);
    }

    public T last() {
        Checks.check("Cannot call last() on an empty List.", () -> !this.isEmpty());
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
        if (this.size() < 2) {
            throw new RuntimeException("Cannot call second() on a List of size " + this.size() + ".");
        }
        return this.ls.get(1);
    }

    public T third() {
        if (this.size() < 3) {
            throw new RuntimeException("Cannot call second() on a List of size " + this.size() + ".");
        }
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

    /**
     * Get n random samples from this list.
     *
     * @param n
     * @return
     */
    public Lists<T> sample(int n) {
        Checks.check("Cannot sample " + n + " times from a list with " + this.size() + " elements", () -> this.size() >= n);
        List<T> ls = new ArrayList<>(this.ls);
        Collections.shuffle(ls);
        return Lists.wrap(ls).take(n);
    }

    /**
     * removes last element
     *
     * @return the element that was just removed
     */
    public T pop() {
        Checks.check("Cannot pop an empty list.", () -> !this.isEmpty());
        return this.ls.remove(this.size() - 1);
    }

    /**
     * removes first element
     *
     * @return the element that was just removed
     */
    public T popFirst() {
        Checks.check("Cannot pop an empty list.", () -> !this.isEmpty());
        return this.ls.remove(0);
    }

    public Lists<T> set(Integer idx, T value) {
        this.ls.set(idx, value);
        return this;
    }

    /**
     * Tests each element with the given predicate. Returns index if true.
     *
     * @param predicate
     * @return first index of predicate.test() evaluating to true or null.
     */
    public Integer indexOf(ActionPredicate<T> predicate) {
        int idx = 0;
        for (T l : this.ls) {
            if (predicate.test(l)) {
                return idx;
            }
            idx++;
        }
        return null;
    }

    /**
     * @param o
     * @return first index of o or null if not found
     */
    public Integer indexOf(Object o) {
        int idx = this.ls.indexOf(o);
        return idx >= 0 ? idx : null;
    }

    /**
     * Filter out all elements that are contained in others.
     *
     * @param others the elements to get rid of
     * @return a new {@link Lists} with no elements from others in it.
     */
    public Lists<T> subtract(CollectionLike<T, ?> others) {
        Set<T> set = new HashSet<>(others.getCollection());
        return this.filter(it -> !set.contains(it));
    }

    /**
     * Filter out all elements that are contained in others.
     *
     * @param others the elements to get rid of
     * @return a new {@link Lists} with no elements from others in it.
     */
    public Lists<T> subtract(Collection<T> others) {
        Set<T> set = new HashSet<>(others);
        return this.filter(it -> !set.contains(it));
    }

    /**
     * Fold this list from the left.
     *
     * @param x   start value
     * @param f
     * @param <X>
     * @return f(ls[2], f ( ls[1], f ( ls[0], x))) and so on.
     */
    public <X> X foldl(X x, ActionBiFunction<T, X, X> f) {
        for (T t : this.ls) {
            x = f.apply(t, x);
        }
        return x;
    }

    /**
     * Fold this list from the right.
     *
     * @param x   start value
     * @param f
     * @param <X>
     * @return f(ls[N - 2], f ( ls[N - 1], f ( ls[N], x))) and so on. Where N is last index of ls.
     */
    public <X> X foldr(X x, ActionBiFunction<T, X, X> f) {
        int idx = this.ls.size() - 1;
        while (idx >= 1) {
            x = f.apply(this.ls.get(idx), x);
            idx--;
        }
        return x;
    }

    /**
     * Put a new item in a certain position.
     *
     * @param idx
     * @param x
     * @return
     */
    public Lists<T> insert(int idx, T x) {
        this.ls.add(idx, x);
        return this;
    }

    public Lists<T> insert(int idx, Lists<T> xs) {
        this.ls.addAll(idx, xs.ls);
        return this;
    }

    public Lists<T> removeAt(int idx) {
        this.ls.remove(idx);
        return this;
    }

    public Lists<Lists<T>> partition(int parts) {
        int desiredSize = Math.ceilDiv(this.ls.size(), parts);
        Lists<Lists<T>> result = Lists.empty();
        int currentStartIdx = 0;
        for (int i = 1; i <= parts; i++) {
            int stopIdx = Math.min(this.size(), currentStartIdx + desiredSize);
            Lists<T> ts = this.subList(currentStartIdx, stopIdx);
            result.add(ts);
            currentStartIdx += desiredSize;
        }
        return result;
    }

    public Lists<Lists<T>> batch(int batchSize) {
        Checks.check("Batch size illegal: " + batchSize, () -> batchSize > 0);
        if (this.isEmpty())
            return Lists.of();
        Lists<Lists<T>> result = Lists.of(Lists.empty());
        Lists<T> current = result.first();
        int c = 0;
        int idx = 0;
        int maxIdx = this.size();
        for (T t : this.ls) {
            c++;
            idx++;
            current.add(t);
            if (c == batchSize) {
                c = 0;
                if (idx < maxIdx) {
                    current = result.add(Lists.empty()).last();
                }
            }
        }
        return result;
    }
}
