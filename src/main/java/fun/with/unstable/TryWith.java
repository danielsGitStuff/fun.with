package fun.with.unstable;

import fun.with.interfaces.actions.ActionConsumer;
import fun.with.interfaces.actions.ActionFunction;

public class TryWith<T> {

    private final T t;

    public TryWith(T t) {
        this.t = t;
    }


    public <X> X function(ActionFunction<T, X> f) {
        try {
            return f.apply(this.t);
        } catch (Exception e) {
            throw e;
        }
    }

    public void consume(ActionConsumer<T> f) {
        try {
            f.accept(this.t);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public <X> X defaultOrFunction(X defaultX, ActionFunction<T, X> f) {
        try {
            return f.apply(this.t);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultX;
        }
    }
}
