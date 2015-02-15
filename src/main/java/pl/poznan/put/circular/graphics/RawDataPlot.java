package pl.poznan.put.circular.graphics;

import java.awt.geom.Rectangle2D;
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

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

import pl.poznan.put.circular.Axis;
import pl.poznan.put.circular.Circular;
import pl.poznan.put.circular.Vector;
import pl.poznan.put.circular.exception.InvalidCircularValueException;
import pl.poznan.put.circular.exception.InvalidVectorFormatException;
import pl.poznan.put.utility.svg.Format;
import pl.poznan.put.utility.svg.SVGHelper;

public class RawDataPlot {
    private final Collection<Circular> data;
    private final double diameter;
    private final double majorTickSpread;
    private final double minorTickSpread;

    private double centerX;
    private double centerY;
    private double radius;

    public RawDataPlot(Collection<Circular> data, double diameter, double majorTickSpread, double minorTickSpread) {
        super();
        this.data = data;
        this.diameter = diameter;
        this.majorTickSpread = majorTickSpread;
        this.minorTickSpread = minorTickSpread;
        init();
    }

    public RawDataPlot(Collection<Circular> data, double diameter) {
        super();
        this.data = data;
        this.diameter = diameter;
        this.majorTickSpread = Math.PI / 2;
        this.minorTickSpread = Math.PI / 12;
        init();
    }

    public RawDataPlot(Collection<Circular> data) {
        super();
        this.data = data;
        this.diameter = 1024;
        this.majorTickSpread = Math.PI / 2;
        this.minorTickSpread = Math.PI / 12;
        init();
    }

    private void init() {
        // circle center
        centerX = diameter / 2.0;
        centerY = diameter / 2.0;
        // circle radius
        radius = diameter / 2.0;
    }

    public SVGDocument draw() {
        DOMImplementation domImplementation = SVGDOMImplementation.getDOMImplementation();
        SVGDocument document = (SVGDocument) domImplementation.createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
        SVGGraphics2D graphics = new SVGGraphics2D(document);

        // main circle
        graphics.drawOval(0, 0, (int) diameter, (int) diameter);
        // ticks
        double rminor = 0.95 * radius;
        double rmajor = 0.85 * radius;
        drawTicks(graphics, minorTickSpread, rminor);
        drawTicks(graphics, majorTickSpread, rmajor);

        // observations for every degree on a circle (map key = 0..360)
        Map<Integer, List<Circular>> observations = new TreeMap<>();

        for (Circular circular : data) {
            double degrees = circular.getDegrees();
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

                if (circular instanceof Vector) {
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

                    graphics.drawPolygon(new int[] { (int) x1, (int) x2, (int) x3 }, new int[] { (int) (diameter - y1), (int) (diameter - y2), (int) (diameter - y3) }, 3);
                    i += 2;
                } else if (circular instanceof Axis) {
                    xv -= observationSize / 2.0;
                    yv += observationSize / 2.0;
                    graphics.drawOval((int) xv, (int) (diameter - yv), (int) observationSize, (int) observationSize);
                    xv = centerX + virtualRadius * Math.cos(t + Math.PI) - observationSize / 2.0;
                    yv = centerY + virtualRadius * Math.sin(t + Math.PI) + observationSize / 2.0;
                    graphics.drawOval((int) xv, (int) (diameter - yv), (int) observationSize, (int) observationSize);
                    i += 1;
                }
            }
        }

        SVGSVGElement rootElement = document.getRootElement();
        graphics.getRoot(rootElement);
        Rectangle2D box = SVGHelper.calculateBoundingBox(document);
        rootElement.setAttributeNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "viewBox", box.getX() + " " + box.getY() + " " + box.getWidth() + " " + box.getHeight());
        return document;
    }

    private void drawTicks(SVGGraphics2D graphics, double tickSpread, double virtualRadius) {
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

    public static void main(String[] args) throws IOException, InvalidVectorFormatException, InvalidCircularValueException {
        /*
         * First example
         */
        List<Circular> data = new ArrayList<>();
        List<String> lines = FileUtils.readLines(new File("data/D01"), "UTF-8");

        for (String line : lines) {
            if (line.startsWith("#")) {
                continue;
            }

            for (String token : StringUtils.split(line)) {
                if (!StringUtils.isBlank(token)) {
                    data.add(Vector.fromHourMinuteString(token));
                }
            }
        }

        RawDataPlot plot = new RawDataPlot(data);
        SVGDocument svgDocument = plot.draw();

        try (OutputStream stream = new FileOutputStream("/tmp/D01-plot.svg")) {
            SVGHelper.export(svgDocument, stream, Format.SVG, null);
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
        svgDocument = plot.draw();

        try (OutputStream stream = new FileOutputStream("/tmp/D02-plot.svg")) {
            SVGHelper.export(svgDocument, stream, Format.SVG, null);
        }
    }
}
