package fun.with.actions;

public interface ActionBiConsumer<T,S> extends Action {
    void performAction(T t, S s) throws Exception;

    default void accept(T t, S s){
        try {
            this.performAction(t, s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
