package pl.poznan.put.types;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableQuadruple.class)
@JsonDeserialize(as = ImmutableQuadruple.class)
public interface Quadruple<T> {
  @Value.Parameter(order = 1)
  T a();

  @Value.Parameter(order = 2)
  T b();

  @Value.Parameter(order = 3)
  T c();

  @Value.Parameter(order = 4)
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
