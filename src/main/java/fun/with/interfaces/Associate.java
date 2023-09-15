package fun.with.interfaces;

import fun.with.Lists;
import fun.with.Maps;
import fun.with.Pair;
import fun.with.actions.ActionBiFunction;
import fun.with.actions.ActionFunction;

import java.util.Map;
import java.util.function.Function;

public interface Associate<T> {

    /**
     * Associate by providing Key-Value-{@link Pair}s. Writes into an existing {@link Maps}.
     * @param association
     * @param m
     * @return
     * @param <K>
     * @param <V>
     */
    <K, V> Maps<K, V> associate(ActionFunction<T, Pair<K, V>> association, Map<K, V> m);
    <K, V> Maps<K, V> associateIndexed(ActionBiFunction<Integer,T, Pair<K, V>> association, Map<K, V> m);

    /**
     * Associate by providing Key-Value-{@link Pair}s.
     * @param association
     * @return
     * @param <K>
     * @param <V>
     */
    <K, V> Maps<K, V> associate(ActionFunction<T, Pair<K, V>> association);

    <K, V> Maps<K, V> associateIndexed(ActionBiFunction<Integer, T, Pair<K, V>> association);

    /**
     * Create a map by providing a key selector
     *
     * @param keySelector
     * @param <K>
     * @return
     */
    <K> Maps<K, T> associateBy(ActionFunction<T, K> keySelector);

    /**
     * Create a map by providing a value selector.
     * @param valueSelector
     * @return
     * @param <V>
     */
    <V> Maps<T, V> associateWith(ActionFunction<T, V> valueSelector);

    /**
     * Create a map by providing a constant value.
     * @param v
     * @return
     * @param <V>
     */
    <V> Maps<T, V> associateWith(V v);

    /**
     * Create a map by providing a key selector.
     * @param keySelector
     * @return
     * @param <K>
     */
    <K> Maps<K, T> associateByIndexed(ActionBiFunction<Integer, T, K> keySelector);

    /**
     * Create a map by providing a value selector.
     * @param valueSelector
     * @return
     * @param <V>
     */
    <V> Maps<T, V> associateWithIndexed(ActionBiFunction<Integer, T, V> valueSelector);

    <K> Maps<K, Lists<T>> groupBy(ActionFunction<T, K> keySelector);

    <K, V> Maps<K, Lists<V>> groupBy(ActionFunction<T, K> keySelector, ActionFunction<T, V> valueSelector);
}
