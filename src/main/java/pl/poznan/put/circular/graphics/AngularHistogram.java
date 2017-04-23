package pl.poznan.put.circular.graphics;

import org.apache.batik.ext.awt.geom.Polygon2D;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.circular.Circular;
import pl.poznan.put.circular.Histogram;
import pl.poznan.put.circular.enums.AngleTransformation;

import java.util.Collection;

public class AngularHistogram extends RawDataPlot {
    private final double binRadians;
    private double scalingFactor;

    public AngularHistogram(
            final Collection<Circular> data, final double binRadians,
            final double diameter, final double majorTickSpread,
            final double minorTickSpread,
            final AngleTransformation angleTransformation) {
        super(data, diameter, majorTickSpread, minorTickSpread,
              angleTransformation);
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
                                      (d + Math.PI) % MathUtils.TWO_PI,
                                      frequency);
            }
        }

        return finalizeDrawingAndGetSvg();
    }

    private void drawHistogramTriangle(
            final SVGGraphics2D graphics, final double circularValue,
            final double frequency) {
        double sectorRadius =
                FastMath.sqrt(frequency) * getRadius() * scalingFactor;

        // angle as in XY coordinate system
        double t1 = transform(circularValue);
        double x1 = getCenterX() + (sectorRadius * FastMath.cos(t1));
        double y1 = getCenterY() + (sectorRadius * FastMath.sin(t1));

        double t2 = transform(circularValue + binRadians);
        double x2 = getCenterX() + (sectorRadius * FastMath.cos(t2));
        double y2 = getCenterY() + (sectorRadius * FastMath.sin(t2));

        float[] xs = {(float) x1, (float) x2, (float) getCenterX()};
        float[] ys = {
                (float) (getDiameter() - y1), (float) (getDiameter() - y2),
                (float) (getDiameter() - getCenterY())};
        graphics.draw(new Polygon2D(xs, ys, 3));
    }
}
