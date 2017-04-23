package pl.poznan.put.circular.graphics;

import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.util.MathUtils;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.circular.Circular;
import pl.poznan.put.circular.Histogram;
import pl.poznan.put.circular.utility.Helper;
import pl.poznan.put.utility.AngleFormat;
import pl.poznan.put.utility.svg.Format;
import pl.poznan.put.utility.svg.SVGHelper;

import java.awt.FontMetrics;
import java.awt.font.LineMetrics;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LinearHistogram extends AbstractDrawable {
    public static void main(final String[] args)
            throws IOException, FileNotFoundException {
        String resource = Helper.readResource("example/D01");
        List<Circular> data = Helper.loadHourMinuteData(resource);

        LinearHistogram histogram =
                new LinearHistogram(data, Math.toRadians(20));
        histogram.draw();
        SVGDocument svgDocument = histogram.finalizeDrawing();
        OutputStream stream = null;

        try {
            stream = new FileOutputStream("/tmp/D01-linear-histogram.svg");
            IOUtils.write(SVGHelper.export(svgDocument, Format.SVG), stream);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    private final Collection<Circular> data;
    private final double binRadians;
    private final int drawingUnitSize;

    public LinearHistogram(
            final Collection<Circular> data, final double binRadians,
            final int drawingUnitSize) {
        super();
        this.data = new ArrayList<>(data);
        this.binRadians = binRadians;
        this.drawingUnitSize = drawingUnitSize;
    }

    public LinearHistogram(
            final Collection<Circular> data, final double binRadians) {
        super();
        this.data = new ArrayList<>(data);
        this.binRadians = binRadians;
        drawingUnitSize = 20;
    }

    @Override
    public final void draw() {
        Histogram histogram = new Histogram(data, binRadians);
        double maxHeight = Double.NEGATIVE_INFINITY;
        int maxFrequency = Integer.MIN_VALUE;
        int i = 0;

        for (double d = 0; d < MathUtils.TWO_PI; d += binRadians, i += 1) {
            int frequency = histogram.getBinSize(d);
            int height = frequency * drawingUnitSize;
            svgGraphics.drawRect(i * drawingUnitSize, -height, drawingUnitSize,
                                 height);

            maxFrequency = Math.max(frequency, maxFrequency);
            maxHeight = Math.max(height, maxHeight);
        }
        double maxWidth = i * drawingUnitSize;

        /*
         * X axis lines
         */
        svgGraphics
                .drawLine(0, drawingUnitSize, (int) maxWidth, drawingUnitSize);
        svgGraphics.drawLine(0, drawingUnitSize, 0,
                             (int) (drawingUnitSize + (0.2 * drawingUnitSize)));
        svgGraphics.drawLine((int) maxWidth, drawingUnitSize, (int) maxWidth,
                             (int) (drawingUnitSize + (0.2 * drawingUnitSize)));
        /*
         * Y axis lines
         */
        svgGraphics
                .drawLine(-drawingUnitSize, (int) -maxHeight, -drawingUnitSize,
                          0);
        svgGraphics.drawLine(-drawingUnitSize, (int) -maxHeight,
                             (int) (-drawingUnitSize - (0.2 * drawingUnitSize)),
                             (int) -maxHeight);
        svgGraphics.drawLine(-drawingUnitSize, 0,
                             (int) (-drawingUnitSize - (0.2 * drawingUnitSize)),
                             0);

        LineMetrics lineMetrics = SVGHelper.getLineMetrics(svgGraphics);
        FontMetrics fontMetrics = SVGHelper.getFontMetrics(svgGraphics);
        float fontHeight = lineMetrics.getHeight();

        for (int j = 0; j <= maxFrequency; j++) {
            svgGraphics.drawString(String.valueOf(j), -drawingUnitSize << 1,
                                   (-j * drawingUnitSize) + (fontHeight / 6));
        }

        i = 0;
        for (double d = 0; d < MathUtils.TWO_PI; d += binRadians, i += 1) {
            String label = AngleFormat.formatDisplayShort(d);
            int labelWidth = fontMetrics
                    .stringWidth(label.substring(0, label.length() - 1));
            svgGraphics.drawString(label,
                                   ((i * drawingUnitSize) + (drawingUnitSize
                                                             / 2)) - (labelWidth
                                                                      / 2),
                                   (drawingUnitSize << 1) + (((i % 2) == 0) ? 0
                                                                            :
                                                             drawingUnitSize));
        }
    }
}
