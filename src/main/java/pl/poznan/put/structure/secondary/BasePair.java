package pl.poznan.put.structure.secondary;

import org.apache.commons.lang3.tuple.Pair;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.pdb.analysis.PdbResidue;

import java.io.Serializable;

public class BasePair implements Serializable, Comparable<BasePair> {
    private static final double GU_DISTANCE_O6_N3 = 2.83 + 0.13 * 3;
    private static final double GU_DISTANCE_N1_O2 = 2.79 + 0.13 * 3;
    private static final double AU_DISTANCE_N6_O4 = 3.00 + 0.17 * 3;
    private static final double AU_DISTANCE_N1_N3 = 2.84 + 0.12 * 3;
    private static final double CG_DISTANCE_N4_O6 = 2.96 + 0.17 * 3;
    private static final double CG_DISTANCE_O2_N2 = 2.77 + 0.15 * 3;
    private static final double CG_DISTANCE_N3_N1 = 2.89 + 0.11 * 3;

    public static boolean isCanonicalPair(PdbResidue left, PdbResidue right) {
        char leftName = Character.toUpperCase(left.getOneLetterName());
        char rightName = Character.toUpperCase(right.getOneLetterName());

        if (leftName > rightName) {
            return BasePair.isCanonicalPair(right, left);
        }

        if (leftName == 'C' && rightName == 'G') {
            return BasePair.isCanonicalCG(left, right);
        } else if (leftName == 'A' && rightName == 'U') {
            return BasePair.isCanonicalAU(left, right);
        } else if (leftName == 'G' && rightName == 'U') {
            return BasePair.isCanonicalGU(left, right);
        }

        return false;
    }

    public static boolean isCanonicalGU(PdbResidue guanine, PdbResidue uracil) {
        if (!guanine.hasAtom(AtomName.N1) || !guanine.hasAtom(AtomName.O6)) {
            return false;
        }
        if (!uracil.hasAtom(AtomName.O2) || !uracil.hasAtom(AtomName.N3)) {
            return false;
        }

        PdbAtomLine n1 = guanine.findAtom(AtomName.N1);
        PdbAtomLine o6 = guanine.findAtom(AtomName.O6);
        PdbAtomLine o2 = uracil.findAtom(AtomName.O2);
        PdbAtomLine n3 = uracil.findAtom(AtomName.N3);
        double n1o2 = n1.distanceTo(o2);
        double o6n3 = o6.distanceTo(n3);
        return n1o2 <= BasePair.GU_DISTANCE_N1_O2 && o6n3 <= BasePair.GU_DISTANCE_O6_N3;
    }

    public static boolean isCanonicalAU(PdbResidue adenine, PdbResidue uracil) {
        if (!adenine.hasAtom(AtomName.N1) || !adenine.hasAtom(AtomName.N6)) {
            return false;
        }
        if (!uracil.hasAtom(AtomName.N3) || !uracil.hasAtom(AtomName.O4)) {
            return false;
        }

        PdbAtomLine n1 = adenine.findAtom(AtomName.N1);
        PdbAtomLine n6 = adenine.findAtom(AtomName.N6);
        PdbAtomLine n3 = uracil.findAtom(AtomName.N3);
        PdbAtomLine o4 = uracil.findAtom(AtomName.O4);
        double n1n3 = n1.distanceTo(n3);
        double n6o4 = n6.distanceTo(o4);
        return n1n3 <= BasePair.AU_DISTANCE_N1_N3 && n6o4 <= BasePair.AU_DISTANCE_N6_O4;
    }

    public static boolean isCanonicalCG(PdbResidue cytosine, PdbResidue guanine) {
        if (!cytosine.hasAtom(AtomName.N3) || !cytosine.hasAtom(AtomName.O2) || !cytosine.hasAtom(AtomName.N4)) {
            return false;
        }
        if (!guanine.hasAtom(AtomName.N1) || !guanine.hasAtom(AtomName.N2) || !guanine.hasAtom(AtomName.O6)) {
            return false;
        }

        PdbAtomLine n3 = cytosine.findAtom(AtomName.N3);
        PdbAtomLine o2 = cytosine.findAtom(AtomName.O2);
        PdbAtomLine n4 = cytosine.findAtom(AtomName.N4);
        PdbAtomLine n1 = guanine.findAtom(AtomName.N1);
        PdbAtomLine n2 = guanine.findAtom(AtomName.N2);
        PdbAtomLine o6 = guanine.findAtom(AtomName.O6);
        double n3n1 = n3.distanceTo(n1);
        double o2n2 = o2.distanceTo(n2);
        double n4o6 = n4.distanceTo(o6);
        return n3n1 <= BasePair.CG_DISTANCE_N3_N1 && o2n2 <= BasePair.CG_DISTANCE_O2_N2 && n4o6 <= BasePair.CG_DISTANCE_N4_O6;
    }

    private final Pair<PdbResidueIdentifier, PdbResidueIdentifier> pair;

    public BasePair(PdbResidueIdentifier left, PdbResidueIdentifier right) {
        super();
        pair = Pair.of(left, right);
    }

    public PdbResidueIdentifier getLeft() {
        return pair.getLeft();
    }

    public PdbResidueIdentifier getRight() {
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
        if (o == null) {
            throw new NullPointerException();
        }

        if (equals(o)) {
            return 0;
        }

        return pair.compareTo(o.pair);
    }
}
