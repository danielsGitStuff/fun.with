package fun.with;

public class ChainablePair<K, V> {

    Lists<Pair<K, V>> elements = Lists.empty();

    public ChainablePair<K, V> add(Pair<K, V> pair) {
        elements.add(pair);
        return this;
    }

    public ChainablePair<K, V> and(K k, V v) {
        return this.add(Pair.of(k, v));
    }


    public Pair<K, V>[] get() {
        Pair<K, V>[] arr = new Pair[this.elements.size()];
        this.elements.forEachIndexed((i, pair) -> arr[i] = pair);
        return arr;
    }
}
