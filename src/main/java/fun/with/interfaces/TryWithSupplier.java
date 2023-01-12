package fun.with.interfaces;

import fun.with.annotations.Unstable;

@Unstable
public interface TryWithSupplier<T> {

  T supply() throws Exception;
}
