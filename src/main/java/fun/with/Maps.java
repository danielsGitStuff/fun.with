package fun.with;

import fun.with.interfaces.actions.ActionBiConsumer;
import fun.with.interfaces.actions.ActionBiFunction;
import fun.with.interfaces.actions.ActionFunction;
import fun.with.interfaces.actions.ActionTriConsumer;
import fun.with.misc.Pair;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class Maps<K, V> {

    private final Map<K, V> m;

    public Maps(Map<K, V> m) {
        this.m = m;
    }

    public static <K, V> Maps<K, V> of(Pair<K, V>... pairs) {
        Map<K, V> map = new HashMap<>();
        for (Pair<K, V> p : pairs)
            map.put(p.k(), p.v());
        return Maps.wrap(map);
    }

    public static <K, V> Maps<K, V> wrap(Map<K, V> map) {
        return new Maps<>(map);
    }

    public static <K, V> Maps<K, V> empty() {
        return Maps.wrap(new HashMap<>());
    }

    public static <K, V> Maps<K, V> empty(Class<K> klass, Class<V> vlass) {
        return Maps.wrap(new HashMap<>());
    }

    public Map<K, V> get() {
        return this.m;
    }

    public Lists<V> values() {
        return Lists.from(this.m.values());
    }

    public Sets<K> keySet() {
        return Sets.of(this.m.keySet());
    }

    public <KK, VV> Maps<KK, VV> map(ActionBiFunction<K, V, Pair<KK, VV>> f) {
        Map<KK, VV> m = this.newMap();
        for (Map.Entry<K, V> e : this.m.entrySet()) {
            Pair<KK, VV> pair = f.apply(e.getKey(), e.getValue());
            m.put(pair.k(), pair.v());
        }
        return Maps.wrap(m);
    }

    private <KK,VV> Map<KK, VV> newMap() {
        if (this.m instanceof LinkedHashMap) {
            return new LinkedHashMap<>();
        } else if (this.m instanceof HashMap) {
            return new HashMap<>();
        } else {
            throw new RuntimeException("Got some strange map: " + this.m.getClass().getSimpleName());
        }
    }


    public V get(K k) {
        return m.get(k);
    }

    public Maps<K, V> put(K k, V v) {
        this.m.put(k, v);
        return this;
    }

    public Maps<K, V> remove(K k) {
        this.m.remove(k);
        return this;
    }

    public Maps<K, V> putAll(Map<? extends K, ? extends V> map) {
        m.putAll(map);
        return this;
    }

    public Maps<K, V> putAll(Maps<? extends K, ? extends V> map) {
        return this.putAll(map.m);
    }

    public Maps<K, V> addTo(Map<K, V> map) {
        map.putAll(this.m);
        return this;
    }

    public Maps<K, V> replace(K k, V v) {
        this.m.replace(k, v);
        return this;
    }

    public V computeIfAbsent(K k, ActionFunction<? super K, ? extends V> mappingFunction) {
        return this.m.computeIfAbsent(k, mappingFunction::apply);
    }

    public Maps<K, V> putIfAbsent(K k, ActionFunction<? super K, ? extends V> function) {
        this.m.computeIfAbsent(k, function::apply);
        return this;
    }

    public Maps<K, V> putIfPresent(K k, ActionBiFunction<? super K, ? super V, ? extends V> biFunction) {
        this.m.computeIfPresent(k, biFunction::apply);
        return this;
    }

    public Maps<K, V> putComputation(K k, ActionBiFunction<? super K, ? super V, ? extends V> biFunction) {
        this.m.compute(k, biFunction::apply);
        return this;
    }

    public V compute(K k, ActionBiFunction<? super K, ? super V, ? extends V> biFunction) {
        return this.m.compute(k, biFunction::apply);
    }

    public Maps<K, V> merge(K k, V v, BiFunction<? super V, ? super V, ? extends V> biFunction) {
        this.m.merge(k, v, biFunction);
        return this;
    }

    public boolean containsKey(K k) {
        return m.containsKey(k);
    }

    public Maps<K, V> clear() {
        m.clear();
        return this;
    }

    public V getOrDefault(K k, V v) {
        return m.getOrDefault(k, v);
    }

    public int size() {
        return this.m.size();
    }

    @Override
    public String toString() {
        if (this.isEmpty()) return "M(0){}";
        StringBuilder b = new StringBuilder("M(").append(this.size()).append("){");
        final int lastIndex = this.m.size() - 1;
        int index = 0;
        for (Map.Entry<K, V> e : this.m.entrySet()) {
            b.append(e.getKey()).append("->").append(e.getValue());
            if (index != lastIndex)
                b.append(",");
            index++;
        }
        return b.append("}").toString();
    }

    public boolean isEmpty() {
        return this.m.isEmpty();
    }

    public Maps<K, V> filter(BiPredicate<K, V> predicate) {
        Map<K, V> m = this.newMap();
        for (Map.Entry<K, V> e : this.m.entrySet()) {
            if (predicate.test(e.getKey(), e.getValue()))
                m.put(e.getKey(), e.getValue());
        }
        return Maps.wrap(m);
    }

    /**
     * replaces all the content of targetMap with the current one
     *
     * @param targetMap
     * @return
     */
    public Maps<K, V> toMap(Map<K, V> targetMap) {
        if (targetMap == this.m)
            return this;
        targetMap.clear();
        targetMap.putAll(this.m);
        return this;
    }

    public Maps<K, V> keep(K... ks) {
        Map<K, V> m = this.newMap();
        for (K k : ks) {
            if (this.m.containsKey(k))
                m.put(k, this.m.get(k));
        }
        return Maps.wrap(m);
    }

    public Maps<K, V> intersection(Set<K> other) {
        Map<K, V> m = this.newMap();
        for (K k : other) {
            if (this.m.containsKey(k))
                m.put(k, this.m.get(k));
        }
        return Maps.wrap(m);
    }

    public Maps<K, V> intersection(Sets<K> other) {
        return this.intersection(other.get());
    }

    public Maps<K, V> intersection(Map<K, V> other) {
        return this.intersection(other.keySet());
    }

    public Maps<K, V> intersection(Maps<K, V> other) {
        return this.intersection(other.keySet().get());
    }

    public Maps<K, V> forEach(ActionBiConsumer<K, V> biConsumer) {
        for (Map.Entry<K, V> e : this.m.entrySet()) {
            biConsumer.accept(e.getKey(), e.getValue());
        }
        return this;
    }

    public Maps<K, V> forEachIndexed(ActionTriConsumer<Integer, K, V> biConsumer) {
        int index = 0;
        for (Map.Entry<K, V> e : this.m.entrySet()) {
            biConsumer.accept(index, e.getKey(), e.getValue());
            index++;
        }
        return this;
    }

    public <TT> Lists<TT> ls(BiFunction<K, V, TT> f) {
        List<TT> ls = new ArrayList<>(this.m.size());
        for (Map.Entry<K, V> e : this.m.entrySet()) {
            ls.add(f.apply(e.getKey(), e.getValue()));
        }
        return Lists.wrap(ls);
    }

    public Lists<Pair<K, V>> ls() {
        Lists<Pair<K, V>> ls = Lists.empty();
        for (Map.Entry<K, V> e : this.m.entrySet()) {
            ls.add(Pair.of(e.getKey(), e.getValue()));
        }
        return ls;
    }

    public Maps<K, V> copy() {
        Map<K, V> m = this.newMap();
        m.putAll(this.m);
        return Maps.wrap(m);
    }

    public String join(String limiter, String keyValueSeparator) {
        return this.join(limiter, keyValueSeparator, "\"", "\"");
    }

    public String join(String limiter, String keyValueSeparator, String keyWrapper, String valueWrapper) {
        StringBuilder b = new StringBuilder();
        this.forEachIndexed((integer, k, v) -> {
            b.append(keyWrapper).append(k).append(keyWrapper).append(keyValueSeparator).append(valueWrapper).append(v).append(valueWrapper);
            if (integer < this.size() - 1) {
                b.append(limiter);
            }
        });
        return b.toString();
    }

    public <X, Y> Maps<X, Y> cast(Class<X> x, Class<Y> y) {
        return this.map((k, v) -> Pair.of((X) k, (Y) v));
    }

    public <X> Maps<X, V> mapKeys(ActionBiFunction<K, V, X> f) {
        Map<X, V> xm = this.newMap();
        this.m.forEach((key, value) -> xm.put(f.apply(key, value), value));
        return Maps.wrap(xm);
    }

    public <X> Maps<K, X> mapValues(ActionBiFunction<K, V, X> f) {
        Map<K, X> xm = this.newMap();
        this.m.forEach((key, value) -> xm.put(key, f.apply(key, value)));
        return Maps.wrap(xm);
    }

    public Integer deepHash() {
        return this.deepHash(new HashSet<>());
    }

    private Integer deepHash(Set<Object> visited) {
        int hash = 0;
        for (K k : this.m.keySet()) {
            if (!visited.contains(k)) {
                if (k instanceof Maps<?, ?>)
                    hash += ((Maps<?, ?>) k).deepHash(visited);
                else
                    hash += k.hashCode();
            }
            V v = this.m.get(k);
            if (v == null)
                hash++;
            else {
                if (!visited.contains(v)) {
                    if (v instanceof Maps<?, ?>)
                        hash += ((Maps<?, ?>) v).deepHash(visited);
                    else
                        hash += v.hashCode();
                }
            }
        }
        return hash;
    }
}
