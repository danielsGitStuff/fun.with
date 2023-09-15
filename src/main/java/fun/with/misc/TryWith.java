package fun.with.misc;

import fun.with.Try;
import fun.with.interfaces.TryWithConsumer;
import fun.with.interfaces.TryWithFunction;

import java.io.FileInputStream;

public class TryWith<T> {

    private final T t;

    public TryWith(T t) {
        this.t = t;
    }


    public <X> X function(TryWithFunction<T, X> f) {
        try {
            return f.run(this.t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void consume(TryWithConsumer<T> f) {
        try {
            f.consume(this.t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <X> X defaultOrFunction(X defaultX, TryWithFunction<T, X> f) {
        try {
            return f.run(this.t);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultX;
        }
    }
}
