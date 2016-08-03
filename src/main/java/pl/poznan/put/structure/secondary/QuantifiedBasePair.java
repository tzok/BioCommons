package pl.poznan.put.structure.secondary;

import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.rna.RNAInteractionType;

public class QuantifiedBasePair extends ClassifiedBasePair {
    private final double shear;
    private final double stretch;
    private final double stagger;
    private final double buckle;
    private final double propeller;
    private final double opening;

    public QuantifiedBasePair(BasePair basePair, Saenger saenger, LeontisWesthof leontisWesthof, double shear, double stretch, double stagger, double buckle, double propeller, double opening) {
        super(basePair, RNAInteractionType.BASE_BASE, saenger, leontisWesthof, HelixOrigin.UNKNOWN);
        this.shear = shear;
        this.stretch = stretch;
        this.stagger = stagger;
        this.buckle = buckle;
        this.propeller = propeller;
        this.opening = opening;
    }

    public double getShear() {
        return shear;
    }

    public double getStretch() {
        return stretch;
    }

    public double getStagger() {
        return stagger;
    }

    public double getBuckle() {
        return buckle;
    }

    public double getPropeller() {
        return propeller;
    }

    public double getOpening() {
        return opening;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        QuantifiedBasePair that = (QuantifiedBasePair) o;

        if (Double.compare(that.shear, shear) != 0) return false;
        if (Double.compare(that.stretch, stretch) != 0) return false;
        if (Double.compare(that.stagger, stagger) != 0) return false;
        if (Double.compare(that.buckle, buckle) != 0) return false;
        if (Double.compare(that.propeller, propeller) != 0) return false;
        return Double.compare(that.opening, opening) == 0;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(shear);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(stretch);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(stagger);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(buckle);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(propeller);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(opening);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "QuantifiedBasePair{" +
                "basePair=" + getBasePair() +
                ", saenger=" + getSaenger() +
                ", leontisWesthof=" + getLeontisWesthof() +
                ", shear=" + shear +
                ", stretch=" + stretch +
                ", stagger=" + stagger +
                ", buckle=" + buckle +
                ", propeller=" + propeller +
                ", opening=" + opening +
                '}';
    }
}
