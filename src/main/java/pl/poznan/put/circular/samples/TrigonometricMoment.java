package pl.poznan.put.circular.samples;

import pl.poznan.put.circular.Angle;

public class TrigonometricMoment {
    private final Angle meanDirection;
    private final double meanResultantLength;

    public TrigonometricMoment(Angle meanDirection, double meanResultantLength) {
        super();
        this.meanDirection = meanDirection;
        this.meanResultantLength = meanResultantLength;
    }

    public Angle getMeanDirection() {
        return meanDirection;
    }

    public double getMeanResultantLength() {
        return meanResultantLength;
    }

    @Override
    public String toString() {
        return "TrigonometricMoment [meanDirection=" + meanDirection + ", meanResultantLength=" + meanResultantLength + "]";
    }
}
