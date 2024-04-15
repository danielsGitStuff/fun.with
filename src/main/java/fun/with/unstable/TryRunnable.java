package fun.with.unstable;

import fun.with.annotations.Unstable;

@Unstable
public interface TryRunnable {
void run() throws Exception;
}
