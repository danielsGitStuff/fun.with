package fun.with.unstable;

public record TaskFailure<Start>(Start start, Exception cause) {
}
