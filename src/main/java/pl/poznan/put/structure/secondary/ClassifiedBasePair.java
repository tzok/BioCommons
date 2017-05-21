package pl.poznan.put.structure.secondary;

import pl.poznan.put.notation.BPh;
import pl.poznan.put.notation.BR;
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
    private final BPh bph;
    private final BR br;
    private HelixOrigin helixOrigin;
    private boolean isRepresented;

    public ClassifiedBasePair(
            final BasePair basePair, final RNAInteractionType interactionType,
            final Saenger saenger, final LeontisWesthof leontisWesthof,
            final BPh bph, final BR br, final HelixOrigin helixOrigin) {
        super();
        this.basePair = basePair;
        this.interactionType = interactionType;
        this.saenger = saenger;
        this.leontisWesthof = leontisWesthof;
        this.bph = bph;
        this.br = br;
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

    public BPh getBph() {
        return bph;
    }

    public BR getBr() {
        return br;
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
        return Objects.equals(interactionType, RNAInteractionType.BASE_RIBOSE);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        final ClassifiedBasePair basePair1 = (ClassifiedBasePair) o;
        return Objects.equals(basePair, basePair1.basePair) && Objects
                .equals(interactionType, basePair1.interactionType) && (saenger
                                                                        ==
                                                                        basePair1.saenger)
               && (leontisWesthof == basePair1.leontisWesthof) && (bph
                                                                   ==
                                                                   basePair1.bph)
               && (br == basePair1.br);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(basePair, interactionType, saenger, leontisWesthof, bph,
                      br);
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

        final int interactionComparison =
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
