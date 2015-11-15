package pl.poznan.put.pdb;


public class PdbResidueIdentifier implements Comparable<PdbResidueIdentifier> {
    public static PdbResidueIdentifier fromChainNumberICode(ChainNumberICode chainNumberICode) {
        return new PdbResidueIdentifier(chainNumberICode.getChainIdentifier(), chainNumberICode.getResidueNumber(), chainNumberICode.getInsertionCode());
    }

    private final char chainIdentifier;
    private final int residueNumber;
    private final char insertionCode;

    public PdbResidueIdentifier(char chainIdentifier, int residueNumber, char insertionCode) {
        super();
        this.chainIdentifier = chainIdentifier;
        this.residueNumber = residueNumber;
        this.insertionCode = insertionCode;
    }

    public char getChainIdentifier() {
        return chainIdentifier;
    }

    public int getResidueNumber() {
        return residueNumber;
    }

    public char getInsertionCode() {
        return insertionCode;
    }

    @Override
    public String toString() {
        return chainIdentifier + "." + residueNumber + (insertionCode != ' ' ? insertionCode : "");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + chainIdentifier;
        result = prime * result + insertionCode;
        result = prime * result + residueNumber;
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
        PdbResidueIdentifier other = (PdbResidueIdentifier) obj;
        if (chainIdentifier != other.chainIdentifier) {
            return false;
        }
        return insertionCode == other.insertionCode && residueNumber == other.residueNumber;
    }

    @Override
    public int compareTo(PdbResidueIdentifier o) {
        if (chainIdentifier != o.chainIdentifier) {
            return chainIdentifier < o.chainIdentifier ? -1 : 1;
        }

        if (residueNumber != o.residueNumber) {
            return residueNumber < o.residueNumber ? -1 : 1;
        }

        return insertionCode < o.insertionCode ? -1 : (insertionCode == o.insertionCode ? 0 : 1);
    }
}
