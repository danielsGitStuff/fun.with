package fun.with.misc;

import fun.with.actions.ActionBiFunction;
import fun.with.actions.ActionFunction;
import fun.with.actions.ActionSupplier;
import fun.with.interfaces.CollectionLike;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Unique {
    /**
     * Ensures all elements of a collection are unique by some property extracted by f.
     * @param collectionLike collection of type T.
     * @param collectionCreator the new collection the results will reside in.
     * @param f function that extracts the uniqueness object for each element of the collection of type X.
     * @param collisionSelector function that returns the instance of T that will be kept should there be two Xs with the same hash.
     * @return
     * @param <T> type of incoming collection
     * @param <X> type of the unique criteria
     * @param <Re> return type of the collection
     */
    public static <T, X, Re extends CollectionLike<T, ?>> Re uniqueBy(CollectionLike<T, Re> collectionLike, ActionSupplier<Re> collectionCreator, ActionFunction<T, X> f, ActionBiFunction<T, T, T> collisionSelector) {
        Map<X, T> key2t = new HashMap<>();
        List<X> uniqueKeys = new ArrayList<>();
        collisionSelector = collisionSelector != null ? collisionSelector : (existing, current) -> existing;
        for (T t : collectionLike.getCollection()) {
            X x = f.apply(t);
            if (key2t.containsKey(x)) {
                T selected = collisionSelector.apply(key2t.get(x), t);
                key2t.put(x, selected);
            } else {
                uniqueKeys.add(x);
                key2t.put(x, t);
            }
        }
        Re uniqueTs = collectionCreator.get();
        for (X x : uniqueKeys) {
            uniqueTs.add(key2t.get(x));
        }
        return uniqueTs;
    }
}
