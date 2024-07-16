package fun.with.interfaces.actions;

public interface ActionTriConsumer<R, T, S> extends Action {
    void performAction(R r, T t, S s) throws Exception;

    default void accept(R r, T t, S s) {
        try {
            this.performAction(r, t, s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
