package pl.poznan.put.types;

import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Quadruplet<T> {
  private final T a;
  private final T b;
  private final T c;
  private final T d;

  public Quadruplet(final List<T> list) {
    super();
    assert list.size() == 4;
    a = list.get(0);
    b = list.get(1);
    c = list.get(2);
    d = list.get(3);
  }

  public Quadruplet(final T a, final T b, final T c, final T d) {
    super();
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
  }

  public Quadruplet(final T[] array) {
    super();
    assert array.length == 4;
    a = array[0];
    b = array[1];
    c = array[2];
    d = array[3];
  }

  public final T get(final int index) {
    assert (index >= 0) && (index <= 3);

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
        break;
    }

    throw new IllegalArgumentException(
        "UniTypeQuadruplet.get(index) was called with index < 0 or index > 3");
  }
}
