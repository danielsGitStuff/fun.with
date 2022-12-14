package fun.with.interfaces;

import fun.with.Lists;

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
  Re forEach(Consumer<? super T> consumer);

  /**
   * ForEach but with an index
   *
   * @param consumer
   * @return this
   */
  Re forEachIndexed(BiConsumer<Integer, ? super T> consumer);

  /**
   * Returns new CollectionLike with elements removed where predicate returns false.
   *
   * @param predicate when it returns true the element is in the result
   * @return filtered CollectionLike
   */
  Re filter(Predicate<? super T> predicate);

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
   *
   * @param t
   * @return true if element in CollectionLike
   */
  boolean contains(T t);
}
