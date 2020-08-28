package pl.poznan.put.types;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class QuadrupletTest {
  private Quadruplet<Integer> fromParameters;

  @Before
  public final void setUp() {
    fromParameters = ImmutableQuadruplet.<Integer>builder().a(1).b(2).c(3).d(4).build();
  }

  @Test
  public final void getTest() {
    Assert.assertThat(fromParameters.get(0), is(1));
    Assert.assertThat(fromParameters.get(1), is(2));
    Assert.assertThat(fromParameters.get(2), is(3));
    Assert.assertThat(fromParameters.get(3), is(4));
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
