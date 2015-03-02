package pl.poznan.put.circular.graphics;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

import pl.poznan.put.circular.Axis;
import pl.poznan.put.circular.Circular;
import pl.poznan.put.circular.Constants;
import pl.poznan.put.circular.Histogram;
import pl.poznan.put.circular.Vector;
import pl.poznan.put.circular.exception.InvalidCircularOperationException;
import pl.poznan.put.circular.exception.InvalidCircularValueException;
import pl.poznan.put.circular.exception.InvalidVectorFormatException;
import pl.poznan.put.utility.svg.Format;
import pl.poznan.put.utility.svg.SVGHelper;

public class AngularHistogram extends RawDataPlot {
    private final double binRadians;
    private double scalingFactor;

    public AngularHistogram(Collection<Circular> data, double binRadians, double diameter, double majorTickSpread, double minorTickSpread) throws InvalidCircularOperationException {
        super(data, diameter, majorTickSpread, minorTickSpread);
        this.binRadians = binRadians;
    }

    public AngularHistogram(Collection<Circular> data, double binRadians, double diameter) throws InvalidCircularOperationException {
        super(data, diameter);
        this.binRadians = binRadians;
    }

    public AngularHistogram(Collection<Circular> data, double binRadians) throws InvalidCircularOperationException {
        super(data);
        this.binRadians = binRadians;
    }

    public AngularHistogram(Collection<Circular> data) throws InvalidCircularOperationException {
        super(data);
        this.binRadians = Math.PI / 12;
    }

    @Override
    public SVGDocument draw() throws InvalidCircularValueException {
        DOMImplementation domImplementation = SVGDOMImplementation.getDOMImplementation();
        SVGDocument document = (SVGDocument) domImplementation.createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
        SVGGraphics2D graphics = drawBareImage(document);
        Histogram histogram = new Histogram(data, binRadians);
        double maxFrequency = Double.NEGATIVE_INFINITY;

        for (double d = 0; Math.abs(d - 2 * Math.PI) > Constants.EPSILON; d += binRadians) {
            if (!histogram.containsBin(d)) {
                continue;
            }
            double frequency = (double) histogram.getBin(d).size() / (double) data.size();
            maxFrequency = Math.max(frequency, maxFrequency);
        }

        // the 0.8 is here because up to 0.85 the majorTick can be drawn and we
        // do not want overlaps
        scalingFactor = 0.8 / Math.sqrt(maxFrequency);

        for (double d = 0; Math.abs(d - 2 * Math.PI) > Constants.EPSILON; d += binRadians) {
            if (!histogram.containsBin(d)) {
                continue;
            }

            double frequency = (double) histogram.getBin(d).size() / (double) data.size();
            drawHistogramTriangle(graphics, d, frequency);

            if (isAxes) {
                drawHistogramTriangle(graphics, (d + Math.PI) % (2 * Math.PI), frequency);
            }
        }

        SVGSVGElement rootElement = document.getRootElement();
        graphics.getRoot(rootElement);
        Rectangle2D box = SVGHelper.calculateBoundingBox(document);
        rootElement.setAttributeNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "viewBox", box.getX() + " " + box.getY() + " " + box.getWidth() + " " + box.getHeight());
        return document;
    }

    private void drawHistogramTriangle(Graphics graphics, double circularValue, double frequency) {
        double sectorRadius = Math.sqrt(frequency) * radius * scalingFactor;

        // angle as in XY coordinate system
        double t = -(circularValue + Math.PI * 3 / 2) % (2 * Math.PI);
        double x1 = centerX + sectorRadius * Math.cos(t);
        double y1 = centerY + sectorRadius * Math.sin(t);
        t = -(circularValue + binRadians + Math.PI * 3 / 2) % (2 * Math.PI);
        double x2 = centerX + sectorRadius * Math.cos(t);
        double y2 = centerY + sectorRadius * Math.sin(t);

        graphics.drawPolygon(new int[] { (int) x1, (int) x2, (int) centerX }, new int[] { (int) (diameter - y1), (int) (diameter - y2), (int) (diameter - centerY) }, 3);
    }

    public static void main(String[] args) throws IOException, InvalidVectorFormatException, InvalidCircularValueException, InvalidCircularOperationException {
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

        Drawable plot = new AngularHistogram(data);
        SVGDocument svgDocument = plot.draw();

        try (OutputStream stream = new FileOutputStream("/tmp/D01-angular-histogram.svg")) {
            SVGHelper.export(svgDocument, stream, Format.SVG, null);
        }

        /*
         * Second example
         */
        data = new ArrayList<>();
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

        plot = new AngularHistogram(data);
        svgDocument = plot.draw();

        try (OutputStream stream = new FileOutputStream("/tmp/D02-angular-histogram.svg")) {
            SVGHelper.export(svgDocument, stream, Format.SVG, null);
        }

    }
}
