package fun.with.promise;

import fun.with.interfaces.actions.ActionConsumer;

public interface Promise<DoneType, ErrorType> {

    Promise<DoneType, ErrorType> onDone(ActionConsumer<DoneType> consumer);
    Promise<DoneType, ErrorType> onError(ActionConsumer<ErrorType> consumer);

    boolean isPending();
    boolean isOk();
    boolean isFailed();

    ErrorType getError();

    DoneType getResult();

    boolean hasError();

    boolean hasResult();
}
