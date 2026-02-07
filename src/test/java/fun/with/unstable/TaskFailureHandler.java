package fun.with.unstable;

import fun.with.interfaces.actions.ActionConsumer;

/**
 * Handles one failure of a particular {@link Task}.
 * @param <Start>
 */
public interface TaskFailureHandler<Start> extends ActionConsumer<TaskFailure<Start>> {


}