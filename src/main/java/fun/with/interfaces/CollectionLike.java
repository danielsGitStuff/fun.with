package fun.with.interfaces;

import fun.with.Lists;
import fun.with.actions.ActionBiConsumer;
import fun.with.actions.ActionConsumer;
import fun.with.actions.ActionPredicate;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface CollectionLike<T, Re> {

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

    /**
     * @return whether CollectionLike is empty or not
     */
    boolean isEmpty();

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
}
