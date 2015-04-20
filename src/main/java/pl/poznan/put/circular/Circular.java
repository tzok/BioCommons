package pl.poznan.put.circular;

public abstract class Circular implements Comparable<Circular> {
    protected final double radians;

    protected Circular(double radians) {
        super();
        this.radians = radians;
    }

    public double getRadians() {
        return radians;
    }

    public double getDegrees() {
        return Math.toDegrees(radians);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(radians);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Circular other = (Circular) obj;
        if (Double.doubleToLongBits(radians) != Double.doubleToLongBits(other.radians))
            return false;
        return true;
    }

    @Override
    public int compareTo(Circular other) {
        if (equals(other)) {
            return 0;
        }

        return Double.compare(radians, other.radians);
    }

    @Override
    public String toString() {
        return String.valueOf(getRadians()) + " rad\t" + String.valueOf(getDegrees()) + " deg";
    }
}
