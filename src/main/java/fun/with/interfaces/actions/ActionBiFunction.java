package fun.with.interfaces.actions;

public interface ActionBiFunction<T, S, R> extends Action {
    R performAction(T t, S s) throws Exception;

    default R apply(T t, S s) {
        try {
            return this.performAction(t, s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
