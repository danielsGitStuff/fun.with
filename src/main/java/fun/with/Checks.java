package fun.with;

public class Checks {
    public interface ICheck {
        public boolean test() throws Exception;
    }

    /**
     * throws {@link RuntimeException} when calling test returns false or throws an {@link Exception}.
     *
     * @param failureMessage
     * @param test
     */
    public static void check(String failureMessage, ICheck test) {
        boolean finished = false;
        boolean success = false;
        try {
            success = test.test();
            finished = true;
        } catch (Exception e) {
            System.err.println("Check failed: " + failureMessage);
            throw new RuntimeException(e);
        }
        if (finished && !success) throw new RuntimeException("test returned false: " + failureMessage);
    }
}
