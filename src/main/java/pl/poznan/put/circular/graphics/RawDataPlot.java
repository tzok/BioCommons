package pl.poznan.put.circular.graphics;

import org.apache.batik.ext.awt.geom.Polygon2D;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.Axis;
import pl.poznan.put.circular.Circular;
import pl.poznan.put.circular.enums.AngleTransformation;
import pl.poznan.put.circular.exception.InvalidCircularOperationException;
import pl.poznan.put.circular.utility.Helper;
import pl.poznan.put.constant.Unicode;
import pl.poznan.put.utility.svg.SVGHelper;

import java.awt.FontMetrics;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RawDataPlot extends AbstractDrawable {
    private static final double PI_TIMES_3_BY_2 = 4.71238898038468985766;

    public static void main(final String[] args)
            throws IOException, FileNotFoundException {
        /*
         * First example
         */
        String circularsData = Helper.readResource("example/D01");
        List<Circular> circulars = Helper.loadHourMinuteData(circularsData);
        Drawable circularsPlot = new RawDataPlot(circulars);
        SVGDocument circularsSvg = circularsPlot.draw();
        Helper.exportSvg(circularsSvg, File.createTempFile("D01", ".svg"));

        /*
         * Second example
         */
        String axesData = Helper.readResource("example/D02");
        List<Axis> axes = Helper.loadAxisData(axesData);
        Drawable axesPlot = new RawDataPlot(axes);
        SVGDocument axesSvg = axesPlot.draw();
        Helper.exportSvg(axesSvg, File.createTempFile("D02", ".svg"));
    }

    private final Collection<? extends Circular> data;
    private final double diameter;
    private final boolean isAxes;
    private final double majorTickSpread;
    private final double minorTickSpread;
    private final AngleTransformation angleTransformation;

    private double centerX;
    private double centerY;
    private double radius;

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
    }

    protected final double transform(final double radians) {
        return angleTransformation.transform(radians);
    }

    @Override
    public SVGDocument draw() {
        // main circle
        SVGGraphics2D graphics = getSvgGraphics();
        graphics.draw(new Ellipse2D.Double(0, 0, diameter, diameter));

        // ticks
        double rminor = 0.95 * radius;
        double rmajor = 0.85 * radius;
        drawTicks(graphics, minorTickSpread, rminor);
        drawTicks(graphics, majorTickSpread, rmajor);
        drawTicksText(graphics, majorTickSpread);

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

        double observationSize = 0.02 * radius;

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
                double virtualRadius = radius + ((i + 1) * observationSize);
                double xv = centerX + (virtualRadius * FastMath.cos(t));
                double yv = centerY + (virtualRadius * FastMath.sin(t));

                if (circular instanceof Angle) {
                    double x1;
                    double y1;
                    double x2;
                    double y2;

                    // special case is required for 90 and 270 degrees
                    if ((degree == 90) || (degree == 270)) {
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
                    virtualRadius = radius + ((i + 2) * observationSize);
                    double x3 = centerX + (virtualRadius * FastMath.cos(t));
                    double y3 = centerY + (virtualRadius * FastMath.sin(t));

                    float[] xs = {(float) x1, (float) x2, (float) x3};
                    float[] ys = {
                            (float) (diameter - y1), (float) (diameter - y2),
                            (float) (diameter - y3)};
                    graphics.draw(new Polygon2D(xs, ys, 3));
                    i += 2;
                } else if (circular instanceof Axis) {
                    xv -= observationSize / 2.0;
                    yv += observationSize / 2.0;

                    graphics.draw(new Ellipse2D.Double(xv, diameter - yv,
                                                       observationSize,
                                                       observationSize));
                    xv = (centerX + (virtualRadius * FastMath.cos(t + Math.PI)))
                         - (observationSize / 2.0);
                    yv = centerY + (virtualRadius * FastMath.sin(t + Math.PI))
                         + (observationSize / 2.0);
                    graphics.draw(new Ellipse2D.Double(xv, diameter - yv,
                                                       observationSize,
                                                       observationSize));
                    i += 1;
                }
            }
        }
        return finalizeDrawingAndGetSvg();
    }

    private void drawTicks(
            final SVGGraphics2D graphics, final double tickSpread,
            final double virtualRadius) {
        for (double d = 0; d < MathUtils.TWO_PI; d += tickSpread) {
            // angle as in XY coordinate system
            double t = transform(d);
            // point on virtual circle
            double xv = centerX + (virtualRadius * FastMath.cos(t));
            double yv = centerY + (virtualRadius * FastMath.sin(t));
            // point on circle
            double x = centerX + (radius * FastMath.cos(t));
            double y = centerY + (radius * FastMath.sin(t));
            graphics.draw(
                    new Line2D.Double(xv, diameter - yv, x, diameter - y));
        }
    }

    private void drawTicksText(
            final SVGGraphics2D graphics, final double tickSpread) {
        FontMetrics fontMetrics = SVGHelper.getFontMetrics(graphics);
        double maxWidth = Double.NEGATIVE_INFINITY;
        double maxHeight = Double.NEGATIVE_INFINITY;

        for (double d = 0; d < MathUtils.TWO_PI; d += tickSpread) {
            // text to be displayed
            String text = Math.round(Math.toDegrees(d)) + Unicode.DEGREE;
            Rectangle2D bounds = fontMetrics.getStringBounds(text, graphics);
            maxWidth = Math.max(bounds.getWidth(), maxWidth);
            maxHeight = Math.max(bounds.getHeight(), maxHeight);
        }

        double virtualRadius = radius + Math.max(maxWidth, maxHeight);

        for (double d = 0; d < MathUtils.TWO_PI; d += tickSpread) {
            // text to be displayed
            String text = Math.round(Math.toDegrees(d)) + Unicode.DEGREE;
            // angle as in XY coordinate system
            double t = transform(d);
            // point on virtual circle
            double xv = centerX + (virtualRadius * FastMath.cos(t));
            double yv = centerY + (virtualRadius * FastMath.sin(t));

            Rectangle2D bounds = fontMetrics.getStringBounds(text, graphics);
            xv -= bounds.getWidth() / 2;

            graphics.drawString(text, (float) xv, (float) yv);
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
