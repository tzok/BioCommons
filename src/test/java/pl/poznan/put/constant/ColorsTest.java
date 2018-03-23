package pl.poznan.put.constant;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import org.junit.Test;

public class ColorsTest {
  @Test
  public void getDistinctColors() {
    assertEquals(33, Colors.getDistinctColors().length);
  }

  @Test
  public void interpolateColor() {
    final Color minimumColor = Colors.interpolateColor(0.0, 0.0, 1.0);
    final Color maximumColor = Colors.interpolateColor(1.0, 0.0, 1.0);
    assertEquals(minimumColor, Colors.interpolateColor(-10.0, 0.0, 1.0));
    assertEquals(maximumColor, Colors.interpolateColor(10.0, 0.0, 1.0));
  }

  @Test
  public void toHexString() {
    assertEquals("#000000", Colors.toHexString(Color.BLACK));
    assertEquals("#FF0000", Colors.toHexString(Color.RED));
    assertEquals("#00FF00", Colors.toHexString(Color.GREEN));
    assertEquals("#0000FF", Colors.toHexString(Color.BLUE));
    assertEquals("#FFFFFF", Colors.toHexString(Color.WHITE));
  }

  @Test
  public void toSvgString() {
    assertEquals("rgb(0,0,0)", Colors.toSvgString(Color.BLACK));
    assertEquals("rgb(255,0,0)", Colors.toSvgString(Color.RED));
    assertEquals("rgb(0,255,0)", Colors.toSvgString(Color.GREEN));
    assertEquals("rgb(0,0,255)", Colors.toSvgString(Color.BLUE));
    assertEquals("rgb(255,255,255)", Colors.toSvgString(Color.WHITE));
  }
}
