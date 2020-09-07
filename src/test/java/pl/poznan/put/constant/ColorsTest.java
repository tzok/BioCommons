package pl.poznan.put.constant;

import org.junit.Test;

import java.awt.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ColorsTest {
  @Test
  public final void getDistinctColors() {
    assertThat(Colors.getDistinctColors().length, is(33));
  }

  @Test
  public final void interpolateColor() {
    final Color minimumColor = Colors.interpolateColor(0.0, 0.0, 1.0);
    final Color maximumColor = Colors.interpolateColor(1.0, 0.0, 1.0);
    assertThat(Colors.interpolateColor(-10.0, 0.0, 1.0), is(minimumColor));
    assertThat(Colors.interpolateColor(10.0, 0.0, 1.0), is(maximumColor));
  }

  @Test
  public final void toHexString() {
    assertThat(Colors.toHexString(Color.BLACK), is("#000000"));
    assertThat(Colors.toHexString(Color.RED), is("#FF0000"));
    assertThat(Colors.toHexString(Color.GREEN), is("#00FF00"));
    assertThat(Colors.toHexString(Color.BLUE), is("#0000FF"));
    assertThat(Colors.toHexString(Color.WHITE), is("#FFFFFF"));
  }

  @Test
  public final void toSvgString() {
    assertThat(Colors.toSvgString(Color.BLACK), is("rgb(0,0,0)"));
    assertThat(Colors.toSvgString(Color.RED), is("rgb(255,0,0)"));
    assertThat(Colors.toSvgString(Color.GREEN), is("rgb(0,255,0)"));
    assertThat(Colors.toSvgString(Color.BLUE), is("rgb(0,0,255)"));
    assertThat(Colors.toSvgString(Color.WHITE), is("rgb(255,255,255)"));
  }
}
