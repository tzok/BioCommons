package pl.poznan.put.pdb;


import javax.annotation.Nonnull;
import java.io.Serializable;

public class PdbResidueIdentifier
        implements Comparable<PdbResidueIdentifier>, Serializable {
    private static final long serialVersionUID = -573135765487167710L;

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
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        PdbResidueIdentifier other = (PdbResidueIdentifier) obj;
        return (residueNumber == other.residueNumber) && chainIdentifier
                .equals(other.chainIdentifier) && insertionCode
                       .equals(other.insertionCode);
    }

    @Override
    public final String toString() {
        String icode = " ".equals(insertionCode) ? "" : insertionCode;
        return chainIdentifier + '.' + residueNumber + icode;
    }

    @Override
    public final int compareTo(@Nonnull final PdbResidueIdentifier t) {
        if (!chainIdentifier.equals(t.chainIdentifier)) {
            return chainIdentifier.compareTo(t.chainIdentifier);
        }

        if (residueNumber != t.residueNumber) {
            return (residueNumber < t.residueNumber) ? -1 : 1;
        }

        return insertionCode.compareTo(t.insertionCode);
    }
}
