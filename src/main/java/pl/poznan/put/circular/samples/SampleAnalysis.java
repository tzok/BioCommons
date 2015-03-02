package pl.poznan.put.circular.samples;

import java.util.Collection;

import pl.poznan.put.circular.Vector;
import pl.poznan.put.circular.exception.InvalidCircularValueException;

public class SampleAnalysis {
    private final Collection<Vector> data;
    private final Vector meanDirection;
    private final double meanResultantLength;
    private final double circularVariance;
    private final double circularStandardDeviation;
    private final double circularDispersion;
    private final double skewness;
    private final double kurtosis;

    public SampleAnalysis(Collection<Vector> data) throws InvalidCircularValueException {
        super();
        this.data = data;

        double c = 0;
        double s = 0;

        for (Vector vector : data) {
            double radians = vector.getRadians();
            c += Math.cos(radians);
            s += Math.sin(radians);
        }

        double r = Math.sqrt(Math.pow(c, 2) + Math.pow(s, 2));
        double mi;

        if (s > 0 && c > 0) {
            mi = Math.atan(s / c);
        } else if (c < 0) {
            mi = Math.atan(s / c) + Math.PI;
        } else {
            // s < 0 && c > 0
            mi = Math.atan(s / c) + 2 * Math.PI;
        }

        meanDirection = new Vector(mi);
        meanResultantLength = r / data.size();
        circularVariance = 1 - meanResultantLength;
        circularStandardDeviation = Math.sqrt(-2 * Math.log(meanResultantLength));

        TrigonometricMoment cm2 = getCenteredMoment(2);
        TrigonometricMoment um2 = getUncenteredMoment(2);
        circularDispersion = (1.0 - cm2.getMeanResultantLength()) / (2 * Math.pow(meanResultantLength, 2));
        skewness = cm2.getMeanResultantLength() * Math.sin(Vector.subtract(cm2.getMeanDirection(), meanDirection.multiply(2)).getRadians()) / Math.sqrt(circularVariance);
        kurtosis = (cm2.getMeanResultantLength() * Math.cos(Vector.subtract(um2.getMeanDirection(), meanDirection.multiply(2)).getRadians()) - Math.pow(meanResultantLength, 4)) / Math.pow(circularVariance, 2);
    }

    public Vector getMeanDirection() {
        return meanDirection;
    }

    public double getMeanResultantLength() {
        return meanResultantLength;
    }

    public double getCircularVariance() {
        return circularVariance;
    }

    public double getCircularStandardDeviation() {
        return circularStandardDeviation;
    }

    public double getCircularDispersion() {
        return circularDispersion;
    }

    public double getSkewness() {
        return skewness;
    }

    public double getKurtosis() {
        return kurtosis;
    }

    public TrigonometricMoment getUncenteredMoment(int p) throws InvalidCircularValueException {
        return getMoment(p, false);
    }

    public TrigonometricMoment getCenteredMoment(int p) throws InvalidCircularValueException {
        return getMoment(p, true);
    }

    private TrigonometricMoment getMoment(int p, boolean isCentered) throws InvalidCircularValueException {
        double c = 0;
        double s = 0;

        for (Vector vector : data) {
            double radians = vector.getRadians();

            if (isCentered) {
                radians = Vector.subtract(radians, meanDirection.getRadians());
            }

            c += Math.cos(p * radians);
            s += Math.sin(p * radians);
        }

        c /= data.size();
        s /= data.size();

        double rho = Math.sqrt(Math.pow(c, 2) + Math.pow(s, 2));
        double mi;

        if (s > 0 && c > 0) {
            mi = Math.atan(s / c);
        } else if (c < 0) {
            mi = Math.atan(s / c) + Math.PI;
        } else {
            // s < 0 && c > 0
            mi = Math.atan(s / c) + 2 * Math.PI;
        }

        return new TrigonometricMoment(new Vector(mi), rho);
    }

}
