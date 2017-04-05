package pl.poznan.put.circular;

import org.apache.commons.math3.util.MathUtils;

import java.util.Objects;

public abstract class Circular implements Comparable<Circular> {
    private final double radians;

    protected Circular(final double radians) {
        super();
        this.radians = radians;
    }

    public final double getRadians() {
        return radians;
    }

    public final double getDegrees() {
        return Math.toDegrees(radians);
    }

    public final double getDegrees360() {
        return Math.toDegrees(getRadians2PI());
    }

    public final double getRadians2PI() {
        return (radians < 0.0) ? (radians + MathUtils.TWO_PI) : radians;
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        Circular circular = (Circular) o;
        return Double.compare(circular.radians, radians) == 0;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(radians);
    }

    @Override
    public final String toString() {
        return !isValid() ? "invalid"
                          : (radians + " rad\t" + Math.toDegrees(radians)
                             + " deg");
    }

    public final boolean isValid() {
        return !Double.isNaN(radians);
    }

    @Override
    public final int compareTo(final Circular t) {
        if (equals(t)) {
            return 0;
        }
        return Double.compare(radians, t.radians);
    }
}
