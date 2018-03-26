package pl.poznan.put.types;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class QuadrupletTest {
  private Quadruplet<Integer> fromArray;
  private Quadruplet<Integer> fromList;
  private Quadruplet<Integer> fromParameters;

  @Before
  public void setUp() {
    final Integer[] array = {1, 2, 3, 4};
    fromArray = new Quadruplet<>(array);
    fromList = new Quadruplet<>(Arrays.asList(array));
    fromParameters = new Quadruplet<>(array[0], array[1], array[2], array[3]);
  }

  @Test
  public void getTest() {
    assertEquals(1, (int) fromArray.get(0));
    assertEquals(2, (int) fromArray.get(1));
    assertEquals(3, (int) fromArray.get(2));
    assertEquals(4, (int) fromArray.get(3));
  }

  @Test(expected = IllegalArgumentException.class)
  public void getLessThanZero() {
    fromArray.get(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getMoreThanThree() {
    fromArray.get(4);
  }

  @Test
  public void equalsTest() {
    assertEquals(fromArray, fromList);
    assertEquals(fromArray, fromParameters);
    assertEquals(fromList, fromParameters);
  }

  @Test
  public void hashCodeTest() {
    assertEquals(fromArray.hashCode(), fromList.hashCode());
    assertEquals(fromArray.hashCode(), fromParameters.hashCode());
    assertEquals(fromList.hashCode(), fromParameters.hashCode());
  }
}
