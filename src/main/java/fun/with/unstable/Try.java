package fun.with.unstable;

import fun.with.interfaces.actions.ActionRunnable;
import fun.with.interfaces.actions.ActionSupplier;
import fun.with.annotations.Unstable;

@Unstable(reason = "not sure it handles that well")
public class Try {

    public static <X extends AutoCloseable> TryWithClosable<X> withClosable(ActionSupplier<X> supplier) {
        try {
            return new TryWithClosable<>(supplier.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <X> TryWith<X> with(ActionSupplier<X> supplier) {
        return new TryWith<>(Try.supply(supplier));
    }

    public static void run(ActionRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <X> X supply(ActionSupplier<X> supplier) {
        return supplier.get();
    }

    public static <X> X ignorantSupply(ActionSupplier<X> supplier) {
        try {
            return supplier.get();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static <X> X defaultOrSupply(X defaultX, ActionSupplier<X> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            e.printStackTrace();
            return defaultX;
        }
    }

    public static void ignorant(ActionRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
        }
    }
}
