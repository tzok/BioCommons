package pl.poznan.put.pdb.analysis;

import java.util.Collections;
import java.util.List;

public class PdbChain implements Comparable<PdbChain> {
    private final char identifier;
    private final List<PdbResidue> residues;

    public PdbChain(char identifier, List<PdbResidue> residues) {
        super();
        this.identifier = identifier;
        this.residues = residues;
    }

    public char getIdentifier() {
        return identifier;
    }

    public List<PdbResidue> getResidues() {
        return Collections.unmodifiableList(residues);
    }

    @Override
    public String toString() {
        return String.valueOf(identifier);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + identifier;
        result = prime * result + (residues == null ? 0 : residues.hashCode());
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
        PdbChain other = (PdbChain) obj;
        if (identifier != other.identifier) {
            return false;
        }
        if (residues == null) {
            if (other.residues != null) {
                return false;
            }
        } else if (!residues.equals(other.residues)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(PdbChain o) {
        return identifier < o.identifier ? -1 : identifier == o.identifier ? 0 : 1;
    }

    public String getSequence() {
        StringBuilder builder = new StringBuilder();
        for (PdbResidue residue : residues) {
            builder.append(residue.getOneLetterName());
        }
        return builder.toString();
    }
}
