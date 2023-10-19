package fun.with;

import fun.with.interfaces.CollectionLike;

public class Ints {
    public static Integer max(CollectionLike<Integer, ?> ints) {
        Integer max = null;
        for (Integer i : ints.getCollection()) {
            if (max == null)
                max = i;
            else {
                max = max > i ? max : i;
            }
        }
        return max;
    }
}
