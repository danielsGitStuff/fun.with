package fun.with.interfaces;

import fun.with.annotations.Unstable;

@Unstable
public interface TrySupplier<T> {

  T supply() throws Exception;
}
