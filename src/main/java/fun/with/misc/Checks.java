package fun.with.misc;

import fun.with.interfaces.actions.ActionFunction;
import fun.with.interfaces.actions.ActionSupplier;

public class Checks {
    public interface ICheck {
        boolean test() throws Exception;
    }

    /**
     * throws {@link RuntimeException} when calling test returns false or throws an {@link Exception}.
     *
     * @param failureMessage
     * @param test
     */
    public static void check(String failureMessage, ICheck test) {
        check(failureMessage, test, null);
    }

    /**
     * throws {@link RuntimeException} when calling test returns false or throws an {@link Exception}.
     *
     * @param failureMessage
     * @param failureMessageAppendix appends to the failure message. Can provide more insight on why the test has failed.
     * @param test
     */
    public static void check(String failureMessage, ICheck test, ActionSupplier<String> failureMessageAppendix) {
        boolean success;
        try {
            success = test.test();
        } catch (Exception e) {
            failureMessage = failureMessageAppendix == null ? failureMessage : failureMessage + " " + failureMessageAppendix.get();
            System.err.println("Check failed: " + failureMessage);
            throw new RuntimeException(e);
        }
        if (!success) throw new RuntimeException("test returned false: " + failureMessage);
    }
}
