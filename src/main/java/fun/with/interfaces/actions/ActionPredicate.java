package fun.with.interfaces.actions;

public interface ActionPredicate<T> extends Action {
    boolean performAction(T t) throws Exception;
    default boolean test(T t){
        try {
            return this.performAction(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
