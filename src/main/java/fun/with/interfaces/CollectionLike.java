package fun.with.interfaces;

import fun.with.Lists;
import fun.with.interfaces.actions.*;

import java.util.Collection;
import java.util.Iterator;

public interface CollectionLike<T, Re> {

    <R> Lists<R> flatMap(ActionFunction<T, Lists<R>> f);

    /**
     * ForEach
     *
     * @param consumer
     * @return this
     */
    Re forEach(ActionConsumer<? super T> consumer);

    /**
     * ForEach but with an index
     *
     * @param consumer
     * @return this
     */
    Re forEachIndexed(ActionBiConsumer<Integer, ? super T> consumer);

    /**
     * Returns new CollectionLike with elements removed where predicate returns false.
     *
     * @param predicate when it returns true the element is in the result
     * @return filtered CollectionLike
     */
    Re filter(ActionPredicate<? super T> predicate);

    /**
     * appends to this CollectionLike
     *
     * @param t new element
     * @return this
     */
    Re add(T t);

    /**
     * appends to this CollectionLike
     *
     * @param ts elements to append
     * @return this
     */
    Re addAll(CollectionLike<T, ?> ts);

    /**
     * appends to this CollectionLike
     *
     * @param ts elements to append
     * @return this
     */
    Re addAll(Collection<T> ts);

    /**
     * appends to this CollectionLike
     *
     * @param ts elements to append
     * @return this
     */
    Re addAll(Lists<T> ts);

    /**
     * append this CollectionLike to another
     *
     * @param other CollectionLike to add to
     * @return this
     */
    Re addTo(CollectionLike<T, Re> other);

    /**
     * remove all copies of elements in this CollectionLike
     *
     * @return filtered CollectionLike
     */
    Re unique();

    <X> Re  uniqueBy(ActionFunction<T, X> f);

    <X> Re uniqueBy(ActionFunction<T, X> f, ActionBiFunction<T, T, T> collisionSelector);


    /**
     * @return whether CollectionLike is empty or not
     */
    boolean isEmpty();

    default boolean notEmpty(){
        return !this.isEmpty();
    }

    /**
     * return an iterator
     *
     * @return new iterator
     */
    Iterator<T> iterator();

    /**
     * @return no of elements in this
     */
    int size();

    /**
     * @param t
     * @return true if element in CollectionLike
     */
    boolean contains(T t);

    Collection<T> getCollection();

    /**
     *
     * @param predicate
     * @return true if all elements satisfy predicate
     */
    boolean allMatch(ActionPredicate<T> predicate);

    boolean anyMatch(ActionPredicate<T> predicate);

    Re nonNull();

    /**
     * Example use case: test whether an operation can successfully execute: <br>
     * Lists.of(1, 2, "A").forEach(Integer::parseInt).ok();
     * @return just returns true
     */
    default boolean ok(){
        return true;
    };
}
