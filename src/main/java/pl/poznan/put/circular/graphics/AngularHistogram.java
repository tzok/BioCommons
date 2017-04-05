package pl.poznan.put.circular.graphics;

import org.apache.commons.math3.util.MathUtils;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.circular.Circular;
import pl.poznan.put.circular.Histogram;

import java.awt.Graphics;
import java.util.Collection;

public class AngularHistogram extends RawDataPlot {
    private final double binRadians;
    private double scalingFactor;

    public AngularHistogram(
            final Collection<Circular> data, final double binRadians,
            final double diameter, final double majorTickSpread,
            final double minorTickSpread) {
        super(data, diameter, majorTickSpread, minorTickSpread);
        this.binRadians = binRadians;
    }

    public AngularHistogram(
            final Collection<Circular> data, final double binRadians,
            final double diameter) {
        super(data, diameter);
        this.binRadians = binRadians;
    }

    public AngularHistogram(
            final Collection<Circular> data, final double binRadians) {
        super(data);
        this.binRadians = binRadians;
    }

    public AngularHistogram(final Collection<? extends Circular> data) {
        super(data);
        binRadians = Math.PI / 12;
    }

    @Override
    public final SVGDocument draw() {
        super.draw();

        Histogram histogram = new Histogram(getData(), binRadians);
        double maxFrequency = Double.NEGATIVE_INFINITY;

        for (double d = 0; d < MathUtils.TWO_PI; d += binRadians) {
            double frequency =
                    (double) histogram.getBinSize(d) / getData().size();
            maxFrequency = Math.max(frequency, maxFrequency);
        }

        // the 0.8 is here because up to 0.85 the majorTick can be drawn and we
        // do not want overlaps
        scalingFactor = 0.8 / Math.sqrt(maxFrequency);

        for (double d = 0; d < MathUtils.TWO_PI; d += binRadians) {
            double frequency =
                    (double) histogram.getBinSize(d) / getData().size();
            drawHistogramTriangle(getSvgGraphics(), d, frequency);

            if (isAxes()) {
                drawHistogramTriangle(getSvgGraphics(),
                                      (d + Math.PI) % (2 * Math.PI), frequency);
            }
        }

        return finalizeDrawingAndGetSvg();
    }

    private void drawHistogramTriangle(
            final Graphics graphics, final double circularValue,
            final double frequency) {
        double sectorRadius =
                Math.sqrt(frequency) * getRadius() * scalingFactor;

        // angle as in XY coordinate system
        double t = -(circularValue + ((Math.PI * 3) / 2)) % (2 * Math.PI);
        double x1 = getCenterX() + (sectorRadius * StrictMath.cos(t));
        double y1 = getCenterY() + (sectorRadius * StrictMath.sin(t));
        t = -(circularValue + binRadians + ((Math.PI * 3) / 2)) % (2 * Math.PI);
        double x2 = getCenterX() + (sectorRadius * StrictMath.cos(t));
        double y2 = getCenterY() + (sectorRadius * StrictMath.sin(t));

        graphics.drawPolygon(new int[]{(int) x1, (int) x2, (int) getCenterX()},
                             new int[]{
                                     (int) (getDiameter() - y1),
                                     (int) (getDiameter() - y2),
                                     (int) (getDiameter() - getCenterY())}, 3);
    }
}
