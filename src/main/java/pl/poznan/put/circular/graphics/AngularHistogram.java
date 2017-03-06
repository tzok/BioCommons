package pl.poznan.put.circular.graphics;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.Circular;
import pl.poznan.put.circular.Constants;
import pl.poznan.put.circular.Histogram;
import pl.poznan.put.circular.exception.InvalidCircularOperationException;
import pl.poznan.put.circular.exception.InvalidCircularValueException;
import pl.poznan.put.circular.exception.InvalidVectorFormatException;
import pl.poznan.put.utility.svg.Format;
import pl.poznan.put.utility.svg.SVGHelper;

import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AngularHistogram extends RawDataPlot {
    private final double binRadians;
    private double scalingFactor;

    public AngularHistogram(
            Collection<Circular> data, double binRadians, double diameter,
            double majorTickSpread, double minorTickSpread)
            throws InvalidCircularOperationException {
        super(data, diameter, majorTickSpread, minorTickSpread);
        this.binRadians = binRadians;
    }

    public AngularHistogram(
            Collection<Circular> data, double binRadians, double diameter)
            throws InvalidCircularOperationException {
        super(data, diameter);
        this.binRadians = binRadians;
    }

    public AngularHistogram(Collection<Circular> data, double binRadians)
            throws InvalidCircularOperationException {
        super(data);
        this.binRadians = binRadians;
    }

    public AngularHistogram(Collection<? extends Circular> data)
            throws InvalidCircularOperationException {
        super(data);
        this.binRadians = Math.PI / 12;
    }

    public static void main(String[] args)
            throws IOException, InvalidVectorFormatException,
                   InvalidCircularValueException,
                   InvalidCircularOperationException {
        if (args.length != 1) {
            System.err.println("Usage: angular-histogram <FILE>");
            return;
        }

        List<Circular> data = new ArrayList<Circular>();
        List<String> lines = FileUtils.readLines(new File(args[0]), "UTF-8");

        for (String line : lines) {
            if (!StringUtils.isBlank(line)) {
                double degrees = Double.parseDouble(line.trim());
                data.add(new Angle(Math.toRadians(degrees)));
            }
        }

        AngularHistogram plot = new AngularHistogram(data);
        plot.draw();
        SVGDocument svgDocument = plot.finalizeDrawingAndGetSVG();
        System.out.println(new String(SVGHelper.export(svgDocument, Format.SVG),
                                      Charset.defaultCharset()));
    }

    @Override
    public void draw() throws InvalidCircularValueException {
        super.draw();

        Histogram histogram = new Histogram(data, binRadians);
        double maxFrequency = Double.NEGATIVE_INFINITY;

        for (double d = 0; Math.abs(d - 2 * Math.PI) > Constants.EPSILON;
             d += binRadians) {
            double frequency =
                    (double) histogram.getBinSize(d) / (double) data.size();
            maxFrequency = Math.max(frequency, maxFrequency);
        }

        // the 0.8 is here because up to 0.85 the majorTick can be drawn and we
        // do not want overlaps
        scalingFactor = 0.8 / Math.sqrt(maxFrequency);

        for (double d = 0; Math.abs(d - 2 * Math.PI) > Constants.EPSILON;
             d += binRadians) {
            double frequency =
                    (double) histogram.getBinSize(d) / (double) data.size();
            drawHistogramTriangle(svgGraphics, d, frequency);

            if (isAxes) {
                drawHistogramTriangle(svgGraphics,
                                      (d + Math.PI) % (2 * Math.PI), frequency);
            }
        }
    }

    private void drawHistogramTriangle(
            Graphics graphics, double circularValue, double frequency) {
        double sectorRadius = Math.sqrt(frequency) * radius * scalingFactor;

        // angle as in XY coordinate system
        double t = -(circularValue + Math.PI * 3 / 2) % (2 * Math.PI);
        double x1 = centerX + sectorRadius * Math.cos(t);
        double y1 = centerY + sectorRadius * Math.sin(t);
        t = -(circularValue + binRadians + Math.PI * 3 / 2) % (2 * Math.PI);
        double x2 = centerX + sectorRadius * Math.cos(t);
        double y2 = centerY + sectorRadius * Math.sin(t);

        graphics.drawPolygon(new int[]{(int) x1, (int) x2, (int) centerX},
                             new int[]{
                                     (int) (diameter - y1),
                                     (int) (diameter - y2),
                                     (int) (diameter - centerY)}, 3);
    }
}
