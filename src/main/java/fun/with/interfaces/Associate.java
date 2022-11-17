package fun.with.interfaces;

import fun.with.Lists;
import fun.with.Maps;
import fun.with.Pair;

import java.util.function.Function;

public interface Associate<T> {

  <K, V> Maps<K, V> associate(Function<T, Pair<K, V>> association);

  <K> Maps<K, T> associateBy(Function<T, K> keySelector);

  <V> Maps<T, V> associateWith(Function<T, V> valueSelector);

  <V> Maps<T, V> associateWith(V v);

  <K> Maps<K, Lists<T>> groupBy(Function<T, K> keySelector);

  <K, V> Maps<K, Lists<V>> groupBy(Function<T, K> keySelector, Function<T, V> valueSelector);
}
