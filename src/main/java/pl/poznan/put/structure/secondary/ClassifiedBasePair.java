package pl.poznan.put.structure.secondary;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.rna.RNAInteractionType;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;

public class ClassifiedBasePair
        implements Serializable, Comparable<ClassifiedBasePair> {
    private static final long serialVersionUID = -7311037449944786616L;

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
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        ClassifiedBasePair bp = (ClassifiedBasePair) o;
        return new EqualsBuilder().append(basePair, bp.basePair)
                                  .append(interactionType, bp.interactionType)
                                  .append(saenger, bp.saenger)
                                  .append(leontisWesthof, bp.leontisWesthof)
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(basePair)
                                          .append(interactionType)
                                          .append(saenger)
                                          .append(leontisWesthof).toHashCode();
    }

    @Override
    public String toString() {
        return "ClassifiedBasePair [basePair=" + basePair + ", interactionType="
               + interactionType + ", saenger=" + saenger + ", leontisWesthof="
               + leontisWesthof + ", helixOrigin=" + helixOrigin
               + ", isRepresented=" + isRepresented + ']';
    }

    @Override
    public int compareTo(@Nonnull final ClassifiedBasePair t) {
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

    public ClassifiedBasePair invert() {
        return new ClassifiedBasePair(basePair.invert(),
                                      interactionType.invert(), saenger,
                                      leontisWesthof.invert(), bph, br,
                                      helixOrigin);
    }
}
