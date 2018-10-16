package pl.poznan.put.circular.graphics;

import java.awt.FontMetrics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.batik.ext.awt.geom.Polygon2D;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.Axis;
import pl.poznan.put.circular.Circular;
import pl.poznan.put.circular.enums.AngleTransformation;
import pl.poznan.put.circular.exception.InvalidCircularOperationException;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.utility.svg.SVGHelper;

public class RawDataPlot extends AbstractDrawable {
  private final Collection<? extends Circular> data;
  private final double diameter;
  private final boolean isAxes;
  private final double majorTickSpread;
  private final double minorTickSpread;
  private final AngleTransformation angleTransformation;

  private double centerX;
  private double centerY;
  private double radius;
  private double observationSize;
  private double tickTextWidth;

  public RawDataPlot(
      final Collection<? extends Circular> data,
      final double diameter,
      final double majorTickSpread,
      final double minorTickSpread,
      final AngleTransformation angleTransformation) {
    super();
    this.data = new ArrayList<>(data);
    this.diameter = diameter;
    this.majorTickSpread = majorTickSpread;
    this.minorTickSpread = minorTickSpread;
    this.angleTransformation = angleTransformation;

    if (data.isEmpty()) {
      throw new InvalidCircularOperationException("A dataset cannot be empty!");
    }

    isAxes = data.iterator().next() instanceof Axis;
    init();
  }

  public RawDataPlot(final Collection<? extends Circular> data, final double diameter) {
    this(data, diameter, FastMath.PI / 2, FastMath.PI / 12, AngleTransformation.CLOCK);
  }

  public RawDataPlot(final Collection<? extends Circular> data) {
    this(data, 640);
  }

  private void init() {
    // circle center
    centerX = diameter / 2.0;
    centerY = diameter / 2.0;
    // circle radius
    radius = diameter / 2.0;
    // height of single "observation"
    observationSize = 0.02 * radius;
    // max width of text for ticks, until ticks are drawn this is -inf
    tickTextWidth = Double.NEGATIVE_INFINITY;
  }

  protected final double transform(final double radians) {
    return angleTransformation.transform(radians);
  }

  @Override
  public void draw() {
    // main circle
    svgGraphics.draw(new Ellipse2D.Double(0, 0, diameter, diameter));

    // ticks
    final double rminor = 0.95 * radius;
    final double rmajor = 0.85 * radius;
    drawTicks(minorTickSpread, rminor);
    drawTicks(majorTickSpread, rmajor);
    drawTicksText(majorTickSpread);

    // observations for every degree on a circle (map key = 0..360)
    final Map<Integer, List<Circular>> observations = new TreeMap<>();

    for (final Circular circular : data) {
      final double degrees = circular.getDegrees360();
      final int index = (int) degrees;

      if (!observations.containsKey(index)) {
        observations.put(index, new ArrayList<>());
      }
      observations.get(index).add(circular);
    }

    for (final Map.Entry<Integer, List<Circular>> entry : observations.entrySet()) {
      // 't' = angle as in XY coordinate system
      final int degree = entry.getKey();
      final double t = transform(Math.toRadians(degree));
      // point on circle
      final double x = centerX + (radius * FastMath.cos(t));
      final double y = centerY + (radius * FastMath.sin(t));
      // 'a', 'b' = equation for a line from center to this point
      final double slope = (x - centerX) / (y - centerY);

      int i = 0;
      for (final Circular circular : entry.getValue()) {
        // point on virtual circle
        final double virtualRadius = radius + tickTextWidth + ((i + 1) * observationSize);
        final double xv = centerX + (virtualRadius * FastMath.cos(t));
        final double yv = centerY + (virtualRadius * FastMath.sin(t));

        if (circular instanceof Angle) {
          drawObservationTriangle(t, slope, virtualRadius, xv, yv);
          i += 2;
        } else if (circular instanceof Axis) {
          drawObservationCircles(t, virtualRadius, xv, yv);
          i += 1;
        }
      }
    }
  }

  private void drawObservationCircles(
      final double t, final double virtualRadius, final double xv, final double yv) {
    final double x = xv - (observationSize / 2.0);
    final double y = yv + (observationSize / 2.0);
    svgGraphics.draw(new Ellipse2D.Double(x, diameter - y, observationSize, observationSize));
    final double xPlusPI =
        (centerX + (virtualRadius * FastMath.cos(t + FastMath.PI))) - (observationSize / 2.0);
    final double yPlusPI =
        centerY + (virtualRadius * FastMath.sin(t + FastMath.PI)) + (observationSize / 2.0);
    svgGraphics.draw(
        new Ellipse2D.Double(xPlusPI, diameter - yPlusPI, observationSize, observationSize));
  }

  private void drawObservationTriangle(
      final double t,
      final double a,
      final double virtualRadius,
      final double xv,
      final double yv) {
    // special case for 90 and 270 degrees
    int td = (int) FastMath.round(Math.toDegrees(t));
    td += (td < 0) ? 360 : 0;

    final double x1;
    final double y1;
    final double x2;
    final double y2;

    if ((td == 0) || (td == 180)) {
      // special case for 90 and 270 degrees
      x1 = xv;
      y1 = yv + (observationSize / 2);
      x2 = xv;
      y2 = yv - (observationSize / 2);
    } else {
      // 'ap', 'bp' = equation for perpendicular line to 'a',
      // 'b'
      final double ap = -a;
      final double bp = yv + (a * xv);
      // 'sa', 'sb', 'sc' = quadratic equation parameters
      final double sa = 1 + FastMath.pow(ap, 2);
      final double sb = (-2 * xv) + (2 * ap * (bp - yv));
      final double sc =
          (FastMath.pow(xv, 2) + FastMath.pow(bp - yv, 2)) - FastMath.pow(observationSize / 2, 2);
      // solve
      final double delta = (sb * sb) - (4 * sa * sc);
      x1 = (-sb - FastMath.sqrt(delta)) / (2 * sa);
      y1 = (ap * x1) + bp;
      x2 = (-sb + FastMath.sqrt(delta)) / (2 * sa);
      y2 = (ap * x2) + bp;
    }

    // last point is one step further
    final double x3 = centerX + ((virtualRadius + observationSize) * FastMath.cos(t));
    final double y3 = centerY + ((virtualRadius + observationSize) * FastMath.sin(t));

    final float[] xs = {(float) x1, (float) x2, (float) x3};
    final float[] ys = {(float) (diameter - y1), (float) (diameter - y2), (float) (diameter - y3)};
    svgGraphics.draw(new Polygon2D(xs, ys, 3));
  }

  private void drawTicks(final double tickSpread, final double virtualRadius) {
    for (double d = 0; d < MathUtils.TWO_PI; d += tickSpread) {
      // angle as in XY coordinate system
      final double t = transform(d);
      // point on virtual circle
      final double xv = centerX + (virtualRadius * FastMath.cos(t));
      final double yv = centerY - (virtualRadius * FastMath.sin(t));
      // point on circle
      final double x = centerX + (radius * FastMath.cos(t));
      final double y = centerY - (radius * FastMath.sin(t));
      svgGraphics.draw(new Line2D.Double(xv, diameter - yv, x, diameter - y));
    }
  }

  private void drawTicksText(final double tickSpread) {
    final FontMetrics fontMetrics = SVGHelper.getFontMetrics(svgGraphics);
    final double virtualRadius = radius + observationSize;

    for (double d = 0; d < MathUtils.TWO_PI; d += tickSpread) {
      // text to be displayed
      final String text = FastMath.round(Math.toDegrees(d)) + Unicode.DEGREE;
      final Rectangle2D bounds = fontMetrics.getStringBounds(text, svgGraphics);

      // getHeight() sums up all ascents/descents and it distorts the
      // purpose here, so -getY() is better
      final double width = bounds.getWidth();
      final double height = -bounds.getY();
      tickTextWidth = FastMath.max(width, tickTextWidth);

      // angle as in XY coordinate system
      final double t = transform(d);
      // point on virtual circle
      double xv = centerX + (virtualRadius * FastMath.cos(t));
      double yv = centerY - (virtualRadius * FastMath.sin(t));

      // adjust positioning due to fact that Y axis is reverted because
      // point (0,0) is in top left
      if ((xv - centerX) < -0.01) {
        xv -= width;
      }
      if ((yv - centerY) > 0.01) {
        yv += height;
      }

      // center the text around found point coordinates
      xv -= (width / 2) * FastMath.abs(FastMath.sin(t));
      yv += (height / 2) * FastMath.abs(FastMath.cos(t));

      svgGraphics.drawString(text, (float) xv, (float) yv);
    }
  }

  public final Collection<Circular> getData() {
    return Collections.unmodifiableCollection(data);
  }

  public final double getDiameter() {
    return diameter;
  }

  public final boolean isAxes() {
    return isAxes;
  }

  public final double getMajorTickSpread() {
    return majorTickSpread;
  }

  public final double getMinorTickSpread() {
    return minorTickSpread;
  }

  public final double getCenterX() {
    return centerX;
  }

  public final void setCenterX(final double centerX) {
    this.centerX = centerX;
  }

  public final double getCenterY() {
    return centerY;
  }

  public final void setCenterY(final double centerY) {
    this.centerY = centerY;
  }

  public final double getRadius() {
    return radius;
  }

  public final void setRadius(final double radius) {
    this.radius = radius;
  }
}
