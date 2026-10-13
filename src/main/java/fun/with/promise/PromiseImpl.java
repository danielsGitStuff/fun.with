package fun.with.promise;

import fun.with.Lists;
import fun.with.interfaces.actions.ActionConsumer;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PromiseImpl<Result, Error> implements Promise<Result, Error> {

    private Queue<ActionConsumer<Result>> remainingConsumers = new LinkedList<>();
    private Queue<ActionConsumer<Error>> remainingErrorConsumers = new LinkedList<>();
    private boolean hasResult = false;
    private boolean hasError = false;
    private Result result;
    private Error error;
    private final boolean throwOnException;

    protected PromiseImpl(boolean throwOnException) {
        this.throwOnException = throwOnException;
    }

    /**
     * Logs exceptions thrown by any consumers.
     *
     * @param <R> Type of successful return
     * @param <E> Type of error
     * @return new instance
     */
    public static <R, E> PromiseImpl<R, E> withoutThrowingOnException() {
        return new PromiseImpl<>(false);
    }

    /**
     * Throws any exception thrown by any consumer wrapped in a {@link RuntimeException}.
     *
     * @param <R> Type of successful return
     * @param <E> Type of error
     * @return new instance
     */
    public static <R, E> PromiseImpl<R, E> withThrowingOnException() {
        return new PromiseImpl<>(true);
    }

    /**
     * Fuses the promises into a new one that executes once everything had been executed with any result.
     * Simply skips null promises.
     *
     * @param promises
     * @return
     */
    public static Promise<List<Promise<Object, Object>>, Void> fuse(Promise<?, ?>... promises) {
        PromiseImpl<List<Promise<Object, Object>>, Void> fusedPromise = new PromiseImpl<>(false);
        if (promises == null || promises.length == 0)
            return fusedPromise.resolveOk(new ArrayList<>());
        Lists<Promise<Object, Object>> lsPromises = Lists.of(promises)
                .filter(Objects::nonNull)
                .map(p -> (Promise<Object, Object>) p);
        if (lsPromises.isEmpty())
            return fusedPromise.resolveOk(new ArrayList<>());
        AtomicInteger remaining = new AtomicInteger(lsPromises.size());
        for (Promise<?, ?> promise : lsPromises.get()) {
            promise.onDone(o -> {
                int current = remaining.decrementAndGet();
                if (current == 0) {
                    fusedPromise.resolveOk(lsPromises.copy().get());
                }
            }).onError(o -> {
                int current = remaining.decrementAndGet();
                if (current == 0) {
                    fusedPromise.resolveOk(lsPromises.copy().get());
                }
            });
        }
        return fusedPromise;
    }

    public Promise<Result, Error> toInterface() {
        return this;
    }


    @Override
    public Promise<Result, Error> onDone(ActionConsumer<Result> consumer) {
        boolean executeNow;
        synchronized (this) {
            executeNow = this.hasResult && !this.hasError;
            if (!executeNow && !this.hasResult)
                this.remainingConsumers.add(consumer);
        }
        if (executeNow)
            this.executeConsumer(consumer);
        return this;
    }

    @Override
    public Promise<Result, Error> onError(ActionConsumer<Error> consumer) {
        boolean executeNow;
        synchronized (this) {
            executeNow = this.hasError && !this.hasResult;
            if (!executeNow && !this.hasError)
                this.remainingErrorConsumers.add(consumer);
        }
        if (executeNow)
            this.executeErrorConsumer(consumer);
        return this;
    }

    @Override
    public boolean isPending() {
        synchronized (this) {
            return !this.hasError && !this.hasResult;
        }
    }

    @Override
    public boolean isOk() {
        synchronized (this) {
            return this.hasResult;
        }
    }

    @Override
    public boolean isFailed() {
        synchronized (this) {
            return this.hasError;
        }
    }

    private void executeConsumer(ActionConsumer<Result> consumer) {
        try {
            consumer.accept(this.result);
        } catch (Exception e) {
            if (this.throwOnException)
                throw new RuntimeException(e);
            System.err.println("Got a " + e.getClass().getSimpleName() + " when processing result consumer.");
        }
    }

    private void executeErrorConsumer(ActionConsumer<Error> consumer) {
        try {
            consumer.accept(this.error);
        } catch (Exception e) {
            if (this.throwOnException)
                throw new RuntimeException(e);
            System.err.println("Got a " + e.getClass().getSimpleName() + " when processing error consumer.");
        }
    }


    /**
     * Sets the promise to be determined and ok. Executes all "on Done" consumers.
     *
     * @param result
     * @return
     */
    public PromiseImpl<Result, Error> resolveOk(Result result) {
        Queue<ActionConsumer<Result>> remaining;
        synchronized (this) {
            if (this.hasResult) {
                throw new AlreadyResolvedOkException();
            }
            if (this.hasError) {
                throw new AlreadyResolvedErrorException();
            }
            this.result = result;
            this.hasResult = true;
            remaining = new LinkedList<>(this.remainingConsumers);
            this.remainingConsumers.clear();
        }
        while (remaining.peek() != null) {
            ActionConsumer<Result> consumer = remaining.poll();
            this.executeConsumer(consumer);
        }
        return this;
    }

    /**
     * Sets the promise to be determined with error. Executes all "error" consumers.
     *
     * @param error
     * @return
     */
    public PromiseImpl<Result, Error> resolveError(Error error) {
        Queue<ActionConsumer<Error>> remaining;
        synchronized (this) {
            if (this.hasResult) {
                throw new AlreadyResolvedOkException();
            }
            if (this.hasError) {
                throw new AlreadyResolvedErrorException();
            }
            this.hasError = true;
            this.error = error;
            remaining = new LinkedList<>(this.remainingErrorConsumers);
            this.remainingErrorConsumers.clear();
        }
        while (remaining.peek() != null) {
            ActionConsumer<Error> consumer = remaining.poll();
            this.executeErrorConsumer(consumer);
        }
        return this;
    }

    @Override
    public Error getError() {
        synchronized (this) {
            return error;
        }
    }

    @Override
    public Result getResult() {
        synchronized (this) {
            return result;
        }
    }

    @Override
    public boolean hasError() {
        synchronized (this) {
            return hasError;
        }
    }

    @Override
    public boolean hasResult() {
        synchronized (this) {
            return hasResult;
        }
    }
}
