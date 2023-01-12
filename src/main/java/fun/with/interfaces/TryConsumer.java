package fun.with.interfaces;

import fun.with.annotations.Unstable;

@Unstable
public interface TryConsumer<T> {

  void consume(T t) throws Exception;
}
