package pl.poznan.put.pdb;


public class PdbResidueIdentifier implements Comparable<PdbResidueIdentifier> {
    public static PdbResidueIdentifier fromChainNumberICode(ChainNumberICode chainNumberICode) {
        return new PdbResidueIdentifier(chainNumberICode.getChainIdentifier(), chainNumberICode.getResidueNumber(), chainNumberICode.getInsertionCode());
    }

    private final String chainIdentifier;
    private final int residueNumber;
    private final String insertionCode;

    public PdbResidueIdentifier(String chainIdentifier, int residueNumber, String insertionCode) {
        super();
        this.chainIdentifier = chainIdentifier;
        this.residueNumber = residueNumber;
        this.insertionCode = insertionCode;
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
    public String toString() {
        return chainIdentifier + "." + residueNumber + (" ".equals(insertionCode) ? "" : insertionCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PdbResidueIdentifier that = (PdbResidueIdentifier) o;

        if (residueNumber != that.residueNumber) return false;
        if (!chainIdentifier.equals(that.chainIdentifier)) return false;
        return insertionCode.equals(that.insertionCode);

    }

    @Override
    public int hashCode() {
        int result = chainIdentifier.hashCode();
        result = 31 * result + residueNumber;
        result = 31 * result + insertionCode.hashCode();
        return result;
    }

    @Override
    public int compareTo(PdbResidueIdentifier o) {
        if (!chainIdentifier.equals(o.chainIdentifier)) {
            return chainIdentifier.compareTo(chainIdentifier);
        }

        if (residueNumber != o.residueNumber) {
            return residueNumber < o.residueNumber ? -1 : 1;
        }

        return insertionCode.compareTo(o.insertionCode);
    }
}
