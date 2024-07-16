package fun.with;

import java.util.LinkedHashMap;

public class LinkedMaps {

    public static <X,Y>  Maps<X,Y> empty(){
        return Maps.wrap(new LinkedHashMap<>());
    }

    public static <X, Y> Maps<X, Y> empty(Class<X> klass, Class<Y> vlass) {
        return Maps.wrap(new LinkedHashMap<>());
    }

}
