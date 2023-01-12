package fun.with.misc;

import fun.with.Try;
import fun.with.interfaces.TryWithConsumer;
import fun.with.interfaces.TryWithFunction;

import java.io.FileInputStream;

public class TryWith<T extends AutoCloseable> {

    private final T autoClosable;

    public TryWith(T autoClosable) {
        this.autoClosable = autoClosable;
    }

    public static void main(String[] args) {
        Integer read = Try.with(() -> new FileInputStream("build.gradle")).function(FileInputStream::read);
        System.out.println(read);
    }

    public <X> X function(TryWithFunction<T, X> f) {
        try {
            return f.run(this.autoClosable);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Try.ignorant(this.autoClosable::close);
        }
    }

    public void consume(TryWithConsumer<T> f) {
        try {
            f.consume(this.autoClosable);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Try.ignorant(this.autoClosable::close);
        }
    }

    public <X> X defaultOrFunction(X defaultX, TryWithFunction<T, X> f) {
        try {
            return f.run(this.autoClosable);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultX;
        } finally {
            Try.ignorant(this.autoClosable::close);
        }
    }
}
