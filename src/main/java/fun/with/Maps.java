package fun.with;

import java.util.HashMap;
import java.util.Map;

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

    public Lists<V> values(){
        return Lists.wrap(this.m.values());
    }
    public Lists<K> keys(){
        return Lists.wrap(this.m.keySet());
    }
}
