package fun.with.actions;

public interface ActionBiPredicate<S, T> extends Action {
    boolean performAction(S s, T t) throws Exception;

    default boolean test(S s, T t) {
        try {
            return this.performAction(s, t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
