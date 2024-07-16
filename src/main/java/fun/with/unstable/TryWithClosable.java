package fun.with.unstable;

import fun.with.interfaces.actions.ActionConsumer;
import fun.with.interfaces.actions.ActionFunction;

import java.io.FileInputStream;

public class TryWithClosable<T extends AutoCloseable> {

    private final T autoClosable;

    public TryWithClosable(T autoClosable) {
        this.autoClosable = autoClosable;
    }

    public static void main(String[] args) {
        Integer read = Try.withClosable(() -> new FileInputStream("build.gradle")).function(FileInputStream::read);
        System.out.println(read);
    }

    public <X> X function(ActionFunction<T, X> f) {
        try {
            return f.apply(this.autoClosable);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            Try.ignorant(this.autoClosable::close);
        }
    }

    public void consume(ActionConsumer<T> f) {
        try {
            f.accept(this.autoClosable);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            Try.ignorant(this.autoClosable::close);
        }
    }

    public <X> X defaultOrFunction(X defaultX, ActionFunction<T, X> f) {
        try {
            return f.apply(this.autoClosable);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultX;
        } finally {
            Try.ignorant(this.autoClosable::close);
        }
    }
}
