package fun.with.interfaces;

import fun.with.annotations.Unstable;

@Unstable
public interface TryWithConsumer<T> {

  void consume(T t) throws Exception;
}
