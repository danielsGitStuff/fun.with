package fun.with;

import fun.with.annotations.Unstable;
import fun.with.interfaces.TryRunnable;
import fun.with.interfaces.TryWithSupplier;
import fun.with.misc.TryWith;

@Unstable(reason = "not sure it handles that well")
public class Try {

    public static <X extends AutoCloseable> TryWith<X> with(TryWithSupplier<X> supplier) {
        try {
            return new TryWith<>(supplier.supply());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void run(TryRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <X> X supply(TryWithSupplier<X> supplier) {
        try {
            return supplier.supply();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <X> X defaultOrSupply(X defaultX, TryWithSupplier<X> supplier) {
        try {
            return supplier.supply();
        } catch (Exception e) {
            e.printStackTrace();
            return defaultX;
        }
    }

    public static void ignorant(TryRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
        }
    }
}
