package fun.with;

import java.util.Objects;

public class Pair<K, V> {
    private K k;
    private V v;

    public Pair(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public Pair<K, V> k(K k) {
        this.k = k;
        return this;
    }

    public Pair<K, V> v(V v) {
        this.v = v;
        return this;
    }

    public K k() {
        return this.k;
    }

    public V v() {
        return this.v;
    }

    public Pair<V, K> flip() {
        return Pair.of(this.v, this.k);
    }

    public static <K, V> Pair<K, V> of(K k, V v) {
        return new Pair<>(k, v);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(k, pair.k) && Objects.equals(v, pair.v);
    }

    @Override
    public int hashCode() {
        return Objects.hash(k, v);
    }
}
