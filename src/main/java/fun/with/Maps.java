package fun.with;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
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

    public Lists<K> keys() {
        return Lists.wrap(this.m.keySet());
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

    public Maps<K, V> remove(K  k) {
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
}
