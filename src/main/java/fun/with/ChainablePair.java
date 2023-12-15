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

    public<X,Y> ChainablePair<X,Y> cast(Class<X> x, Class<Y> y) {
        ChainablePair<X,Y> cp = new ChainablePair<>();
        this.elements.forEach(pair -> cp.add(Pair.of((X) pair.k(), (Y) pair.v())));
        return cp;
    }
}
