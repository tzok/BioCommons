package pl.poznan.put.structure;

import pl.poznan.put.notations.LeontisWesthof;
import pl.poznan.put.notations.Saenger;

public class ClassifiedPair extends BasePair {
    private static final long serialVersionUID = -6590368481589627182L;

    private Saenger saenger;
    private LeontisWesthof leontisWesthof;

    public ClassifiedPair(Residue left, Residue right, Saenger saenger,
            LeontisWesthof leontisWesthof) {
        super(left, right);
        this.saenger = saenger;
        this.leontisWesthof = leontisWesthof;
    }

    public Saenger getSaenger() {
        return saenger;
    }

    public LeontisWesthof getLeontisWesthof() {
        return leontisWesthof;
    }

    @Override
    public String toString() {
        return "ClassifiedBasePair [saenger=" + saenger + ", leontisWesthof="
                + leontisWesthof + ", pair=" + pair + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((leontisWesthof == null) ? 0 : leontisWesthof.hashCode());
        result = prime * result + ((saenger == null) ? 0 : saenger.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ClassifiedPair other = (ClassifiedPair) obj;
        if (leontisWesthof != other.leontisWesthof) {
            return false;
        }
        if (saenger != other.saenger) {
            return false;
        }
        return true;
    }
}
