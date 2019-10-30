package pl.poznan.put.constant;

import static org.hamcrest.CoreMatchers.*;

import java.awt.Color;

import org.junit.Assert;
import org.junit.Test;

public class ColorsTest {
  @Test
  public final void getDistinctColors() {
    Assert.assertThat(Colors.getDistinctColors().length, is(33));
  }

  @Test
  public final void interpolateColor() {
    final Color minimumColor = Colors.interpolateColor(0.0, 0.0, 1.0);
    final Color maximumColor = Colors.interpolateColor(1.0, 0.0, 1.0);
    Assert.assertThat(Colors.interpolateColor(-10.0, 0.0, 1.0), is(minimumColor));
    Assert.assertThat(Colors.interpolateColor(10.0, 0.0, 1.0), is(maximumColor));
  }

  @Test
  public final void toHexString() {
    Assert.assertThat(Colors.toHexString(Color.BLACK), is("#000000"));
    Assert.assertThat(Colors.toHexString(Color.RED), is("#FF0000"));
    Assert.assertThat(Colors.toHexString(Color.GREEN), is("#00FF00"));
    Assert.assertThat(Colors.toHexString(Color.BLUE), is("#0000FF"));
    Assert.assertThat(Colors.toHexString(Color.WHITE), is("#FFFFFF"));
  }

  @Test
  public final void toSvgString() {
    Assert.assertThat(Colors.toSvgString(Color.BLACK), is("rgb(0,0,0)"));
    Assert.assertThat(Colors.toSvgString(Color.RED), is("rgb(255,0,0)"));
    Assert.assertThat(Colors.toSvgString(Color.GREEN), is("rgb(0,255,0)"));
    Assert.assertThat(Colors.toSvgString(Color.BLUE), is("rgb(0,0,255)"));
    Assert.assertThat(Colors.toSvgString(Color.WHITE), is("rgb(255,255,255)"));
  }
}
