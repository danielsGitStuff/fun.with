package fun.with.interfaces;

import fun.with.Lists;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface CollectionLike<T, Re> {

    Re forEach(Consumer<? super T> consumer);

    Re forEachIndexed(BiConsumer<Integer, ? super T> consumer);

    Re filter(Predicate<? super T> predicate);

    Re add(T t);

    Re addAll(Collection<T> ts);

    Re addAll(Lists<T> ts);

    Re unique();

    boolean isEmpty();

    Iterator<T> iterator();

    int size();
}
