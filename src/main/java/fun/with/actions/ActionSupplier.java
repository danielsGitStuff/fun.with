package fun.with.actions;

public interface ActionSupplier<S> extends Action {
    S performAction() throws Exception;

    default S get(){
        try {
            return this.performAction();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
