package pl.poznan.put.circular.graphics;

import java.awt.FontMetrics;
import java.awt.font.LineMetrics;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.svg.SVGDocument;

import pl.poznan.put.circular.Circular;
import pl.poznan.put.circular.Constants;
import pl.poznan.put.circular.Histogram;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.exception.InvalidCircularValueException;
import pl.poznan.put.circular.exception.InvalidVectorFormatException;
import pl.poznan.put.utility.AngleFormat;
import pl.poznan.put.utility.svg.Format;
import pl.poznan.put.utility.svg.SVGHelper;

public class LinearHistogram extends AbstractDrawable {
    private final Collection<Circular> data;
    private final double binRadians;
    private final int drawingUnitSize;

    public LinearHistogram(Collection<Circular> data, double binRadians, int drawingUnitSize) {
        super();
        this.data = data;
        this.binRadians = binRadians;
        this.drawingUnitSize = drawingUnitSize;
    }

    public LinearHistogram(Collection<Circular> data, double binRadians) {
        super();
        this.data = data;
        this.binRadians = binRadians;
        this.drawingUnitSize = 20;
    }

    @Override
    public void draw() throws InvalidCircularValueException {
        Histogram histogram = new Histogram(data, binRadians);
        double maxHeight = Double.NEGATIVE_INFINITY;
        int maxFrequency = Integer.MIN_VALUE;
        int i = 0;

        for (double d = 0; Math.abs(d - 2 * Math.PI) > Constants.EPSILON; d += binRadians, i += 1) {
            int frequency = histogram.getBinSize(d);
            int height = frequency * drawingUnitSize;
            svgGraphics.drawRect(i * drawingUnitSize, -height, drawingUnitSize, height);

            maxFrequency = Math.max(frequency, maxFrequency);
            maxHeight = Math.max(height, maxHeight);
        }
        double maxWidth = i * drawingUnitSize;

        /*
         * X axis lines
         */
        svgGraphics.drawLine(0, drawingUnitSize, (int) maxWidth, drawingUnitSize);
        svgGraphics.drawLine(0, drawingUnitSize, 0, (int) (drawingUnitSize + 0.2 * drawingUnitSize));
        svgGraphics.drawLine((int) maxWidth, drawingUnitSize, (int) maxWidth, (int) (drawingUnitSize + 0.2 * drawingUnitSize));
        /*
         * Y axis lines
         */
        svgGraphics.drawLine(-drawingUnitSize, (int) -maxHeight, -drawingUnitSize, 0);
        svgGraphics.drawLine(-drawingUnitSize, (int) -maxHeight, (int) (-drawingUnitSize - 0.2 * drawingUnitSize), (int) -maxHeight);
        svgGraphics.drawLine(-drawingUnitSize, 0, (int) (-drawingUnitSize - 0.2 * drawingUnitSize), 0);

        LineMetrics lineMetrics = SVGHelper.getLineMetrics(svgGraphics);
        FontMetrics fontMetrics = SVGHelper.getFontMetrics(svgGraphics);
        float fontHeight = lineMetrics.getHeight();

        for (int j = 0; j <= maxFrequency; j++) {
            svgGraphics.drawString(String.valueOf(j), -drawingUnitSize * 2, -j * drawingUnitSize + fontHeight / 6);
        }

        i = 0;
        for (double d = 0; Math.abs(d - 2 * Math.PI) > Constants.EPSILON; d += binRadians, i += 1) {
            String label = AngleFormat.formatDisplayShort(d);
            int labelWidth = fontMetrics.stringWidth(label.substring(0, label.length() - 1));
            svgGraphics.drawString(label, i * drawingUnitSize + (drawingUnitSize / 2) - (labelWidth / 2), drawingUnitSize * 2 + (i % 2 == 0 ? 0 : drawingUnitSize));
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
                    data.add(Angle.fromHourMinuteString(token));
                }
            }
        }

        LinearHistogram histogram = new LinearHistogram(data, Math.toRadians(20));
        histogram.draw();
        SVGDocument svgDocument = histogram.finalizeDrawingAndGetSVG();

        try (OutputStream stream = new FileOutputStream("/tmp/D01-linear-histogram.svg")) {
            SVGHelper.export(svgDocument, stream, Format.SVG, null);
        }
    }
}
