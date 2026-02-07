package fun.with.unstable;

import fun.with.interfaces.actions.ActionFunction;

import java.util.Objects;
import java.util.function.Function;

public class Task<Start, IntermediateSource, Target> {
    private final ActionFunction<IntermediateSource, Target> function;

    private Task<Start, ?, IntermediateSource> predecessor;
    private Task<Start, Target, ?> successor;
    private Task<Start, Start, ?> startTask;

    private Task(ActionFunction<IntermediateSource, Target> function) {
        this.function = function;
    }

    public static <X, Y> Task<X, X, Y> of(Class<X>  clazz,ActionFunction<X, Y> function) {
        Task<X, X, Y> task = new Task<>(function);
        task.startTask = task;
        return task;
    }

    public <X> Task<Start, Target, X> then(ActionFunction<Target, X> function) {
        Task<Start, Target, X> successor = new Task<>(function);
        successor.predecessor = this;
        this.successor = successor;
        successor.startTask = this.getStartTask();
        return successor;
    }

    public Task<Start, Start, ?> getStartTask() {
        return startTask;
    }

    public Task<Start, ?, IntermediateSource> getPredecessor() {
        return predecessor;
    }

    public Task<Start, Target, ?> getSuccessor() {
        return successor;
    }

    public boolean hasSuccessor() {
        return this.successor != null;
    }

    public Target apply(IntermediateSource intermediateSource) {
        return this.function.apply(intermediateSource);
    }

    public Object applyObj(Object intermediateSource) {
        return this.function.apply((IntermediateSource) intermediateSource);
    }
}
