package pl.poznan.put.pdb;


public class PdbResidueIdentifier implements Comparable<PdbResidueIdentifier> {
    private final String chainIdentifier;
    private final int residueNumber;
    private final String insertionCode;

    public PdbResidueIdentifier(String chainIdentifier, int residueNumber,
                                String insertionCode) {
        super();
        this.chainIdentifier = chainIdentifier;
        this.residueNumber = residueNumber;
        this.insertionCode = insertionCode;
    }

    public static PdbResidueIdentifier fromChainNumberICode(
            ChainNumberICode chainNumberICode) {
        return new PdbResidueIdentifier(chainNumberICode.getChainIdentifier(),
                                        chainNumberICode.getResidueNumber(),
                                        chainNumberICode.getInsertionCode());
    }

    public String getChainIdentifier() {
        return chainIdentifier;
    }

    public int getResidueNumber() {
        return residueNumber;
    }

    public String getInsertionCode() {
        return insertionCode;
    }

    @Override
    public int hashCode() {
        int result = chainIdentifier.hashCode();
        result = 31 * result + residueNumber;
        result = 31 * result + insertionCode.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PdbResidueIdentifier that = (PdbResidueIdentifier) o;
        return residueNumber == that.residueNumber && chainIdentifier
                .equals(that.chainIdentifier) && insertionCode
                       .equals(that.insertionCode);
    }

    @Override
    public String toString() {
        return chainIdentifier + "." + residueNumber + (
                " ".equals(insertionCode) ? "" : insertionCode);
    }

    @Override
    public int compareTo(PdbResidueIdentifier o) {
        if (o == null) {
            throw new NullPointerException();
        }

        if (!chainIdentifier.equals(o.chainIdentifier)) {
            return chainIdentifier.compareTo(o.chainIdentifier);
        }

        if (residueNumber != o.residueNumber) {
            return residueNumber < o.residueNumber ? -1 : 1;
        }

        return insertionCode.compareTo(o.insertionCode);
    }
}
