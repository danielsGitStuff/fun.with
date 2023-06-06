package fun.with.actions;

public interface ActionFunction<T, S> extends Action {
    S performAction(T t) throws Exception;

    default S apply(T t){
        try {
            return this.performAction(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
