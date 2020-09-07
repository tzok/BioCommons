package pl.poznan.put.types;

import org.immutables.value.Value;

@Value.Immutable
public interface Quadruplet<T> {
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

  @Value.Parameter(order = 1)
  T a();

  @Value.Parameter(order = 2)
  T b();

  @Value.Parameter(order = 3)
  T c();

  @Value.Parameter(order = 4)
  T d();
}
