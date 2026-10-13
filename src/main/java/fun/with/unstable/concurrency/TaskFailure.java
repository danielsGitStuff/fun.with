package fun.with.unstable.concurrency;

public record TaskFailure<Start>(Start start, Exception cause) {
}
