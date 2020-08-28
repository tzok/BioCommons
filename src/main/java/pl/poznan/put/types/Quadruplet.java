package pl.poznan.put.types;

import org.immutables.value.Value;

@Value.Immutable
public interface Quadruplet<T> {
  @Value.Parameter
  T a();

  @Value.Parameter
  T b();

  @Value.Parameter
  T c();

  @Value.Parameter
  T d();

  default T get(final int index) {
    switch (index) {
      case 0:
        return a();
      case 1:
        return b();
      case 2:
        return c();
      case 3:
        return d();
      default:
        throw new IllegalArgumentException(
            "Quadruplet.get(index) was called with index < 0 or index > 3");
    }
  }
}
