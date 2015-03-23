package pl.poznan.put.structure.secondary;

import java.io.Serializable;

import org.apache.commons.lang3.tuple.Pair;

import pl.poznan.put.pdb.analysis.PdbResidue;

public class BasePair implements Serializable, Comparable<BasePair> {
    private Pair<PdbResidue, PdbResidue> pair;

    public BasePair(PdbResidue left, PdbResidue right) {
        super();
        pair = Pair.of(left, right);
    }

    public PdbResidue getLeft() {
        return pair.getLeft();
    }

    public PdbResidue getRight() {
        return pair.getRight();
    }

    public BasePair invert() {
        return new BasePair(pair.getRight(), pair.getLeft());
    }

    @Override
    public String toString() {
        return pair.getLeft() + " - " + pair.getRight();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (pair == null ? 0 : pair.hashCode());
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
        BasePair other = (BasePair) obj;
        if (pair == null) {
            if (other.pair != null) {
                return false;
            }
        } else if (!pair.equals(other.pair)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(BasePair o) {
        if (equals(o)) {
            return 0;
        }

        return pair.compareTo(o.pair);
    }
}
