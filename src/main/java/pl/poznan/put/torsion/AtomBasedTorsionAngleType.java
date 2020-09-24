package pl.poznan.put.torsion;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.circular.ImmutableAngle;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.types.Quadruple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** A torsion angle which is defined upon four atomic coordinates. */
@Value.Immutable
public interface AtomBasedTorsionAngleType extends TorsionAngleType {
  @Override
  @Value.Parameter(order = 1)
  MoleculeType moleculeType();

  /**
   * Calculates the value of this torsion angle (see {@link
   * TorsionAnglesHelper#calculateTorsionAngle(PdbAtomLine, PdbAtomLine, PdbAtomLine, PdbAtomLine)}
   * and {@link pl.poznan.put.circular.Angle#torsionAngle(Vector3D, Vector3D, Vector3D, Vector3D)}).
   *
   * @param residues The list of residues.
   * @param currentIndex The index of current residue.
   * @return The value of torsion angle of this type.
   */
  @Override
  default TorsionAngleValue calculate(final List<PdbResidue> residues, final int currentIndex) {
    final List<AtomPair> atomPairs = findAtomPairs(residues, currentIndex);

    if (atomPairs.isEmpty()) {
      return ImmutableTorsionAngleValue.of(this, ImmutableAngle.of(Double.NaN));
    }

    assert atomPairs.size() == 3;
    return ImmutableTorsionAngleValue.of(
        this,
        TorsionAnglesHelper.calculateTorsionAngle(
            atomPairs.get(0).leftAtom(),
            atomPairs.get(1).leftAtom(),
            atomPairs.get(2).leftAtom(),
            atomPairs.get(2).rightAtom()));
  }

  @Override
  @Value.Parameter(order = 2)
  String shortDisplayName();

  @Override
  default String longDisplayName() {
    return String.format(
        "%s (%s) %s-%s-%s-%s",
        shortDisplayName(), exportName(), atoms().a(), atoms().b(), atoms().c(), atoms().d());
  }

  @Override
  @Value.Parameter(order = 3)
  String exportName();

  /** @return The quadruple of atoms defining this torsion angle type. */
  @Value.Parameter(order = 4)
  Quadruple<AtomName> atoms();

  /**
   * @return The quadruple of relative indices to take atoms from. For example, a rule (0, 0, 0, 0)
   *     means that all atoms are from the same residue, while a rule (-1, 0, 0, 0) means that the
   *     first atom is taken from the preceding residue.
   */
  @Value.Parameter(order = 5)
  Quadruple<Integer> residueRule();

  /**
   * @return True if this is an instance of a pseudo-torsion angle type. A pseudo-torsion angle type
   *     is defined on a quadruple of atoms which are not connected (See {@link
   *     pl.poznan.put.rna.NucleotideTorsionAngle#ETA} or {@link
   *     pl.poznan.put.protein.AminoAcidTorsionAngle#CALPHA}).
   */
  @Value.Default
  default boolean isPseudoTorsion() {
    return false;
  }

  /**
   * Calculates a value of this torsion angle type given atoms explicitly.
   *
   * @param a1 The first atom.
   * @param a2 The second atom.
   * @param a3 The third atom.
   * @param a4 The fourth atom.
   * @return A value of this torsion angle type.
   */
  default TorsionAngleValue calculate(
      final PdbAtomLine a1, final PdbAtomLine a2, final PdbAtomLine a3, final PdbAtomLine a4) {
    return ImmutableTorsionAngleValue.of(
        this, TorsionAnglesHelper.calculateTorsionAngle(a1, a2, a3, a4));
  }

  /**
   * Applies {@code residueRule()} on the given list of residues to find three pairs of atoms: (a1,
   * a2), (a2, a3), (a3, a4).
   *
   * @param residues The list of residues.
   * @param currentIndex Index of the current residue.
   * @return The list of atom pairs with three entries: (a1, a2), (a2, a3), (a3, a4)
   */
  default List<AtomPair> findAtomPairs(final List<PdbResidue> residues, final int currentIndex) {
    final List<PdbAtomLine> foundAtoms = new ArrayList<>(4);

    for (int i = 0; i < 4; i++) {
      final int index = currentIndex + residueRule().get(i);
      if ((index < 0) || (index >= residues.size())) {
        return Collections.emptyList();
      }

      final PdbResidue residue = residues.get(index);
      if (!residue.hasAtom(atoms().get(i))) {
        return Collections.emptyList();
      }

      foundAtoms.add(residue.findAtom(atoms().get(i)));
    }

    return IntStream.range(1, 4)
        .mapToObj(i -> ImmutableAtomPair.of(foundAtoms.get(i - 1), foundAtoms.get(i)))
        .collect(Collectors.toList());
  }
}
