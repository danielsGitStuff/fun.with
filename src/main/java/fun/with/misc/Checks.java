package fun.with.misc;

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
        boolean success;
        try {
            success = test.test();
        } catch (Exception e) {
            System.err.println("Check failed: " + failureMessage);
            throw new RuntimeException(e);
        }
        if (!success) throw new RuntimeException("test returned false: " + failureMessage);
    }
}
