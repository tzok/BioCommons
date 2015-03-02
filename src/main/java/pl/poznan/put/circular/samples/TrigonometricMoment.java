package pl.poznan.put.circular.samples;

import pl.poznan.put.circular.Vector;

public class TrigonometricMoment {
    private final Vector meanDirection;
    private final double meanResultantLength;

    public TrigonometricMoment(Vector meanDirection, double meanResultantLength) {
        super();
        this.meanDirection = meanDirection;
        this.meanResultantLength = meanResultantLength;
    }

    public Vector getMeanDirection() {
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
