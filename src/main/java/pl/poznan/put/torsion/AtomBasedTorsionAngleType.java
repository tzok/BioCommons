package pl.poznan.put.torsion;

import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.circular.ImmutableAngle;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.types.Quadruplet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Value.Immutable
public interface AtomBasedTorsionAngleType extends TorsionAngleType {
  @Override
  @Value.Parameter(order = 1)
  MoleculeType moleculeType();

  default TorsionAngleValue calculate(
      final List<? extends PdbResidue> residues, final int currentIndex) {
    final List<AtomPair> atomPairs = findAtomPairs(residues, currentIndex);

    if (atomPairs.isEmpty()) {
      return new TorsionAngleValue(this, ImmutableAngle.of(Double.NaN));
    }

    assert atomPairs.size() == 3;
    return new TorsionAngleValue(
        this,
        TorsionAnglesHelper.calculateTorsionAngle(
            atomPairs.get(0).getLeftAtom(),
            atomPairs.get(1).getLeftAtom(),
            atomPairs.get(2).getLeftAtom(),
            atomPairs.get(2).getRightAtom()));
  }

  @Override
  @Value.Parameter(order = 2)
  String shortDisplayName();

  default String longDisplayName() {
    return String.format(
        "%s (%s) %s-%s-%s-%s",
        shortDisplayName(), exportName(), atoms().a(), atoms().b(), atoms().c(), atoms().d());
  }

  @Override
  @Value.Parameter(order = 3)
  String exportName();

  @Value.Parameter(order = 4)
  Quadruplet<AtomName> atoms();

  @Value.Parameter(order = 5)
  Quadruplet<Integer> residueRule();

  @Value.Default
  default boolean isPseudoTorsion() {
    return false;
  }

  default TorsionAngleValue calculate(
      final PdbAtomLine a1, final PdbAtomLine a2, final PdbAtomLine a3, final PdbAtomLine a4) {
    return new TorsionAngleValue(this, TorsionAnglesHelper.calculateTorsionAngle(a1, a2, a3, a4));
  }

  default List<AtomPair> findAtomPairs(
      final List<? extends PdbResidue> residues, final int currentIndex) {
    final List<PdbResidue> foundResidues = new ArrayList<>(4);
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

      foundResidues.add(residue);
      foundAtoms.add(residue.findAtom(atoms().get(i)));
    }

    return IntStream.range(1, 4)
        .mapToObj(
            i ->
                new AtomPair(
                    foundResidues.get(i - 1),
                    foundResidues.get(i),
                    foundAtoms.get(i - 1),
                    foundAtoms.get(i)))
        .collect(Collectors.toList());
  }
}
