package fun.with.unstable;

import java.util.ArrayList;
import java.util.List;

public class TaskBox<Start, Target> {

    private Task<Start, Start, ?> startTask;
    private ConsumingTask<Start, Target> startConsumingTask;
    private List<Task<Start, ?, ?>> intermediateTasks;
    private Task<Start, ?, Target> finalTask;
    private ConsumingTask<?, Target> finalConsumingTask;

    public static <X,Y> TaskBox<X,Y> of(ConsumingTask<X,Y> finalConsumingTask){
        TaskBox<X,Y> box = new TaskBox<>();
        Task<X, X, ?> startTask = finalConsumingTask.getStartTask();
        if (startTask == null){
            box.startConsumingTask = finalConsumingTask;
        }else {
            box.startTask = startTask;
            box.appendTasks(startTask);
        }
        box.finalConsumingTask = finalConsumingTask;
        return box;
    }

    private void appendTasks(Task<Start, ?, ?> currentTaskFromStart){
        while (currentTaskFromStart.hasSuccessor()) {
            Task<Start, ?, ?> successor = currentTaskFromStart.getSuccessor();
            if (successor != null) {
                this.intermediateTasks = this.intermediateTasks == null ? new ArrayList<>() : this.intermediateTasks;
                if (successor.hasSuccessor()) {
                    this.intermediateTasks.add(successor);
                } else {
                    this.finalTask = (Task<Start, ?, Target>) successor;
                }
            }
            currentTaskFromStart = successor;
        }
    }

    public static <X, Y> TaskBox<X, Y> of(Task<X, ?, Y> finalTask) {
        Task<X, X, ?> startTask = finalTask.getStartTask();
        TaskBox<X, Y> taskBox = new TaskBox<>();
        taskBox.startTask = startTask;
        taskBox.appendTasks(startTask);
        return taskBox;
    }

    public Target map(Start s) {
        Object intermediateResult = this.startTask.apply(s);
        if (this.intermediateTasks != null) {
            for (Task<Start, ?, ?> task : this.intermediateTasks) {
                intermediateResult = task.applyObj(intermediateResult);
            }
        }
        if (this.finalTask != null) {
            intermediateResult = this.finalTask.applyObj(intermediateResult);
        }
        if (this.finalConsumingTask != null){
            this.finalConsumingTask.consume((Target) intermediateResult);
        }
        return (Target) intermediateResult;
    }
}
