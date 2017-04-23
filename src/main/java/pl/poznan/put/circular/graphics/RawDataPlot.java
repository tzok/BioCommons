package pl.poznan.put.circular.graphics;

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
            final Collection<? extends Circular> data, final double diameter,
            final double majorTickSpread, final double minorTickSpread,
            final AngleTransformation angleTransformation) {
        super();
        this.data = new ArrayList<>(data);
        this.diameter = diameter;
        this.majorTickSpread = majorTickSpread;
        this.minorTickSpread = minorTickSpread;
        this.angleTransformation = angleTransformation;

        if (data.isEmpty()) {
            throw new InvalidCircularOperationException(
                    "A dataset cannot be empty!");
        }

        isAxes = data.iterator().next() instanceof Axis;
        init();
    }


    public RawDataPlot(
            final Collection<? extends Circular> data, final double diameter) {
        this(data, diameter, Math.PI / 2, Math.PI / 12,
             AngleTransformation.CLOCK);
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
        double rminor = 0.95 * radius;
        double rmajor = 0.85 * radius;
        drawTicks(minorTickSpread, rminor);
        drawTicks(majorTickSpread, rmajor);
        drawTicksText(majorTickSpread);

        // observations for every degree on a circle (map key = 0..360)
        Map<Integer, List<Circular>> observations = new TreeMap<>();

        for (final Circular circular : data) {
            double degrees = circular.getDegrees360();
            int index = (int) degrees;

            if (!observations.containsKey(index)) {
                observations.put(index, new ArrayList<>());
            }
            observations.get(index).add(circular);
        }

        for (final Map.Entry<Integer, List<Circular>> entry : observations
                .entrySet()) {
            // 't' = angle as in XY coordinate system
            int degree = entry.getKey();
            double t = transform(Math.toRadians(degree));
            // point on circle
            double x = centerX + (radius * FastMath.cos(t));
            double y = centerY + (radius * FastMath.sin(t));
            // 'a', 'b' = equation for a line from center to this point
            double a = (x - centerX) / (y - centerY);

            int i = 0;
            for (final Circular circular : entry.getValue()) {
                // point on virtual circle
                double virtualRadius =
                        radius + tickTextWidth + ((i + 1) * observationSize);
                double xv = centerX + (virtualRadius * FastMath.cos(t));
                double yv = centerY + (virtualRadius * FastMath.sin(t));

                if (circular instanceof Angle) {
                    double x1;
                    double y1;
                    double x2;
                    double y2;

                    // special case for 90 and 270 degrees
                    int td = (int) Math.round(Math.toDegrees(t));
                    td += (td < 0) ? 360 : 0;
                    if ((td == 0) || (td == 180)) {
                        // special case for 90 and 270 degrees
                        x1 = xv;
                        y1 = yv + (observationSize / 2);
                        x2 = xv;
                        y2 = yv - (observationSize / 2);
                    } else {
                        // 'ap', 'bp' = equation for perpendicular line to 'a',
                        // 'b'
                        double ap = -a;
                        double bp = yv + (a * xv);
                        // 'sa', 'sb', 'sc' = quadratic equation parameters
                        double sa = 1 + FastMath.pow(ap, 2);
                        double sb = (-2 * xv) + (2 * ap * (bp - yv));
                        double sc =
                                (FastMath.pow(xv, 2) + FastMath.pow(bp - yv, 2))
                                - FastMath.pow(observationSize / 2, 2);
                        // solve
                        double delta = (sb * sb) - (4 * sa * sc);
                        x1 = (-sb - Math.sqrt(delta)) / (2 * sa);
                        y1 = (ap * x1) + bp;
                        x2 = (-sb + Math.sqrt(delta)) / (2 * sa);
                        y2 = (ap * x2) + bp;
                    }

                    // last point is one step further
                    double x3 = centerX + ((virtualRadius + observationSize)
                                           * FastMath.cos(t));
                    double y3 = centerY + ((virtualRadius + observationSize)
                                           * FastMath.sin(t));

                    float[] xs = {(float) x1, (float) x2, (float) x3};
                    float[] ys = {
                            (float) (diameter - y1), (float) (diameter - y2),
                            (float) (diameter - y3)};
                    svgGraphics.draw(new Polygon2D(xs, ys, 3));
                    i += 2;
                } else if (circular instanceof Axis) {
                    xv -= observationSize / 2.0;
                    yv += observationSize / 2.0;

                    svgGraphics.draw(new Ellipse2D.Double(xv, diameter - yv,
                                                          observationSize,
                                                          observationSize));
                    xv = (centerX + (virtualRadius * FastMath.cos(t + Math.PI)))
                         - (observationSize / 2.0);
                    yv = centerY + (virtualRadius * FastMath.sin(t + Math.PI))
                         + (observationSize / 2.0);
                    svgGraphics.draw(new Ellipse2D.Double(xv, diameter - yv,
                                                          observationSize,
                                                          observationSize));
                    i += 1;
                }
            }
        }
    }

    private void drawTicks(
            final double tickSpread, final double virtualRadius) {
        for (double d = 0; d < MathUtils.TWO_PI; d += tickSpread) {
            // angle as in XY coordinate system
            double t = transform(d);
            // point on virtual circle
            double xv = centerX + (virtualRadius * FastMath.cos(t));
            double yv = centerY - (virtualRadius * FastMath.sin(t));
            // point on circle
            double x = centerX + (radius * FastMath.cos(t));
            double y = centerY - (radius * FastMath.sin(t));
            svgGraphics.draw(new Line2D.Double(xv, diameter - yv, x,
                                               diameter - y));
        }
    }

    private void drawTicksText(
            final double tickSpread) {
        FontMetrics fontMetrics = SVGHelper.getFontMetrics(svgGraphics);
        double virtualRadius = radius + observationSize;

        for (double d = 0; d < MathUtils.TWO_PI; d += tickSpread) {
            // text to be displayed
            String text = Math.round(Math.toDegrees(d)) + Unicode.DEGREE;
            Rectangle2D bounds = fontMetrics.getStringBounds(text, svgGraphics);

            // getHeight() sums up all ascents/descents and it distorts the
            // purpose here, so -getY() is better
            double width = bounds.getWidth();
            double height = -bounds.getY();
            tickTextWidth = Math.max(width, tickTextWidth);

            // angle as in XY coordinate system
            double t = transform(d);
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
            xv -= (width / 2) * Math.abs(FastMath.sin(t));
            yv += (height / 2) * Math.abs(FastMath.cos(t));

            svgGraphics.drawString(text, (float) xv, (float) yv);
        }
    }

    public final Collection<? extends Circular> getData() {
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
