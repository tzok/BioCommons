package pl.poznan.put.structure.secondary;

import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.rna.RNAInteractionType;

import java.io.Serializable;

public class ClassifiedBasePair
        implements Serializable, Comparable<ClassifiedBasePair> {
    private final BasePair basePair;
    private final RNAInteractionType interactionType;
    private final Saenger saenger;
    private final LeontisWesthof leontisWesthof;
    private HelixOrigin helixOrigin;
    private boolean isRepresented;

    public ClassifiedBasePair(BasePair basePair,
                              RNAInteractionType interactionType,
                              Saenger saenger, LeontisWesthof leontisWesthof,
                              HelixOrigin helixOrigin) {
        super();
        this.basePair = basePair;
        this.interactionType = interactionType;
        this.saenger = saenger;
        this.leontisWesthof = leontisWesthof;
        this.helixOrigin = helixOrigin;
    }

    public BasePair getBasePair() {
        return basePair;
    }

    public RNAInteractionType getInteractionType() {
        return interactionType;
    }

    public Saenger getSaenger() {
        return saenger;
    }

    public LeontisWesthof getLeontisWesthof() {
        return leontisWesthof;
    }

    public HelixOrigin getHelixOrigin() {
        return helixOrigin;
    }

    public void setHelixOrigin(HelixOrigin helixOrigin) {
        this.helixOrigin = helixOrigin;
    }

    public boolean isRepresented() {
        return isRepresented;
    }

    public void setRepresented(boolean isRepresented) {
        this.isRepresented = isRepresented;
    }

    // required for Spring to get "isRepresented" field
    public boolean getIsRepresented() {
        return isRepresented;
    }

    public boolean canUseInSecondaryStructure() {
        return interactionType.isPairing();
    }

    public boolean isCanonical() {
        return Saenger.isCanonical(saenger);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (basePair == null ? 0 : basePair.hashCode());
        result = prime * result + (interactionType == null ? 0 : interactionType
                .hashCode());
        result = prime * result + (leontisWesthof == null ? 0 : leontisWesthof
                .hashCode());
        result = prime * result + (saenger == null ? 0 : saenger.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ClassifiedBasePair other = (ClassifiedBasePair) obj;
        if (basePair == null) {
            if (other.basePair != null) {
                return false;
            }
        } else if (!basePair.equals(other.basePair)) {
            return false;
        }
        return interactionType == other.interactionType
               && leontisWesthof == other.leontisWesthof
               && saenger == other.saenger;
    }

    @Override
    public String toString() {
        return "ClassifiedBasePair [basePair=" + basePair + ", interactionType="
               + interactionType + ", saenger=" + saenger + ", leontisWesthof="
               + leontisWesthof + ", helixOrigin=" + helixOrigin
               + ", isRepresented=" + isRepresented + "]";
    }

    @Override
    public int compareTo(ClassifiedBasePair o) {
        if (o == null) {
            throw new NullPointerException();
        }

        if (equals(o)) {
            return 0;
        }

        int interactionComparison =
                interactionType.compareTo(o.interactionType);
        if (interactionComparison != 0) {
            return interactionComparison;
        }

        return basePair.compareTo(o.basePair);
    }

    public String generateComment() {
        if (saenger != Saenger.UNKNOWN
            && leontisWesthof != LeontisWesthof.UNKNOWN) {
            return "S:" + saenger + ", LW:" + leontisWesthof;
        } else if (saenger != Saenger.UNKNOWN) {
            return "S:" + saenger;
        } else if (leontisWesthof != LeontisWesthof.UNKNOWN) {
            return "LW:" + leontisWesthof;
        }
        return "unknown classification";
    }
}
