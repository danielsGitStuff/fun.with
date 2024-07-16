package fun.with.unstable;

import fun.with.interfaces.actions.ActionRunnable;

/**
 * Test whether some operation can be executed or throws an {@link Exception}.
 *
 */
public class WorksTest {


    private final ActionRunnable runnable;

    private WorksTest(ActionRunnable runnable) {
        this.runnable = runnable;
    }

    /**
     * Test whether some operation can be executed or throws an {@link Exception}.
     *
     * @param runnable run what you suspect might throw {@link Exception}s here.
     * @return WorksTest
     */
    public static WorksTest test(ActionRunnable runnable) {
        return new WorksTest(runnable);
    }

    private boolean test() {
        try {
            this.runnable.run();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return true if this instance could be run without throwing any {@link Exception}s.
     */
    public boolean successful() {
        return this.test();
    }

    /**
     * @return true if this instance could be run and threw an {@link Exception}s.
     */
    public boolean failed() {
        return !this.test();
    }

}
