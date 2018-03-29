package pl.poznan.put.circular.graphics;

import java.awt.FontMetrics;
import java.awt.font.LineMetrics;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.math3.util.MathUtils;
import pl.poznan.put.circular.Circular;
import pl.poznan.put.circular.Histogram;
import pl.poznan.put.utility.AngleFormat;
import pl.poznan.put.utility.svg.SVGHelper;

public class LinearHistogram extends AbstractDrawable {
  private final Collection<Circular> data;
  private final double binRadians;
  private final int drawingUnitSize;

  public LinearHistogram(
      final Collection<? extends Circular> data,
      final double binRadians,
      final int drawingUnitSize) {
    super();
    this.data = new ArrayList<>(data);
    this.binRadians = binRadians;
    this.drawingUnitSize = drawingUnitSize;
  }

  public LinearHistogram(final Collection<? extends Circular> data, final double binRadians) {
    this(data, binRadians, 20);
  }

  public LinearHistogram(final Collection<? extends Circular> data) {
    this(data, Math.PI / 12, 20);
  }

  @Override
  public final void draw() {
    final Histogram histogram = new Histogram(data, binRadians);
    double maxHeight = Double.NEGATIVE_INFINITY;
    int maxFrequency = Integer.MIN_VALUE;
    int i = 0;

    for (double d = 0; d < MathUtils.TWO_PI; d += binRadians, i += 1) {
      final int frequency = histogram.getBinSize(d);
      final int height = frequency * drawingUnitSize;
      svgGraphics.drawRect(i * drawingUnitSize, -height, drawingUnitSize, height);

      maxFrequency = Math.max(frequency, maxFrequency);
      maxHeight = Math.max(height, maxHeight);
    }
    final double maxWidth = i * drawingUnitSize;

    /*
     * X axis lines
     */
    svgGraphics.drawLine(0, drawingUnitSize, (int) maxWidth, drawingUnitSize);
    svgGraphics.drawLine(0, drawingUnitSize, 0, (int) (drawingUnitSize + (0.2 * drawingUnitSize)));
    svgGraphics.drawLine(
        (int) maxWidth,
        drawingUnitSize,
        (int) maxWidth,
        (int) (drawingUnitSize + (0.2 * drawingUnitSize)));
    /*
     * Y axis lines
     */
    svgGraphics.drawLine(-drawingUnitSize, (int) -maxHeight, -drawingUnitSize, 0);
    svgGraphics.drawLine(
        -drawingUnitSize,
        (int) -maxHeight,
        (int) (-drawingUnitSize - (0.2 * drawingUnitSize)),
        (int) -maxHeight);
    svgGraphics.drawLine(
        -drawingUnitSize, 0, (int) (-drawingUnitSize - (0.2 * drawingUnitSize)), 0);

    final LineMetrics lineMetrics = SVGHelper.getLineMetrics(svgGraphics);
    final FontMetrics fontMetrics = SVGHelper.getFontMetrics(svgGraphics);
    final float fontHeight = lineMetrics.getHeight();

    for (int j = 0; j <= maxFrequency; j++) {
      svgGraphics.drawString(
          String.valueOf(j), -drawingUnitSize << 1, (-j * drawingUnitSize) + (fontHeight / 6));
    }

    i = 0;
    for (double d = 0; d < MathUtils.TWO_PI; d += binRadians, i += 1) {
      final String label = AngleFormat.degreesRoundedToOne(d);
      final int labelWidth = fontMetrics.stringWidth(label.substring(0, label.length() - 1));
      svgGraphics.drawString(
          label,
          ((i * drawingUnitSize) + (drawingUnitSize / 2)) - (labelWidth / 2),
          (drawingUnitSize << 1) + (((i % 2) == 0) ? 0 : drawingUnitSize));
    }
  }
}
