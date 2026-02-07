package fun.with;

import fun.with.interfaces.actions.ActionConsumer;
import fun.with.unstable.*;

public class Concurrency<Start, Target> {

    private final Lists<Start> elements;
    private final TaskBox<Start, Target> taskBox;
    private final TaskCollector<Start, Target> taskCollector = new TaskCollector<>();
    private final Lists<TaskFailureHandler<Start>> failureHandles = Lists.empty();
    private final Lists<TasksFailureHandler<Start>> generalFailureHandlers = Lists.empty();

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

    public Concurrency<Start, Target> handleFailure(TaskFailureHandler<Start> failureHandle) {
        this.failureHandles.add(failureHandle);
        return this;
    }
    public Concurrency<Start,Target> handleFailures(TasksFailureHandler<Start> failuresHandler){
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
                        TaskFailure<Start> failure = new TaskFailure<>(start, e);
                        // todo this will throw RTEs on failure
                        this.failureHandles.forEach(h -> h.performAction(failure));
                        return failure;
                    }
                })
                .collect(this.taskCollector);
        // todo this will throw RTEs on failure
        this.generalFailureHandlers.forEach(handle -> handle.accept(this.taskCollector.getFailures()));
        return successful;
    }

    static void main() {
        Lists<Integer> ls = Lists.of(1, 2, 3, 0);
        Concurrency<Integer, Long> concurrency = Concurrency.initialize(ls, Task.of(Integer.class, i -> i.toString())
                .then(s -> s + s)
                .then(s -> {
                    System.out.println("Task in " + Thread.currentThread().getName());
                    return s;
                })
                .then(Long::parseLong)
                .then(l -> l / l))
                .handleFailure(f -> {
                    System.out.println("failure handle in " + Thread.currentThread().getName()+ ". Value was " + f.start());
                });
        Lists<Long> converted = concurrency.map();
        System.out.println(converted);
    }
}
