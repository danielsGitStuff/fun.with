package fun.with.unstable;

import fun.with.Lists;
import fun.with.interfaces.actions.ActionConsumer;

/**
 * Handles failures of multiple failed {@link Task}s.
 * @param <Start>
 */
public interface TasksFailureHandler<Start> extends ActionConsumer<Lists<TaskFailure<Start>>> {


}