package fun.with.misc;

import fun.with.annotations.Unstable;
import fun.with.interfaces.let.LetConsumer;

@Unstable(reason = "fooling around. might be useless.")
public class Let<T> {
    private final T t;

    public Let(T t) {
        this.t = t;
    }

    public void run(LetConsumer<T> consumer) {
        if (this.t != null) {
            consumer.consume(t);
        }
    }

    public static <X> Let<X> it(X x) {
        return new Let<>(x);
    }
}
