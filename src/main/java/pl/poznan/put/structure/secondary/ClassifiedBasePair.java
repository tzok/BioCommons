package pl.poznan.put.structure.secondary;

import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.rna.RNAInteractionType;

import java.io.Serializable;
import java.util.Objects;

public class ClassifiedBasePair
        implements Serializable, Comparable<ClassifiedBasePair> {
    private final BasePair basePair;
    private final RNAInteractionType interactionType;
    private final Saenger saenger;
    private final LeontisWesthof leontisWesthof;
    private HelixOrigin helixOrigin;
    private boolean isRepresented;

    public ClassifiedBasePair(
            final BasePair basePair, final RNAInteractionType interactionType,
            final Saenger saenger, final LeontisWesthof leontisWesthof,
            final HelixOrigin helixOrigin) {
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

    public void setHelixOrigin(final HelixOrigin helixOrigin) {
        this.helixOrigin = helixOrigin;
    }

    public boolean isRepresented() {
        return isRepresented;
    }

    public void setRepresented(final boolean isRepresented) {
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

    public boolean isNonCanonical() {
        return !Saenger.isCanonical(saenger) && (
                Objects.equals(interactionType, RNAInteractionType.BASE_BASE)
                || Objects.equals(interactionType,
                                  RNAInteractionType.BASE_BASE_1H));
    }

    public boolean isStacking() {
        return Objects.equals(interactionType, RNAInteractionType.STACKING);
    }

    public boolean isBasePhosphate() {
        return Objects
                .equals(interactionType, RNAInteractionType.BASE_PHOSPHATE);
    }

    public boolean isBaseRibose() {
        return Objects.equals(interactionType, RNAInteractionType.BASE_SUGAR);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((basePair == null) ? 0
                                                        : basePair.hashCode());
        result = (prime * result) + ((interactionType == null) ? 0
                                                               : interactionType
                                             .hashCode());
        result = (prime * result) + ((leontisWesthof == null) ? 0
                                                              : leontisWesthof
                                             .hashCode());
        result =
                (prime * result) + ((saenger == null) ? 0 : saenger.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
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
        } else if (!Objects.equals(basePair, other.basePair)) {
            return false;
        }
        return (interactionType == other.interactionType) && (leontisWesthof
                                                              == other.leontisWesthof)
               && (saenger == other.saenger);
    }

    @Override
    public String toString() {
        return "ClassifiedBasePair [basePair=" + basePair + ", interactionType="
               + interactionType + ", saenger=" + saenger + ", leontisWesthof="
               + leontisWesthof + ", helixOrigin=" + helixOrigin
               + ", isRepresented=" + isRepresented + ']';
    }

    @Override
    public int compareTo(final ClassifiedBasePair t) {
        if (t == null) {
            throw new NullPointerException();
        }

        if (equals(t)) {
            return 0;
        }

        int interactionComparison =
                interactionType.compareTo(t.interactionType);
        if (interactionComparison != 0) {
            return interactionComparison;
        }

        return basePair.compareTo(t.basePair);
    }

    public String generateComment() {
        if ((saenger != Saenger.UNKNOWN) && (leontisWesthof
                                             != LeontisWesthof.UNKNOWN)) {
            return "S:" + saenger + ", LW:" + leontisWesthof;
        }
        if (saenger != Saenger.UNKNOWN) {
            return "S:" + saenger;
        }
        if (leontisWesthof != LeontisWesthof.UNKNOWN) {
            return "LW:" + leontisWesthof;
        }
        return "unknown classification";
    }
}
