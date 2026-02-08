package fun.with.unstable;

import fun.with.interfaces.actions.ActionConsumer;
import fun.with.interfaces.actions.ActionFunction;

public class Task<Start, IntermediateSource, Target> {
    private ActionFunction<IntermediateSource, Target> function;

    private Task<Start, ?, IntermediateSource> predecessor;
    private Task<Start, Target, ?> successor;
    private Task<Start, Start, ?> startTask;
    private ConsumingTask<Start, Target> consumingTask;

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

    public <X> ConsumingTask<Start, Target> consume(ActionConsumer<Target> consumer) {
        ConsumingTask<Start, Target> consumingTask = new ConsumingTask<>(consumer);
        consumingTask.setStartTask(this.getStartTask());
        consumingTask.setPredecessor(this);
        this.consumingTask = consumingTask;
        return consumingTask;
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
