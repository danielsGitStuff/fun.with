package fun.with.promise;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class PromiseImplTest {

    PromiseImpl<Integer, String> promise, p1, p2;
    PromiseImpl<List<Promise<Object, Object>>, Void> fused;
    PromiseImpl<List<Promise<Object, Void>>, Void> fusedAnd;
    Integer result;
    String error;

    private PromiseImpl<List<Promise<Object, Object>>, Void> fused(Promise<?, ?>... promises) {
        return (PromiseImpl<List<Promise<Object, Object>>, Void>) Promise.fused(promises);
    }

    private PromiseImpl<List<Promise<Object, Void>>, Void> fusedAnd(Promise<?, ?>... promises) {
        return (PromiseImpl<List<Promise<Object, Void>>, Void>) Promise.fusedAnd(promises);
    }

    @BeforeEach
    void beforeEach() {
        promise = PromiseImpl.withoutThrowingOnException();
        p1 = PromiseImpl.withoutThrowingOnException();
        p2 = PromiseImpl.withoutThrowingOnException();
    }

    @Test
    void onDone() {
        promise.onDone(integer -> this.result = integer);
        promise.resolveError("error");
        promise.onDone(integer -> this.result = integer);
        assertNull(this.result);
    }

    @Test
    void onError() {
        promise.onError(s -> this.error = s);
        promise.resolveOk(1);
        promise.onError(s -> this.error = s);
        assertNull(this.error);
    }

    @Test
    void fusedEmpty() {
        fused = fused();
        assertTrue(fused.hasResult());
        assertTrue(fused.getResult().isEmpty());
    }

    @Test
    void fusedAndEmpty() {
        fusedAnd = fusedAnd();
        assertTrue(fusedAnd.hasResult());
        assertTrue(fusedAnd.getResult().isEmpty());
    }

    @Test
    void fusedOnlyNull() {
        fused = fused(null, null);
        assertTrue(fused.hasResult());
        assertTrue(fused.getResult().isEmpty());
    }

    @Test
    void fusedAndOnlyNull() {
        fusedAnd = fusedAnd(null, null);
        assertTrue(fusedAnd.hasResult());
        assertTrue(fusedAnd.getResult().isEmpty());
    }

    @Test
    void fusedOk() {
        fused = fused(p1, p2);
        AtomicInteger sum = new AtomicInteger();
        fused.onDone(ps -> {
            ps.stream()
                    .map(p -> (Integer) p.getResult())
                    .forEach(sum::addAndGet);
        });
        p1.resolveOk(1);
        p2.resolveOk(2);
        assertEquals(3, sum.get());
    }

    @Test
    void fusedAndOk() {
        fusedAnd = fusedAnd(p1, p2);
        AtomicInteger sum = new AtomicInteger();
        fusedAnd.onDone(ps -> {
            ps.stream()
                    .map(p -> (Integer) p.getResult())
                    .forEach(sum::addAndGet);
        });
        p1.resolveOk(1);
        p2.resolveOk(2);
        assertEquals(3, sum.get());
    }

    @Test
    void fusedOkWithNull() {
        fused = fused(p1, null, p2);
        AtomicInteger sum = new AtomicInteger();
        fused.onDone(ps -> {
            ps.stream()
                    .map(p -> (Integer) p.getResult())
                    .forEach(sum::addAndGet);
        });
        p1.resolveOk(1);
        p2.resolveOk(2);
        assertEquals(3, sum.get());
    }

    @Test
    void fusedAndOkWithNull() {
        fusedAnd = fusedAnd(p1, null, p2);
        AtomicInteger sum = new AtomicInteger();
        fusedAnd.onDone(ps -> {
            ps.stream()
                    .map(p -> (Integer) p.getResult())
                    .forEach(sum::addAndGet);
        });
        p1.resolveOk(1);
        p2.resolveOk(2);
        assertEquals(3, sum.get());
    }

    @Test
    void fusedFail1() {
        fused = fused(p1, p2);
        AtomicInteger sum = new AtomicInteger();
        fused.onDone(ps -> {
            ps.stream()
                    .filter(Promise::hasResult)
                    .map(p -> (Integer) p.getResult())
                    .forEach(sum::addAndGet);
        });
        p1.resolveOk(1);
        p2.resolveError("asd");
        assertEquals(1, sum.get());
    }

    @Test
    void fusedAndFail1() {
        fusedAnd = fusedAnd(p1, p2);
        AtomicInteger signalUnchanged = new AtomicInteger();
        AtomicInteger signalChanged = new AtomicInteger();
        fusedAnd.onDone(ps -> {
            ps.stream()
                    .filter(Promise::hasResult)
                    .map(p -> (Integer) p.getResult())
                    .forEach(signalUnchanged::addAndGet);
        });
        fusedAnd.onError(unused -> signalChanged.addAndGet(123));
        p1.resolveOk(1);
        p2.resolveError("asd");
        assertEquals(0, signalUnchanged.get());
        assertEquals(123, signalChanged.get());
    }

    @Test
    void fusedAndFailBeforeOthersSettle() {
        fusedAnd = fusedAnd(p1, p2);
        p1.resolveError("asd");
        assertTrue(fusedAnd.hasError());
        assertTrue(p2.isPending());
    }

    @Test
    void fusedAndFailThenOk() {
        p2 = PromiseImpl.withThrowingOnException();
        fusedAnd = fusedAnd(p1, p2);
        AtomicInteger signalUnchanged = new AtomicInteger();
        AtomicInteger signalChanged = new AtomicInteger();
        fusedAnd.onDone(ps -> signalUnchanged.addAndGet(1));
        fusedAnd.onError(unused -> signalChanged.addAndGet(123));
        p1.resolveError("asd");
        assertDoesNotThrow(() -> p2.resolveOk(2));
        assertEquals(0, signalUnchanged.get());
        assertEquals(123, signalChanged.get());
        assertTrue(fusedAnd.hasError());
        assertFalse(fusedAnd.hasResult());
    }

    @Test
    void fusedAndFailTwice() {
        p2 = PromiseImpl.withThrowingOnException();
        fusedAnd = fusedAnd(p1, p2);
        AtomicInteger signalChanged = new AtomicInteger();
        fusedAnd.onError(unused -> signalChanged.addAndGet(123));
        p1.resolveError("asd");
        assertDoesNotThrow(() -> p2.resolveError("asd2"));
        assertEquals(123, signalChanged.get());
    }

    /**
     * Starts a thread that waits at the barrier and then runs the action. Counts any exception that escapes.
     */
    private Thread racingThread(CyclicBarrier barrier, AtomicInteger thrown, Runnable action) {
        Thread thread = new Thread(() -> {
            try {
                barrier.await();
                action.run();
            } catch (Exception e) {
                thrown.incrementAndGet();
            }
        });
        thread.start();
        return thread;
    }

    @Test
    void fusedAndFailTwiceConcurrently() throws InterruptedException {
        AtomicInteger thrown = new AtomicInteger();
        for (int i = 0; i < 20000; i++) {
            PromiseImpl<Integer, String> a = PromiseImpl.withThrowingOnException();
            PromiseImpl<Integer, String> b = PromiseImpl.withThrowingOnException();
            fusedAnd = fusedAnd(a, b);
            CyclicBarrier barrier = new CyclicBarrier(2);
            Thread t1 = racingThread(barrier, thrown, () -> a.resolveError("a"));
            Thread t2 = racingThread(barrier, thrown, () -> b.resolveError("b"));
            t1.join();
            t2.join();
            assertTrue(fusedAnd.hasError());
        }
        assertEquals(0, thrown.get());
    }

    @Test
    void fusedAndFailAndOkConcurrently() throws InterruptedException {
        AtomicInteger thrown = new AtomicInteger();
        for (int i = 0; i < 20000; i++) {
            PromiseImpl<Integer, String> a = PromiseImpl.withThrowingOnException();
            PromiseImpl<Integer, String> b = PromiseImpl.withThrowingOnException();
            fusedAnd = fusedAnd(a, b);
            CyclicBarrier barrier = new CyclicBarrier(2);
            Thread t1 = racingThread(barrier, thrown, () -> a.resolveError("a"));
            Thread t2 = racingThread(barrier, thrown, () -> b.resolveOk(2));
            t1.join();
            t2.join();
            assertTrue(fusedAnd.hasError(), "one promise failed, so the fused one has to fail as well");
        }
        assertEquals(0, thrown.get());
    }

    /**
     * A consumer of the fused promise may block. This must not keep other promises from settling meanwhile.
     */
    @Test
    void fusedAndBlockingConsumerDoesNotBlockOtherPromises() throws InterruptedException {
        PromiseImpl<Integer, String> p3 = PromiseImpl.withoutThrowingOnException();
        fusedAnd = fusedAnd(p1, p2, p3);
        CountDownLatch latchEntered = new CountDownLatch(1);
        CountDownLatch latchOther = new CountDownLatch(1);
        AtomicBoolean otherWasNotBlocked = new AtomicBoolean(false);
        fusedAnd.onError(unused -> {
            System.out.println("cd enter");
            latchEntered.countDown();
            try {
                System.out.println("waiting for other");
                otherWasNotBlocked.set(latchOther.await(20  , TimeUnit.SECONDS));
                System.out.println("waiting for other done");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        Thread failing = new Thread(() -> {
            System.out.println("resolve error");
            p1.resolveError("asd");
        }, "failing");
        failing.start();
        System.out.println("waiting for enter");
        assertTrue(latchEntered.await(2, TimeUnit.SECONDS));
        System.out.println("waiting for enter done");
        Thread other = new Thread(() -> {
            System.out.println("resolve ok");
            p2.resolveOk(2);
            System.out.println("cd other");
            latchOther.countDown();
        }, "other");
        other.start();
        failing.join();
        other.join();
        assertTrue(otherWasNotBlocked.get(), "settling another promise had to wait for the consumer of the fused promise");
    }

    @Test
    void fusedFail2() {
        p1.resolveOk(1);
        p2.resolveError("asd");
        fused = fused(p1, p2);
        AtomicInteger sum = new AtomicInteger();
        fused.onDone(ps -> {
            ps.stream()
                    .filter(Promise::hasResult)
                    .map(p -> (Integer) p.getResult())
                    .forEach(sum::addAndGet);
        });
        assertEquals(1, sum.get());
    }

    @Test
    void resolveOkConsume() {
        promise.resolveOk(1);
        promise.onDone(integer -> this.result = integer);
        assertEquals(1, this.result);
    }

    @Test
    void resolveOkConsumeTwice() {
        promise.resolveOk(1);
        promise.onDone(integer -> this.result = integer);
        assertThrows(AlreadyResolvedOkException.class, () -> promise.resolveOk(2));
    }

    @Test
    void resolveOkTwice() {
        promise.onDone(integer -> this.result = integer);
        promise.resolveOk(1);
        assertThrows(AlreadyResolvedOkException.class, () -> promise.resolveOk(2));
    }

    @Test
    void resolveOkThenError() {
        promise.onDone(integer -> this.result = integer);
        promise.resolveOk(1);
        assertThrows(AlreadyResolvedOkException.class, () -> promise.resolveError("error"));
    }

    @Test
    void resolveErrorConsume() {
        promise.onError(s -> this.error = s);
        promise.resolveError("error");
        assertEquals("error", this.error);
    }

    @Test
    void resolveErrorConsumeTwice() {
        promise.onError(s -> this.error = s);
        promise.resolveError("error");
        promise.onError(s -> this.error = s + "2");
        assertEquals("error2", this.error);
    }

    @Test
    void resolveErrorTwice() {
        promise.onError(s -> this.error = s);
        promise.resolveError("error");
        assertThrows(AlreadyResolvedErrorException.class, () -> promise.resolveError("error2"));
    }

    @Test
    void resolveErrorThenResult() {
        promise.onError(s -> this.error = s);
        promise.resolveError("error");
        assertThrows(AlreadyResolvedErrorException.class, () -> promise.resolveOk(12));
    }
}