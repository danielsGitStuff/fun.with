package fun.with.interfaces;

import fun.with.annotations.Unstable;

@Unstable
public interface TryWithFunction<T, Re> {

  Re run(T t) throws Exception;
}
