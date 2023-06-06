package fun.with.actions;

public interface ActionConsumer<T> extends Action {
    void performAction(T t) throws Exception;

    default void accept(T t){
        try {
            this.performAction(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
