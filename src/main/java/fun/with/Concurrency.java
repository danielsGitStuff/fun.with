package fun.with;

import fun.with.annotations.Unstable;
import fun.with.interfaces.CollectionLike;
import fun.with.unstable.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Unstable
public class Concurrency<Start, Target> {

    private Lists<Start> elements;
    private final TaskBox<Start, Target> taskBox;
    private final TaskCollector<Start, Target> taskCollector = new TaskCollector<>();
    private final Lists<TaskFailureHandler<Start>> failureHandles = Lists.empty();
    private final Lists<TasksFailureHandler<Start>> generalFailureHandlers = Lists.empty();
    private final List<TaskFailure<Start>> unhandledErrors = Collections.synchronizedList(new ArrayList<>());

    private Concurrency(TaskBox<Start, Target> taskBox, Lists<Start> elements) {
        this.elements = elements;
        this.taskBox = taskBox;

    }

    public static <X, Y> Concurrency<X, Y> withElements(TaskBox<X, Y> taskBox, Lists<X> elements) {
        return new Concurrency<>(taskBox, elements);
    }

    public static <X, Y> Concurrency<X, Y> initialize(Lists<X> elements, Task<X, ?, Y> finalTask) {
        TaskBox<X, Y> box = TaskBox.of(finalTask);
        return new Concurrency<>(box, elements);
    }

    public static <X, Y> Concurrency<X, Y> initialize(Task<X, ?, Y> finalTask) {
        TaskBox<X, Y> box = TaskBox.of(finalTask);
        return new Concurrency<>(box, Lists.empty());
    }

    public static <X, Y> Concurrency<X, Y> initialize(ConsumingTask<X, Y> finalConsumingTask) {
        TaskBox<X, Y> box = TaskBox.of(finalConsumingTask);
        return new Concurrency<>(box, Lists.empty());
    }

    public Concurrency<Start, Target> withElements(Start... elements) {
        this.elements = Lists.of(elements);
        return this;
    }

    public Concurrency<Start, Target> addElements(Start... elements) {
        this.elements.addAll(Lists.of(elements));
        return this;
    }

    public Concurrency<Start, Target> withElements(CollectionLike<Start, ?> elements) {
        this.elements = Lists.wrap(elements.getCollection());
        return this;
    }

    public Concurrency<Start, Target> addElements(CollectionLike<Start, ?> elements) {
        this.elements.addAll(elements);
        return this;
    }

    public Concurrency<Start, Target> withElements(Collection<Start> elements) {
        this.elements = Lists.wrap(elements);
        return this;
    }

    public Concurrency<Start, Target> addElements(Collection<Start> elements) {
        this.elements.addAll(elements);
        return this;
    }

    public Concurrency<Start, Target> handleFailure(TaskFailureHandler<Start> failureHandle) {
        this.failureHandles.add(failureHandle);
        return this;
    }

    public Concurrency<Start, Target> handleFailures(TasksFailureHandler<Start> failuresHandler) {
        this.generalFailureHandlers.add(failuresHandler);
        return this;
    }

    public Lists<Target> map() {
        Lists<Target> successful = this.elements.stream()
                .parallel()
                .map(start -> {
                    try {
                        return this.taskBox.map(start);
                    } catch (Exception e) {
                        // ActionFunction/ActionConsumer wrap exceptions in RuntimeException.
                        // We unwrap one level if it's such a wrapper.
                        Throwable cause = (e instanceof RuntimeException && e.getCause() != null) ? e.getCause() : e;
                        TaskFailure<Start> failure = new TaskFailure<>(start,
                                (cause instanceof Exception) ? (Exception) cause : e);

                        if (this.failureHandles.isEmpty()) {
                            this.unhandledErrors.add(failure);
                        } else {
                            this.failureHandles.forEach(h -> {
                                try {
                                    h.performAction(failure);
                                } catch (Exception ex) {
                                    Throwable innerCause = (ex instanceof RuntimeException && ex.getCause() != null)
                                            ? ex.getCause()
                                            : ex;
                                    this.unhandledErrors.add(new TaskFailure<>(start,
                                            (innerCause instanceof Exception) ? (Exception) innerCause : ex));
                                }
                            });
                        }
                        return failure;
                    }
                })
                .collect(this.taskCollector);
        this.generalFailureHandlers.forEach(handle -> {
            try {
                handle.accept(this.taskCollector.getFailures());
            } catch (Exception ex) {
                Throwable innerCause = (ex instanceof RuntimeException && ex.getCause() != null) ? ex.getCause() : ex;
                this.unhandledErrors
                        .add(new TaskFailure<>(null, (innerCause instanceof Exception) ? (Exception) innerCause : ex));
            }
        });
        return successful;
    }

    public Lists<TaskFailure<Start>> getUnhandledErrors() {
        return Lists.from(this.unhandledErrors);
    }

    static void main() {
        Lists<Integer> ls = Lists.of(1, 2, 3, 0);
        // Concurrency<Integer, Long> concurrency = Concurrency.initialize(ls,
        // Task.of(Integer.class, i -> i.toString())
        // .then(s -> s + s)
        // .then(s -> {
        // System.out.println("Task in " + Thread.currentThread().getName());
        // return s;
        // })
        // .then(Long::parseLong)
        // .then(l -> l / l))
        // .handleFailure(f -> {
        // System.out.println("failure handle in " + Thread.currentThread().getName() +
        // ". Value was " + f.start());
        // });
        // Lists<Long> converted = concurrency.map();
        // System.out.println(converted);

        Concurrency<Integer, Integer> conc2 = Concurrency.initialize(Task.of(Integer.class, i -> i.toString())
                .then(s -> s + s + s)
                .then(Integer::parseInt)
                .then(i -> {
                    if (i > 200)
                        return i;
                    return i / i;
                })
                .then(i -> {
                    System.out.println("Success: " + i + " thread " + Thread.currentThread().getName());
                    return i;
                }).consume(i -> {
                    System.out.println("CONSUME " + i + " thread " + Thread.currentThread().getName());
                }))
                .handleFailures(failures -> failures.forEach(System.out::println))
                .withElements(0);
        conc2.map();
    }
}
