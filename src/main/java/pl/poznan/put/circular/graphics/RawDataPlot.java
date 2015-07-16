package pl.poznan.put.circular.graphics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.svg.SVGDocument;

import pl.poznan.put.circular.Axis;
import pl.poznan.put.circular.Circular;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.exception.InvalidCircularOperationException;
import pl.poznan.put.circular.exception.InvalidCircularValueException;
import pl.poznan.put.circular.exception.InvalidVectorFormatException;
import pl.poznan.put.utility.svg.Format;
import pl.poznan.put.utility.svg.SVGHelper;

public class RawDataPlot extends AbstractDrawable {
    protected final Collection<? extends Circular> data;
    protected final double diameter;
    protected final boolean isAxes;
    private final double majorTickSpread;
    private final double minorTickSpread;

    protected double centerX;
    protected double centerY;
    protected double radius;

    public RawDataPlot(Collection<Circular> data, double diameter,
            double majorTickSpread, double minorTickSpread) throws InvalidCircularOperationException {
        super();
        this.data = data;
        this.diameter = diameter;
        this.majorTickSpread = majorTickSpread;
        this.minorTickSpread = minorTickSpread;

        if (data.size() == 0) {
            throw new InvalidCircularOperationException("A dataset cannot be empty!");
        }

        this.isAxes = data.iterator().next() instanceof Axis;
        init();
    }

    public RawDataPlot(Collection<Circular> data, double diameter) throws InvalidCircularOperationException {
        super();
        this.data = data;
        this.diameter = diameter;
        this.majorTickSpread = Math.PI / 2;
        this.minorTickSpread = Math.PI / 12;

        if (data.size() == 0) {
            throw new InvalidCircularOperationException("A dataset cannot be empty!");
        }

        this.isAxes = data.iterator().next() instanceof Axis;
        init();
    }

    public RawDataPlot(Collection<? extends Circular> data) throws InvalidCircularOperationException {
        super();
        this.data = data;
        this.diameter = 1024;
        this.majorTickSpread = Math.PI / 2;
        this.minorTickSpread = Math.PI / 12;

        if (data.size() == 0) {
            throw new InvalidCircularOperationException("A dataset cannot be empty!");
        }

        this.isAxes = data.iterator().next() instanceof Axis;
        init();
    }

    private void init() {
        // circle center
        centerX = diameter / 2.0;
        centerY = diameter / 2.0;
        // circle radius
        radius = diameter / 2.0;
    }

    @Override
    public void draw() throws InvalidCircularValueException {
        // main circle
        svgGraphics.drawOval(0, 0, (int) diameter, (int) diameter);
        // ticks
        double rminor = 0.95 * radius;
        double rmajor = 0.85 * radius;
        drawTicks(svgGraphics, minorTickSpread, rminor);
        drawTicks(svgGraphics, majorTickSpread, rmajor);

        // observations for every degree on a circle (map key = 0..360)
        Map<Integer, List<Circular>> observations = new TreeMap<Integer, List<Circular>>();

        for (Circular circular : data) {
            double degrees = circular.getDegrees360();
            int index = (int) degrees;

            if (!observations.containsKey(index)) {
                observations.put(index, new ArrayList<Circular>());
            }
            observations.get(index).add(circular);
        }

        double observationSize = 0.02 * radius;

        for (Entry<Integer, List<Circular>> entry : observations.entrySet()) {
            // 't' = angle as in XY coordinate system
            int degree = entry.getKey();
            double t = -(Math.toRadians(degree) + Math.PI * 3 / 2) % (2 * Math.PI);
            // point on circle
            double x = centerX + radius * Math.cos(t);
            double y = centerY + radius * Math.sin(t);
            // 'a', 'b' = equation for a line from center to this point
            double a = (x - centerX) / (y - centerY);

            int i = 0;
            for (Circular circular : entry.getValue()) {
                // point on virtual circle
                double virtualRadius = radius + (i + 1) * observationSize;
                double xv = centerX + virtualRadius * Math.cos(t);
                double yv = centerY + virtualRadius * Math.sin(t);

                if (circular instanceof Angle) {
                    double x1, y1, x2, y2;

                    // special case is required for 90 and 270 degrees
                    if (degree == 90 || degree == 270) {
                        x1 = xv;
                        y1 = yv + observationSize / 2;
                        x2 = xv;
                        y2 = yv - observationSize / 2;
                    } else {
                        // 'ap', 'bp' = equation for perpendicular line to 'a',
                        // 'b'
                        double ap = -a;
                        double bp = yv + a * xv;
                        // 'sa', 'sb', 'sc' = square equation parameters
                        double sa = 1 + Math.pow(ap, 2);
                        double sb = -2 * xv + 2 * ap * (bp - yv);
                        double sc = Math.pow(xv, 2) + Math.pow(bp - yv, 2) - Math.pow(observationSize / 2, 2);
                        // solve
                        double delta = sb * sb - 4 * sa * sc;
                        x1 = (-sb - Math.sqrt(delta)) / (2 * sa);
                        y1 = ap * x1 + bp;
                        x2 = (-sb + Math.sqrt(delta)) / (2 * sa);
                        y2 = ap * x2 + bp;
                    }

                    // last point is one step further
                    virtualRadius = radius + (i + 2) * observationSize;
                    double x3 = centerX + virtualRadius * Math.cos(t);
                    double y3 = centerY + virtualRadius * Math.sin(t);

                    svgGraphics.drawPolygon(new int[] { (int) x1, (int) x2, (int) x3 }, new int[] { (int) (diameter - y1), (int) (diameter - y2), (int) (diameter - y3) }, 3);
                    i += 2;
                } else if (circular instanceof Axis) {
                    xv -= observationSize / 2.0;
                    yv += observationSize / 2.0;
                    svgGraphics.drawOval((int) xv, (int) (diameter - yv), (int) observationSize, (int) observationSize);
                    xv = centerX + virtualRadius * Math.cos(t + Math.PI) - observationSize / 2.0;
                    yv = centerY + virtualRadius * Math.sin(t + Math.PI) + observationSize / 2.0;
                    svgGraphics.drawOval((int) xv, (int) (diameter - yv), (int) observationSize, (int) observationSize);
                    i += 1;
                }
            }
        }
    }

    private void drawTicks(SVGGraphics2D graphics, double tickSpread,
            double virtualRadius) {
        for (double d = 0; d < 2 * Math.PI; d += tickSpread) {
            // angle as in XY coordinate system
            double t = -(d + Math.PI * 3 / 2) % (2 * Math.PI);
            // point on virtual circle
            double xv = centerX + virtualRadius * Math.cos(t);
            double yv = centerY + virtualRadius * Math.sin(t);
            // point on circle
            double x = centerX + radius * Math.cos(t);
            double y = centerY + radius * Math.sin(t);
            graphics.drawLine((int) xv, (int) (diameter - yv), (int) x, (int) (diameter - y));
        }
    }

    public static void main(String[] args) throws IOException, InvalidVectorFormatException, InvalidCircularValueException, InvalidCircularOperationException {
        /*
         * First example
         */
        List<Circular> data = new ArrayList<Circular>();
        List<String> lines = FileUtils.readLines(new File("data/D01"), "UTF-8");

        for (String line : lines) {
            if (line.startsWith("#")) {
                continue;
            }

            for (String token : StringUtils.split(line)) {
                if (!StringUtils.isBlank(token)) {
                    data.add(Angle.fromHourMinuteString(token));
                }
            }
        }

        RawDataPlot plot = new RawDataPlot(data);
        plot.draw();
        SVGDocument svgDocument = plot.finalizeDrawingAndGetSVG();
        OutputStream stream = null;

        try {
            stream = new FileOutputStream("/tmp/D01-plot.svg");
            SVGHelper.export(svgDocument, stream, Format.SVG, null);
        } finally {
            IOUtils.closeQuietly(stream);
        }

        /*
         * Second example
         */
        data.clear();
        lines = FileUtils.readLines(new File("data/D02"), "UTF-8");

        for (String line : lines) {
            if (line.startsWith("#")) {
                continue;
            }

            for (String token : StringUtils.split(line)) {
                if (!StringUtils.isBlank(token)) {
                    double degrees = Double.parseDouble(token);
                    data.add(new Axis(Math.toRadians(degrees)));
                }
            }
        }

        plot = new RawDataPlot(data);
        plot.draw();
        svgDocument = plot.finalizeDrawingAndGetSVG();

        try {
            stream = new FileOutputStream("/tmp/D02-plot.svg");
            SVGHelper.export(svgDocument, stream, Format.SVG, null);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }
}
