package fun.with.interfaces;

import fun.with.annotations.Unstable;

@Unstable
public interface TryRunnable {
void run() throws Exception;
}
