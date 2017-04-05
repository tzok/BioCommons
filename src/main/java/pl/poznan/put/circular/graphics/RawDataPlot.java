package pl.poznan.put.circular.graphics;

import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.Axis;
import pl.poznan.put.circular.Circular;
import pl.poznan.put.circular.exception.InvalidCircularOperationException;
import pl.poznan.put.circular.utility.Helper;

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

    private double centerX;
    private double centerY;
    private double radius;

    public RawDataPlot(
            final Collection<Circular> data, final double diameter,
            final double majorTickSpread, final double minorTickSpread) {
        super();
        this.data = new ArrayList<>(data);
        this.diameter = diameter;
        this.majorTickSpread = majorTickSpread;
        this.minorTickSpread = minorTickSpread;

        if (data.isEmpty()) {
            throw new InvalidCircularOperationException(
                    "A dataset cannot be empty!");
        }

        isAxes = data.iterator().next() instanceof Axis;
        init();
    }

    private void init() {
        // circle center
        centerX = diameter / 2.0;
        centerY = diameter / 2.0;
        // circle radius
        radius = diameter / 2.0;
    }

    public RawDataPlot(final Collection<Circular> data, final double diameter) {
        super();
        this.data = new ArrayList<>(data);
        this.diameter = diameter;
        majorTickSpread = Math.PI / 2;
        minorTickSpread = Math.PI / 12;

        if (data.isEmpty()) {
            throw new InvalidCircularOperationException(
                    "A dataset cannot be empty!");
        }

        isAxes = data.iterator().next() instanceof Axis;
        init();
    }

    public RawDataPlot(final Collection<? extends Circular> data) {
        super();
        this.data = new ArrayList<>(data);
        diameter = 1024;
        majorTickSpread = Math.PI / 2;
        minorTickSpread = Math.PI / 12;

        if (data.isEmpty()) {
            throw new InvalidCircularOperationException(
                    "A dataset cannot be empty!");
        }

        isAxes = data.iterator().next() instanceof Axis;
        init();
    }

    @Override
    public SVGDocument draw() {
        // main circle
        getSvgGraphics().drawOval(0, 0, (int) diameter, (int) diameter);
        // ticks
        double rminor = 0.95 * radius;
        double rmajor = 0.85 * radius;
        drawTicks(getSvgGraphics(), minorTickSpread, rminor);
        drawTicks(getSvgGraphics(), majorTickSpread, rmajor);

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
            double t = -(Math.toRadians(degree) + ((Math.PI * 3) / 2)) % (2
                                                                          *
                                                                          Math.PI);
            // point on circle
            double x = centerX + (radius * StrictMath.cos(t));
            double y = centerY + (radius * StrictMath.sin(t));
            // 'a', 'b' = equation for a line from center to this point
            double a = (x - centerX) / (y - centerY);

            int i = 0;
            for (final Circular circular : entry.getValue()) {
                // point on virtual circle
                double virtualRadius = radius + ((i + 1) * observationSize);
                double xv = centerX + (virtualRadius * StrictMath.cos(t));
                double yv = centerY + (virtualRadius * StrictMath.sin(t));

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
                        // 'sa', 'sb', 'sc' = square equation parameters
                        double sa = 1 + StrictMath.pow(ap, 2);
                        double sb = (-2 * xv) + (2 * ap * (bp - yv));
                        double sc = (StrictMath.pow(xv, 2) + StrictMath
                                .pow(bp - yv, 2)) - StrictMath
                                            .pow(observationSize / 2, 2);
                        // solve
                        double delta = (sb * sb) - (4 * sa * sc);
                        x1 = (-sb - Math.sqrt(delta)) / (2 * sa);
                        y1 = (ap * x1) + bp;
                        x2 = (-sb + Math.sqrt(delta)) / (2 * sa);
                        y2 = (ap * x2) + bp;
                    }

                    // last point is one step further
                    virtualRadius = radius + ((i + 2) * observationSize);
                    double x3 = centerX + (virtualRadius * StrictMath.cos(t));
                    double y3 = centerY + (virtualRadius * StrictMath.sin(t));

                    getSvgGraphics().drawPolygon(
                            new int[]{(int) x1, (int) x2, (int) x3}, new int[]{
                                    (int) (diameter - y1),
                                    (int) (diameter - y2),
                                    (int) (diameter - y3)}, 3);
                    i += 2;
                } else if (circular instanceof Axis) {
                    xv -= observationSize / 2.0;
                    yv += observationSize / 2.0;
                    getSvgGraphics().drawOval((int) xv, (int) (diameter - yv),
                                              (int) observationSize,
                                              (int) observationSize);
                    xv = (centerX + (virtualRadius * StrictMath
                            .cos(t + Math.PI))) - (observationSize / 2.0);
                    yv = centerY + (virtualRadius * StrictMath.sin(t + Math.PI))
                         + (observationSize / 2.0);
                    getSvgGraphics().drawOval((int) xv, (int) (diameter - yv),
                                              (int) observationSize,
                                              (int) observationSize);
                    i += 1;
                }
            }
        }
        return finalizeDrawingAndGetSvg();
    }

    private void drawTicks(
            final SVGGraphics2D graphics, final double tickSpread,
            final double virtualRadius) {
        for (double d = 0; d < (2 * Math.PI); d += tickSpread) {
            // angle as in XY coordinate system
            double t = -(d + ((Math.PI * 3) / 2)) % (2 * Math.PI);
            // point on virtual circle
            double xv = centerX + (virtualRadius * StrictMath.cos(t));
            double yv = centerY + (virtualRadius * StrictMath.sin(t));
            // point on circle
            double x = centerX + (radius * StrictMath.cos(t));
            double y = centerY + (radius * StrictMath.sin(t));
            graphics.drawLine((int) xv, (int) (diameter - yv), (int) x,
                              (int) (diameter - y));
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
