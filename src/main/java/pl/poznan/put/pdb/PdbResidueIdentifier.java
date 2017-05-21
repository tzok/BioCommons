package pl.poznan.put.pdb;


import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;

public class PdbResidueIdentifier
        implements Comparable<PdbResidueIdentifier>, Serializable {
    private static final long serialVersionUID = -573135765487167710L;

    private static final PdbResidueIdentifier INVALID =
            new PdbResidueIdentifier("", Integer.MIN_VALUE, "");


    private final String chainIdentifier;
    private final int residueNumber;
    private final String insertionCode;

    public PdbResidueIdentifier(
            final String chainIdentifier, final int residueNumber,
            final String insertionCode) {
        super();
        this.chainIdentifier = chainIdentifier;
        this.residueNumber = residueNumber;
        this.insertionCode = insertionCode;
    }

    public static PdbResidueIdentifier fromChainNumberICode(
            final ChainNumberICode chainNumberICode) {
        return new PdbResidueIdentifier(chainNumberICode.getChainIdentifier(),
                                        chainNumberICode.getResidueNumber(),
                                        chainNumberICode.getInsertionCode());
    }

    public static PdbResidueIdentifier invalid() {
        return PdbResidueIdentifier.INVALID;
    }

    public final String getChainIdentifier() {
        return chainIdentifier;
    }

    public final int getResidueNumber() {
        return residueNumber;
    }

    public final String getInsertionCode() {
        return insertionCode;
    }

    @Override
    public final int hashCode() {
        int result = chainIdentifier.hashCode();
        result = (31 * result) + residueNumber;
        result = (31 * result) + insertionCode.hashCode();
        return result;
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        final PdbResidueIdentifier other = (PdbResidueIdentifier) o;
        return (residueNumber == other.residueNumber) && Objects
                .equals(chainIdentifier, other.chainIdentifier) && Objects
                       .equals(insertionCode, other.insertionCode);
    }

    @Override
    public final String toString() {
        final String icode = Objects.equals(" ", insertionCode) ? "" : insertionCode;
        return chainIdentifier + '.' + residueNumber + icode;
    }

    @Override
    public final int compareTo(@Nonnull final PdbResidueIdentifier t) {
        if (!Objects.equals(chainIdentifier, t.chainIdentifier)) {
            return chainIdentifier.compareTo(t.chainIdentifier);
        }

        if (residueNumber != t.residueNumber) {
            return (residueNumber < t.residueNumber) ? -1 : 1;
        }

        return insertionCode.compareTo(t.insertionCode);
    }

    public PdbResidueIdentifier replaceChainIdentifier(final String chain) {
        return new PdbResidueIdentifier(chain, residueNumber, insertionCode);
    }
}
