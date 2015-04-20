package pl.poznan.put.circular;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pl.poznan.put.circular.exception.InvalidCircularValueException;

public class Histogram {
    public class Bin {
        private final double radiansStart;
        private final List<Circular> data;

        public Bin(double radiansStart, List<Circular> data) {
            super();
            this.radiansStart = radiansStart;
            this.data = data;
        }
    }

    private final List<Bin> histogram = new ArrayList<>();

    public Histogram(Collection<? extends Circular> data, double binWidth) throws InvalidCircularValueException {
        super();

        if (binWidth < 0 || binWidth >= Math.PI) {
            throw new InvalidCircularValueException("A histogram bin size must be in range [0..180)");
        }

        for (double radiansStart = 0; radiansStart < 2 * Math.PI; radiansStart += binWidth) {
            List<Circular> binData = new ArrayList<>();

            for (Circular circular : data) {
                double radians = circular.getRadians();

                if (radians >= radiansStart && radians < radiansStart + binWidth) {
                    binData.add(circular);
                }
            }

            histogram.add(new Bin(radiansStart, binData));
        }
    }

    public int getBinSize(double radiansStart) {
        return getBin(radiansStart).size();
    }

    private Collection<Circular> getBin(double radiansStart) {
        for (Bin bin : histogram) {
            if (Math.abs(bin.radiansStart - radiansStart) < Constants.EPSILON) {
                return bin.data;
            }
        }

        return Collections.emptyList();
    }
}
