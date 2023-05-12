package fun.with;

public class Actions {

    public static class None {
    }

    public static interface Action {
    }

    interface ActionFunction<T, S> extends Action {
        S run(T t);
    }

    interface ActionSupplier<S> extends Action {
        S run();
    }

    interface ActionConsumer<T> extends Action {
        void run(T t);
    }

    interface ActionRunnable extends Action {
        void run();
    }
}
