package fun.with.interfaces;

import fun.with.Lists;
import fun.with.Maps;
import fun.with.Pair;
import fun.with.actions.ActionFunction;

import java.util.Map;
import java.util.function.Function;

public interface Associate<T> {

    <K, V> Maps<K, V> associate(ActionFunction<T, Pair<K, V>> association, Map<K, V> m);

    <K, V> Maps<K, V> associate(ActionFunction<T, Pair<K, V>> association);

  <K> Maps<K, T> associateBy(ActionFunction<T, K> keySelector);

  <V> Maps<T, V> associateWith(ActionFunction<T, V> valueSelector);

  <V> Maps<T, V> associateWith(V v);

  <K> Maps<K, Lists<T>> groupBy(ActionFunction<T, K> keySelector);

  <K, V> Maps<K, Lists<V>> groupBy(ActionFunction<T, K> keySelector, ActionFunction<T, V> valueSelector);
}
