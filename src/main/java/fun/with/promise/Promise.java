package fun.with.promise;

import fun.with.Lists;
import fun.with.interfaces.actions.ActionConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public interface Promise<DoneType, ErrorType> {

    /**
     * Fuses the promises into a new one that executes once everything had been executed with any result.
     * Simply skips null promises.
     *
     * @param promises
     * @return
     */
    static Promise<List<Promise<Object, Object>>, Void> fused(Promise<?, ?>... promises) {
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

    static Promise<List<Promise<Object, Void>>, Void> fusedAnd(Promise<?, ?>... promises) {
        PromiseImpl<List<Promise<Object, Void>>, Void> fusedPromise = new PromiseImpl<>(false);
        if (promises == null || promises.length == 0)
            return fusedPromise.resolveOk(new ArrayList<>());
        Lists<Promise<Object, Void>> lsPromises = Lists.of(promises)
                .filter(Objects::nonNull)
                .map(p -> (Promise<Object, Void>) p);
        if (lsPromises.isEmpty())
            return fusedPromise.resolveOk(new ArrayList<>());
        AtomicInteger remaining = new AtomicInteger(lsPromises.size());
        AtomicBoolean executed = new AtomicBoolean(false);
        ReentrantLock lock = new ReentrantLock();
        for (Promise<?, ?> promise : lsPromises.get()) {
            promise.onDone(o -> {
                boolean resolve = false;
                lock.lock();
                try {
                    int current = remaining.decrementAndGet();
                    if (current == 0 && !executed.get()) {
                        executed.set(true);
                        resolve = true;
                    }
                } finally {
                    lock.unlock();
                }
                if (resolve)
                    fusedPromise.resolveOk(lsPromises.copy().get());
            }).onError(o -> {
                boolean resolve = false;
                lock.lock();
                try {
                    remaining.decrementAndGet();
                    if (!executed.get()) {
                        executed.set(true);
                        resolve = true;
                    }
                } finally {
                    lock.unlock();
                }
                if (resolve)
                    fusedPromise.resolveError(null);
            });
        }
        return fusedPromise;
    }

    static <D, E> Promise<D, E> resolvedOk() {
        return new PromiseImpl<D, E>(false).resolveOk(null);
    }

    static <D, E> Promise<D, E> resolvedError() {
        return new PromiseImpl<D, E>(false).resolveError(null);
    }

    Promise<DoneType, ErrorType> onDone(ActionConsumer<DoneType> consumer);

    Promise<DoneType, ErrorType> onError(ActionConsumer<ErrorType> consumer);

    boolean isPending();

    ErrorType getError();

    DoneType getResult();

    boolean hasError();

    boolean hasResult();


}
