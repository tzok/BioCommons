package pl.poznan.put.structure.secondary;

import org.apache.commons.lang3.tuple.Pair;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.pdb.analysis.PdbResidue;

import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Stream;

public class BasePair implements Serializable, Comparable<BasePair> {
  private static final long serialVersionUID = -8951633864787036880L;
  private static final double GU_DISTANCE_O6_N3 = 2.83 + (0.13 * 3.0);
  private static final double GU_DISTANCE_N1_O2 = 2.79 + (0.13 * 3.0);
  private static final double AU_DISTANCE_N6_O4 = 3.00 + (0.17 * 3.0);
  private static final double AU_DISTANCE_N1_N3 = 2.84 + (0.12 * 3.0);
  private static final double CG_DISTANCE_N4_O6 = 2.96 + (0.17 * 3.0);
  private static final double CG_DISTANCE_O2_N2 = 2.77 + (0.15 * 3.0);
  private static final double CG_DISTANCE_N3_N1 = 2.89 + (0.11 * 3.0);
  private final Pair<PdbResidueIdentifier, PdbResidueIdentifier> pair;

  public BasePair(final PdbResidueIdentifier left, final PdbResidueIdentifier right) {
    super();
    pair = Pair.of(left, right);
  }

  /**
   * Check if two residues are canonical base pairs by means of simple distance between atoms which
   * form hydrogen bond. Data taken from http://bps.rutgers.edu.
   *
   * @param left First residue.
   * @param right Second residue.
   * @return True if we have a pair of C-G, A-U or G-U and atoms' distances are within limits to
   *     form hydrogen bonds.
   */
  public static boolean isCanonicalPair(final PdbResidue left, final PdbResidue right) {
    final char leftName = Character.toUpperCase(left.getOneLetterName());
    final char rightName = Character.toUpperCase(right.getOneLetterName());

    if (leftName > rightName) {
      return BasePair.isCanonicalPair(right, left);
    }

    if ((leftName == 'C') && (rightName == 'G')) {
      return BasePair.isCanonicalCG(left, right);
    }
    if ((leftName == 'A') && (rightName == 'U')) {
      return BasePair.isCanonicalAU(left, right);
    }
    return (leftName == 'G')
        && (rightName == 'U')
        && BasePair.isCanonicalGU(left, right);
  }

  private static boolean isCanonicalGU(final PdbResidue guanine, final PdbResidue uracil) {
    if (!guanine.hasAtom(AtomName.N1) || !guanine.hasAtom(AtomName.O6)) {
      return false;
    }
    if (!uracil.hasAtom(AtomName.O2) || !uracil.hasAtom(AtomName.N3)) {
      return false;
    }

    final PdbAtomLine n1 = guanine.findAtom(AtomName.N1);
    final PdbAtomLine o6 = guanine.findAtom(AtomName.O6);
    final PdbAtomLine o2 = uracil.findAtom(AtomName.O2);
    final PdbAtomLine n3 = uracil.findAtom(AtomName.N3);
    final double n1o2 = n1.distanceTo(o2);
    final double o6n3 = o6.distanceTo(n3);
    return (n1o2 <= BasePair.GU_DISTANCE_N1_O2) && (o6n3 <= BasePair.GU_DISTANCE_O6_N3);
  }

  private static boolean isCanonicalAU(final PdbResidue adenine, final PdbResidue uracil) {
    if (!adenine.hasAtom(AtomName.N1) || !adenine.hasAtom(AtomName.N6)) {
      return false;
    }
    if (!uracil.hasAtom(AtomName.N3) || !uracil.hasAtom(AtomName.O4)) {
      return false;
    }

    final PdbAtomLine n1 = adenine.findAtom(AtomName.N1);
    final PdbAtomLine n6 = adenine.findAtom(AtomName.N6);
    final PdbAtomLine n3 = uracil.findAtom(AtomName.N3);
    final PdbAtomLine o4 = uracil.findAtom(AtomName.O4);
    final double n1n3 = n1.distanceTo(n3);
    final double n6o4 = n6.distanceTo(o4);
    return (n1n3 <= BasePair.AU_DISTANCE_N1_N3) && (n6o4 <= BasePair.AU_DISTANCE_N6_O4);
  }

  private static boolean isCanonicalCG(final PdbResidue cytosine, final PdbResidue guanine) {
    if (Stream.of(AtomName.N3, AtomName.O2, AtomName.N4)
        .anyMatch(atomName -> !cytosine.hasAtom(atomName))) {
      return false;
    }
    if (Stream.of(AtomName.N1, AtomName.N2, AtomName.O6)
        .anyMatch(atomName -> !guanine.hasAtom(atomName))) {
      return false;
    }

    final PdbAtomLine n3 = cytosine.findAtom(AtomName.N3);
    final PdbAtomLine o2 = cytosine.findAtom(AtomName.O2);
    final PdbAtomLine n4 = cytosine.findAtom(AtomName.N4);
    final PdbAtomLine n1 = guanine.findAtom(AtomName.N1);
    final PdbAtomLine n2 = guanine.findAtom(AtomName.N2);
    final PdbAtomLine o6 = guanine.findAtom(AtomName.O6);
    final double n3n1 = n3.distanceTo(n1);
    final double o2n2 = o2.distanceTo(n2);
    final double n4o6 = n4.distanceTo(o6);
    return (n3n1 <= BasePair.CG_DISTANCE_N3_N1)
        && (o2n2 <= BasePair.CG_DISTANCE_O2_N2)
        && (n4o6 <= BasePair.CG_DISTANCE_N4_O6);
  }

  public final PdbResidueIdentifier getLeft() {
    return pair.getLeft();
  }

  public final PdbResidueIdentifier getRight() {
    return pair.getRight();
  }

  public final BasePair invert() {
    return new BasePair(pair.getRight(), pair.getLeft());
  }

  public final boolean is5to3() {
    return pair.getLeft().getResidueNumber() < pair.getRight().getResidueNumber();
  }

  @Override
  public final int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((pair == null) ? 0 : pair.hashCode());
    return result;
  }

  @Override
  public final boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    if (getClass() != o.getClass()) {
      return false;
    }
    final BasePair other = (BasePair) o;
    return pair == null ? other.pair == null : Objects.equals(pair, other.pair);
  }

  @Override
  public final String toString() {
    return pair.getLeft() + " - " + pair.getRight();
  }

  @Override
  public final int compareTo(final BasePair t) {
    return pair.compareTo(t.pair);
  }
}
