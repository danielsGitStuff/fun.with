package fun.with;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

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

    public Map<K, V> get() {
        return this.m;
    }

    public Lists<V> values() {
        return Lists.wrap(this.m.values());
    }

    public Sets<K> keySet() {
        return Sets.wrap(this.m.keySet());
    }

    public <KK, VV> Maps<KK, VV> map(BiFunction<K, V, Pair<KK, VV>> f) {
        Map<KK, VV> m = new HashMap<>();
        for (Map.Entry<K, V> e : this.m.entrySet()) {
            Pair<KK, VV> pair = f.apply(e.getKey(), e.getValue());
            m.put(pair.k(), pair.v());
        }
        return Maps.wrap(m);
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

    public Maps<K, V> replace(K k, V v) {
        this.m.replace(k, v);
        return this;
    }

    public Maps<K, V> computeIfAbsent(K k, Function<? super K, ? extends V> function) {
        this.m.computeIfAbsent(k, function);
        return this;
    }

    public Maps<K, V> computeIfPresent(K k, BiFunction<? super K, ? super V, ? extends V> biFunction) {
        this.m.computeIfPresent(k, biFunction);
        return this;
    }

    public Maps<K, V> compute(K k, BiFunction<? super K, ? super V, ? extends V> biFunction) {
        this.m.compute(k, biFunction);
        return this;
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
            b.append(e.getKey()).append("=>").append(e.getValue());
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
        Map<K, V> m = new HashMap<>();
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
        targetMap.clear();
        targetMap.putAll(this.m);
        return this;
    }

    public Maps<K, V> keep(K... ks) {
        Map<K, V> m = new HashMap<>();
        for (K k : ks) {
            if (this.m.containsKey(k))
                m.put(k, this.m.get(k));
        }
        return Maps.wrap(m);
    }

    public Maps<K, V> intersection(Set<K> other) {
        Map<K, V> m = new HashMap<>();
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
}
