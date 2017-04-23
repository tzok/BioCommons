package pl.poznan.put.circular;

import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;
import pl.poznan.put.circular.exception.InvalidCircularValueException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Histogram {
    private final Collection<Bin> histogram = new ArrayList<>();
    private final double binWidth;
    private final double dataSize;

    public Histogram(
            final Collection<? extends Circular> data, final double binWidth) {
        super();
        this.binWidth = binWidth;
        dataSize = data.size();

        if ((binWidth < 0) || (binWidth >= Math.PI)) {
            throw new InvalidCircularValueException(
                    "A histogram bin size must be in range [0..180)");
        }

        for (double radiansStart = 0; radiansStart < MathUtils.TWO_PI;
             radiansStart += binWidth) {
            List<Circular> binData = new ArrayList<>();

            for (final Circular circular : data) {
                double radians = circular.getRadians2PI();
                if ((radians >= radiansStart) && (radians < (radiansStart
                                                             + binWidth))) {
                    binData.add(circular);
                }
            }

            histogram.add(new Bin(radiansStart, binData));
        }
    }

    public int getBinSize(final double radiansStart) {
        return getBin(radiansStart).size();
    }

    private Collection<Circular> getBin(final double radiansStart) {
        for (final Bin bin : histogram) {
            if (Precision.equals(bin.radiansStart, radiansStart, 1.0e-3)) {
                return bin.data;
            }
        }
        return Collections.emptyList();
    }

    public double getMaxFrequency() {
        double maxFrequency = Double.NEGATIVE_INFINITY;
        for (double d = 0; d < MathUtils.TWO_PI; d += binWidth) {
            double frequency = getBinSize(d) / dataSize;
            maxFrequency = Math.max(frequency, maxFrequency);
        }
        return maxFrequency;
    }

    public static class Bin {
        private final double radiansStart;
        private final List<Circular> data;

        public Bin(final double radiansStart, final List<Circular> data) {
            super();
            this.radiansStart = radiansStart;
            this.data = new ArrayList<>(data);
        }
    }
}
