package pl.poznan.put.types;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Quadruplet<T> {
  public final T a;
  public final T b;
  public final T c;
  public final T d;

  public Quadruplet(final T a, final T b, final T c, final T d) {
    super();
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
  }

  public final T get(final int index) {
    switch (index) {
      case 0:
        return a;
      case 1:
        return b;
      case 2:
        return c;
      case 3:
        return d;
      default:
        throw new IllegalArgumentException(
            "UniTypeQuadruplet.get(index) was called with index < 0 or index > 3");
    }
  }
}
