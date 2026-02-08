package fun.with.unstable;

import fun.with.interfaces.actions.ActionConsumer;

public class ConsumingTask<Start, IntermediateSource> {
    private final ActionConsumer<IntermediateSource> consumer;
    private Task<Start, ?, IntermediateSource> predecessor;
    private Task<Start, Start, ?> startTask;

    public ConsumingTask<Start, IntermediateSource> setStartTask(Task<Start, Start, ?> startTask) {
        this.startTask = startTask;
        return this;
    }


    public void consume(IntermediateSource intermediateSource) {
        this.consumer.accept(intermediateSource);
    }


    ConsumingTask(ActionConsumer<IntermediateSource> consumer) {
        this.consumer = consumer;
    }

    public Task<Start, Start, ?> getStartTask() {
        return startTask;
    }

    void setPredecessor(Task<Start, ?, IntermediateSource> predecessor) {
        this.predecessor = predecessor;
    }
}
