package fun.with.promise;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class PromiseImplTest {

    PromiseImpl<Integer, String> promise, p1, p2;
    PromiseImpl<List<Promise<Object, Object>>, Void> fused;
    Integer result;
    String error;

    private PromiseImpl<List<Promise<Object, Object>>, Void> fuse(Promise<?, ?>... promises) {
        return (PromiseImpl<List<Promise<Object, Object>>, Void>) PromiseImpl.fuse(promises);
    }

    @BeforeEach
    void beforeEach() {
        promise = PromiseImpl.withoutThrowingOnException();
        p1 = PromiseImpl.withoutThrowingOnException();
        p2 = PromiseImpl.withoutThrowingOnException();
    }

    @Test
    void onDone() {
    }

    @Test
    void onError() {
    }

    @Test
    void fuseOk() {
        fused = fuse(p1, p2);
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
    void fuseOkWithNull() {
        fused = fuse(p1, null, p2);
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
    void fuseFail1() {
        fused = fuse(p1, p2);
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
    void fuseFail2() {
        p1.resolveOk(1);
        p2.resolveError("asd");
        fused = fuse(p1, p2);
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