package pl.poznan.put.types;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class QuadrupletTest {
  private Quadruplet<Integer> fromParameters;

  @Before
  public final void setUp() {
    final Integer[] array = {1, 2, 3, 4};
    fromParameters = new Quadruplet<>(1, 2, 3, 4);
  }

  @Test
  public final void getTest() {
    assertEquals(1, (int) fromParameters.get(0));
    assertEquals(2, (int) fromParameters.get(1));
    assertEquals(3, (int) fromParameters.get(2));
    assertEquals(4, (int) fromParameters.get(3));
  }

  @Test(expected = IllegalArgumentException.class)
  public final void getLessThanZero() {
    fromParameters.get(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public final void getMoreThanThree() {
    fromParameters.get(4);
  }
}
