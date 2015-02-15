package pl.poznan.put.circular;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pl.poznan.put.circular.exception.InvalidCircularValueException;

public class Histogram {
    private final Map<Double, List<Circular>> binned = new TreeMap<>();
    private final Map<Circular, Double> datumToBin = new HashMap<>();

    @SuppressWarnings("unused")
    private final Collection<Circular> data;
    @SuppressWarnings("unused")
    private final double binRadians;

    public Histogram(Collection<Circular> data, double binRadians) throws InvalidCircularValueException {
        super();

        if (binRadians < 0 || binRadians >= Math.PI) {
            throw new InvalidCircularValueException("A histogram bin size must be in range [0..180)");
        }

        this.data = data;
        this.binRadians = binRadians;

        for (double d = 0; d < 2 * Math.PI; d += binRadians) {
            for (Circular circular : data) {
                double radians = circular.getRadians();

                if (radians >= d && radians < d + binRadians) {
                    if (!binned.containsKey(d)) {
                        binned.put(d, new ArrayList<Circular>());
                    }
                    binned.get(d).add(circular);
                    datumToBin.put(circular, d);
                }
            }
        }
    }

    public boolean containsBin(double radians) {
        return binned.containsKey(radians);
    }

    public List<Circular> getBin(double radians) {
        return binned.get(radians);
    }

    public Double getContainingBin(Circular circular) {
        return datumToBin.get(circular);
    }
}
