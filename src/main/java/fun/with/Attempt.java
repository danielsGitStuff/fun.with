package fun.with;

import fun.with.actions.*;
import fun.with.annotations.Unstable;

import java.io.IOException;

@Unstable(reason = "might not be more readable at all :D")
public class Attempt<T, S> {
    private ActionFunction<T, S> actionFunction;
    private ActionConsumer<T> actionConsumer;
    private ActionSupplier<S> actionSupplier;
    private ActionRunnable actionRunnable;

    private Attempt<?, ?> previousAttempt;

    private Exception exception;
    private S result;

    private Attempt<?, ?> after;
    private ActionConsumer<Exception> exceptionConsumer;

    private Attempt() {

    }

    @Deprecated
    public static <X> Attempt<X, None> attempt(ActionConsumer<X> actionConsumer) {
        Attempt<X, None> attempt = new Attempt<>();
        attempt.actionConsumer = actionConsumer;
        return attempt;
    }

    @Deprecated
    public static <X, Y> Attempt<X, Y> attempt(ActionFunction<X, Y> actionFunction) {
        Attempt<X, Y> attempt = new Attempt<>();
        attempt.actionFunction = actionFunction;
        return attempt;
    }

    public static <Y> Attempt<None, Y> attempt(ActionSupplier<Y> actionSupplier) {
        Attempt<None, Y> attempt = new Attempt<>();
        attempt.actionSupplier = actionSupplier;
        return attempt;
    }

    public static Attempt<None, None> attempt(ActionRunnable actionRunnable) {
        Attempt<None, None> attempt = new Attempt<>();
        attempt.actionRunnable = actionRunnable;
        return attempt;
    }

    public Attempt<None, None> after(ActionRunnable actionRunnable) {
        Attempt<None, None> attempt = new Attempt<>();
        attempt.actionRunnable = actionRunnable;
        attempt.previousAttempt = this;
        this.after = attempt;
        return attempt;
    }

    public <Y> Attempt<S, Y> afterFunction(ActionFunction<S, Y> actionFunction) {
        Attempt<S, Y> attempt = new Attempt<>();
        attempt.actionFunction = actionFunction;
        attempt.previousAttempt = this;
        this.after = attempt;
        return attempt;
    }

    public <Y> Attempt<None, Y> after(ActionSupplier<Y> actionSupplier) {
        Attempt<None, Y> attempt = new Attempt<>();
        attempt.actionSupplier = actionSupplier;
        attempt.previousAttempt = this;
        this.after = attempt;
        return attempt;
    }

    public Attempt<S, None> after(ActionConsumer<S> actionConsumer) {
        Attempt<S, None> attempt = new Attempt<>();
        attempt.actionConsumer = actionConsumer;
        attempt.previousAttempt = this;
        this.after = attempt;
        return attempt;
    }

    public <E extends Exception> void onFail(Class<E> exceptionClass) {
        throw new RuntimeException("not implemented yet");
    }

    public Attempt<T, S> onFail(ActionConsumer<Exception> exceptionActionConsumer) {
        this.exceptionConsumer = exceptionActionConsumer;
        return this;
    }

    private S execute() {
        try {
            if (this.actionFunction != null)
                this.result = this.actionFunction.performAction((T) this.previousAttempt.result);
            else if (this.actionRunnable != null)
                this.actionRunnable.run();
            else if (this.actionSupplier != null)
                this.result = this.actionSupplier.get();
            else if (this.actionConsumer != null)
                this.actionConsumer.accept((T) this.previousAttempt.result);
        } catch (Exception e) {
            this.exception = e;
            if (this.exceptionConsumer != null) {
                this.exceptionConsumer.accept(e);
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
        return this.result;
    }

    public S run() {
        if (this.previousAttempt != null) {
            this.previousAttempt.run();
        }
        this.result = this.execute();
        return this.result;
    }

    public static void main(String[] args) {
//        Attempt.attempt(() -> {
//                    throw new IOException("blabla");
//                })
//                .onFail(Throwable::printStackTrace)
//                .after((it) -> System.out.println("success " + it))
//                .run();
        Lists<Integer> xs = Attempt.attempt(() -> Lists.of("1", "2", "3", "4")
                        .map(it -> Integer.parseInt(it)))
                .onFail(e -> System.err.println("Mistake " + e.getMessage()))
                .afterFunction(ls -> ls.forEach(i-> System.out.println("S="+i)))
                .run();
        System.out.println("Attempt.main");
    }
}
