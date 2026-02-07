package fun.with.unstable;

import java.util.ArrayList;
import java.util.List;

public class TaskBox<Start, Target> {

    private Task<Start, Start, ?> startTask;
    private List<Task<Start, ?, ?>> intermediateTasks;
    private Task<Start, ?, Target> finalTask;

    public static <X, Y> TaskBox<X, Y> of(Task<X, ?, Y> finalTask) {
        Task<X, X, ?> startTask = finalTask.getStartTask();
        TaskBox<X, Y> taskBox = new TaskBox<>();
        taskBox.startTask = startTask;
        Task<X, ?, ?> currentTask = startTask;
        while (currentTask.hasSuccessor()) {
            Task<X, ?, ?> successor = currentTask.getSuccessor();

            if (successor != null) {
                taskBox.intermediateTasks = taskBox.intermediateTasks == null ? new ArrayList<>() : taskBox.intermediateTasks;
                if (successor.hasSuccessor()) {
                    taskBox.intermediateTasks.add(successor);
                } else {
                    taskBox.finalTask = (Task<X, ?, Y>) successor;
                }
            }
            currentTask = successor;
        }
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
        return (Target) intermediateResult;
    }
}
