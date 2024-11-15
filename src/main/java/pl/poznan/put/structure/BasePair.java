package pl.poznan.put.structure;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.stream.Stream;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.pdb.analysis.PdbResidue;

/** A pairing between two nucleotides' bases. */
@Value.Immutable
@JsonSerialize(as = ImmutableBasePair.class)
@JsonDeserialize(as = ImmutableBasePair.class)
public abstract class BasePair implements Serializable, Comparable<BasePair> {
  private static final double GU_DISTANCE_O6_N3 = 2.83 + (0.13 * 3.0);
  private static final double GU_DISTANCE_N1_O2 = 2.79 + (0.13 * 3.0);
  private static final double AU_DISTANCE_N6_O4 = 3.00 + (0.17 * 3.0);
  private static final double AU_DISTANCE_N1_N3 = 2.84 + (0.12 * 3.0);
  private static final double CG_DISTANCE_N4_O6 = 2.96 + (0.17 * 3.0);
  private static final double CG_DISTANCE_O2_N2 = 2.77 + (0.15 * 3.0);
  private static final double CG_DISTANCE_N3_N1 = 2.89 + (0.11 * 3.0);

  /**
   * Checks if two residues are canonical base pairs by means of (1) distance between atoms which
   * form hydrogen bonds and (2) checking if two bases face each other.
   *
   * @param left First residue.
   * @param right Second residue.
   * @return True if there is a pair of C-G, A-U or G-U.
   */
  public static boolean isCanonicalPair(final PdbResidue left, final PdbResidue right) {
    final char leftName = Character.toUpperCase(left.oneLetterName());
    final char rightName = Character.toUpperCase(right.oneLetterName());

    if (leftName > rightName) {
      return BasePair.isCanonicalPair(right, left);
    }

    if ((leftName == 'C') && (rightName == 'G')) {
      return BasePair.isCanonicalCG(left, right);
    }
    if ((leftName == 'A') && (rightName == 'U' || rightName == 'T')) {
      return BasePair.isCanonicalAU(left, right);
    }
    return (leftName == 'G') && (rightName == 'U') && BasePair.isCanonicalGU(left, right);
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
    return (n1o2 <= BasePair.GU_DISTANCE_N1_O2)
        && (o6n3 <= BasePair.GU_DISTANCE_O6_N3)
        && BasePair.isFacingYR(uracil, guanine);
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
    return (n1n3 <= BasePair.AU_DISTANCE_N1_N3)
        && (n6o4 <= BasePair.AU_DISTANCE_N6_O4)
        && BasePair.isFacingYR(uracil, adenine);
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
        && (n4o6 <= BasePair.CG_DISTANCE_N4_O6)
        && BasePair.isFacingYR(cytosine, guanine);
  }

  private static boolean isFacingYR(final PdbResidue pyrimidine, final PdbResidue purine) {
    if (Stream.of(AtomName.C6, AtomName.N3).anyMatch(atomName -> !pyrimidine.hasAtom(atomName))) {
      return false;
    }
    if (Stream.of(AtomName.C8, AtomName.N1).anyMatch(atomName -> !purine.hasAtom(atomName))) {
      return false;
    }

    final PdbAtomLine c6 = pyrimidine.findAtom(AtomName.C6);
    final PdbAtomLine n3 = pyrimidine.findAtom(AtomName.N3);
    final PdbAtomLine c8 = purine.findAtom(AtomName.C8);
    final PdbAtomLine n1 = purine.findAtom(AtomName.N1);

    final Vector3D v1 = n3.toVector3D().subtract(c6.toVector3D());
    final Vector3D v2 = n1.toVector3D().subtract(c8.toVector3D());
    final double dotProduct = v1.dotProduct(v2);
    return dotProduct < 0.0;
  }

  /**
   * @return The first residue.
   */
  @Value.Parameter(order = 1)
  public abstract PdbNamedResidueIdentifier left();

  /**
   * @return The second residue.
   */
  @Value.Parameter(order = 2)
  public abstract PdbNamedResidueIdentifier right();

  public final BasePair invert() {
    return ImmutableBasePair.of(right(), left());
  }

  /**
   * @return True if the first residue is before the second one in 5'-3' order.
   */
  public final boolean is5to3() {
    return left().compareTo(right()) < 0;
  }

  @Override
  public final String toString() {
    return left() + " - " + right();
  }

  @Override
  public final int compareTo(final BasePair t) {
    return new CompareToBuilder().append(left(), t.left()).append(right(), t.right()).build();
  }
}
