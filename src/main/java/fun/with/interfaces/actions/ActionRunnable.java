package fun.with.interfaces.actions;

public interface ActionRunnable extends Action {
    void performAction() throws Exception;

    default void run(){
        try {
            this.performAction();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
