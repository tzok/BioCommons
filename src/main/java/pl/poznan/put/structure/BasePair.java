package pl.poznan.put.structure;

import java.io.Serializable;

import org.apache.commons.lang3.tuple.Pair;

public class BasePair implements Serializable {
    private static final long serialVersionUID = 2202206332317872401L;

    protected Pair<Residue, Residue> pair;

    public BasePair(Residue left, Residue right) {
        super();
        pair = Pair.of(left, right);
    }

    public Residue getLeft() {
        return pair.getLeft();
    }

    public Residue getRight() {
        return pair.getRight();
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
}
