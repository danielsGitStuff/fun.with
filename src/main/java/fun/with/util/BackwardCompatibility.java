package fun.with.util;

/**
 * Houses some methods that backport some of the functionality of more recent JDK versions.
 */
public class BackwardCompatibility {
    /**
     * Backports Math.ceilDiv() introduced in JDK 19.
     *
     * @param x
     * @param n
     * @return
     */
    public static int ceilDiv(int x, int n) {
        if (n == 0) {
            throw new ArithmeticException("n is 0");
        }
        int r = x % n;
        int y = x / n;
        if (r > 0)
            return y + 1;
        return y;
    }
}
