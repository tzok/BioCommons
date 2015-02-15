package pl.poznan.put.circular.graphics;

import java.awt.FontMetrics;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

import pl.poznan.put.circular.Circular;
import pl.poznan.put.circular.Vector;
import pl.poznan.put.circular.exception.InvalidCircularValueException;
import pl.poznan.put.circular.exception.InvalidVectorFormatException;
import pl.poznan.put.utility.AngleFormat;
import pl.poznan.put.utility.svg.Format;
import pl.poznan.put.utility.svg.SVGHelper;

public class LinearHistogram {
    private static final double EPSILON = 1e-3;

    private final Collection<Circular> data;
    private final int drawingUnitSize;

    public LinearHistogram(Collection<Circular> data, int drawingUnitSize) {
        super();
        this.data = data;
        this.drawingUnitSize = drawingUnitSize;
    }

    public LinearHistogram(Collection<Circular> data) {
        super();
        this.data = data;
        this.drawingUnitSize = 20;
    }

    public SVGDocument draw(double binRadians) {
        DOMImplementation domImplementation = SVGDOMImplementation.getDOMImplementation();
        SVGDocument document = (SVGDocument) domImplementation.createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
        SVGGraphics2D graphics = new SVGGraphics2D(document);
        Map<Double, List<Circular>> binned = new TreeMap<>();

        for (double d = 0; d < 2 * Math.PI; d += binRadians) {
            for (Circular circular : data) {
                double radians = circular.getRadians();

                if (radians >= d && radians < d + binRadians) {
                    if (!binned.containsKey(d)) {
                        binned.put(d, new ArrayList<Circular>());
                    }
                    binned.get(d).add(circular);
                }
            }
        }

        double maxHeight = Double.NEGATIVE_INFINITY;
        int maxFrequency = Integer.MIN_VALUE;
        int i = 0;

        for (double d = 0; Math.abs(d - 2 * Math.PI) > LinearHistogram.EPSILON; d += binRadians, i += 1) {
            if (!binned.containsKey(d)) {
                continue;
            }

            List<Circular> observations = binned.get(d);
            int frequency = observations.size();
            int height = frequency * drawingUnitSize;
            graphics.drawRect(i * drawingUnitSize, -height, drawingUnitSize, height);

            maxFrequency = Math.max(frequency, maxFrequency);
            maxHeight = Math.max(height, maxHeight);
        }
        double maxWidth = i * drawingUnitSize;

        /*
         * X axis lines
         */
        graphics.drawLine(0, drawingUnitSize, (int) maxWidth, drawingUnitSize);
        graphics.drawLine(0, drawingUnitSize, 0, (int) (drawingUnitSize + 0.2 * drawingUnitSize));
        graphics.drawLine((int) maxWidth, drawingUnitSize, (int) maxWidth, (int) (drawingUnitSize + 0.2 * drawingUnitSize));
        /*
         * Y axis lines
         */
        graphics.drawLine(-drawingUnitSize, (int) -maxHeight, -drawingUnitSize, 0);
        graphics.drawLine(-drawingUnitSize, (int) -maxHeight, (int) (-drawingUnitSize - 0.2 * drawingUnitSize), (int) -maxHeight);
        graphics.drawLine(-drawingUnitSize, 0, (int) (-drawingUnitSize - 0.2 * drawingUnitSize), 0);

        LineMetrics lineMetrics = SVGHelper.getLineMetrics(graphics);
        FontMetrics fontMetrics = SVGHelper.getFontMetrics(graphics);
        float fontHeight = lineMetrics.getHeight();

        for (int j = 0; j <= maxFrequency; j++) {
            graphics.drawString(String.valueOf(j), -drawingUnitSize * 2, -j * drawingUnitSize + fontHeight / 6);
        }

        i = 0;
        for (double d = 0; Math.abs(d - 2 * Math.PI) > LinearHistogram.EPSILON; d += binRadians, i += 1) {
            String label = AngleFormat.formatDisplayShort(d);
            int labelWidth = fontMetrics.stringWidth(label.substring(0, label.length() - 1));
            graphics.drawString(label, i * drawingUnitSize + (drawingUnitSize / 2) - (labelWidth / 2), drawingUnitSize * 2 + (i % 2 == 0 ? 0 : drawingUnitSize));
        }

        SVGSVGElement rootElement = document.getRootElement();
        graphics.getRoot(rootElement);
        Rectangle2D box = SVGHelper.calculateBoundingBox(document);
        rootElement.setAttributeNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "viewBox", box.getX() + " " + box.getY() + " " + box.getWidth() + " " + box.getHeight());
        return document;
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

        LinearHistogram histogram = new LinearHistogram(data);
        SVGDocument svgDocument = histogram.draw(Math.toRadians(20));

        try (OutputStream stream = new FileOutputStream("/tmp/D01-linear-histogram.svg")) {
            SVGHelper.export(svgDocument, stream, Format.SVG, null);
        }
    }
}
