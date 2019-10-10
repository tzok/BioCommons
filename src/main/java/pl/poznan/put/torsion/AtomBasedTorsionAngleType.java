package pl.poznan.put.torsion;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.atom.Bond;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.types.Quadruplet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AtomBasedTorsionAngleType extends TorsionAngleType {
  private String displayName;
  private Quadruplet<AtomName> atoms;
  private Quadruplet<Integer> residueRule;

  protected AtomBasedTorsionAngleType(
      final MoleculeType moleculeType,
      final String displayName,
      final Quadruplet<AtomName> atoms,
      final Quadruplet<Integer> residueRule) {
    super(moleculeType);
    this.displayName = displayName;
    this.atoms = atoms;
    this.residueRule = residueRule;
  }

  @Override
  public final String getLongDisplayName() {
    return String.format(
        "%s(%s)%s-%s-%s-%s", displayName, getExportName(), atoms.a, atoms.b, atoms.c, atoms.d);
  }

  @Override
  public final String getShortDisplayName() {
    return displayName;
  }

  @Override
  public final String getExportName() {
    return getClass().getSimpleName().toLowerCase();
  }

  @Override
  public final TorsionAngleValue calculate(
      final List<PdbResidue> residues, final int currentIndex) {
    final List<AtomPair> atomPairs = findAtomPairs(residues, currentIndex);

    if (atomPairs.isEmpty()) {
      return TorsionAngleValue.invalidInstance(this);
    }

    assert atomPairs.size() == 3;
    return new TorsionAngleValue(
        this,
        TorsionAnglesHelper.calculateTorsionAngle(
            atomPairs.get(0).leftAtom,
            atomPairs.get(1).leftAtom,
            atomPairs.get(2).leftAtom,
            atomPairs.get(2).rightAtom));
  }

  public final List<AtomPair> findAtomPairs(List<PdbResidue> residues, int currentIndex) {
    final List<PdbResidue> foundResidues = new ArrayList<>(4);
    final List<PdbAtomLine> foundAtoms = new ArrayList<>(4);

    for (int i = 0; i < 4; i++) {
      final int index = currentIndex + residueRule.get(i);
      if ((index < 0) || (index >= residues.size())) {
        return Collections.emptyList();
      }

      final PdbResidue residue = residues.get(index);
      if (!residue.hasAtom(this.atoms.get(i))) {
        return Collections.emptyList();
      }

      foundResidues.add(residue);
      foundAtoms.add(residue.findAtom(this.atoms.get(i)));
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

  public final TorsionAngleValue calculate(
      final PdbAtomLine a1, final PdbAtomLine a2, final PdbAtomLine a3, final PdbAtomLine a4) {
    return new TorsionAngleValue(this, TorsionAnglesHelper.calculateTorsionAngle(a1, a2, a3, a4));
  }

  @Data
  public static class AtomPair implements Comparable<AtomPair> {
    private final PdbResidue leftResidue;
    private final PdbResidue rightResidue;
    private final PdbAtomLine leftAtom;
    private final PdbAtomLine rightAtom;

    private final double distance;
    private final Bond.Length bondLength;

    public AtomPair(
        PdbResidue leftResidue,
        PdbResidue rightResidue,
        PdbAtomLine leftAtom,
        PdbAtomLine rightAtom) {
      this.leftResidue = leftResidue;
      this.rightResidue = rightResidue;
      this.leftAtom = leftAtom;
      this.rightAtom = rightAtom;

      distance = leftAtom.distanceTo(rightAtom);

      final AtomName leftAtomName = leftAtom.detectAtomName();
      final AtomName rightAtomName = rightAtom.detectAtomName();
      bondLength = Bond.length(leftAtomName.getType(), rightAtomName.getType());
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      AtomPair atomPair = (AtomPair) o;
      return (leftResidue.equals(atomPair.leftResidue)
              && rightResidue.equals(atomPair.rightResidue)
              && leftAtom.equals(atomPair.leftAtom)
              && rightAtom.equals(atomPair.rightAtom))
          || (leftResidue.equals(atomPair.rightResidue)
              && rightResidue.equals(atomPair.leftResidue)
              && leftAtom.equals(atomPair.rightAtom)
              && rightAtom.equals(atomPair.leftAtom));
    }

    @Override
    public int hashCode() {
      return leftResidue.hashCode()
          + rightResidue.hashCode()
          + leftAtom.hashCode()
          + rightAtom.hashCode();
    }

    public boolean isValid() {
      // skip check if any of the residues has icode
      if (StringUtils.isNotBlank(leftResidue.getInsertionCode())
          || StringUtils.isNotBlank(rightResidue.getInsertionCode())) {
        return true;
      }

      // skip check if residues are in different chains
      if (!leftResidue.getChainIdentifier().equals(rightResidue.getChainIdentifier())) {
        return true;
      }

      // skip check if residues are not consecutive
      if (Math.abs(leftResidue.getResidueNumber() - rightResidue.getResidueNumber()) > 1) {
        return true;
      }

      return distance <= bondLength.getMax() * 1.5;
    }

    public String generateValidationMessage() {
      if (isValid()) {
        return "";
      }

      if (leftResidue.equals(rightResidue)) {
        return String.format(
            Locale.US,
            "%s-%s distance in %s is %.2f but should be in range [%.2f; %.2f]",
            leftAtom.getAtomName(),
            rightAtom.getAtomName(),
            leftResidue.getResidueIdentifier(),
            distance,
            bondLength.getMin(),
            bondLength.getMax());
      }

      return String.format(
          Locale.US,
          "%s-%s distance between %s and %s is %.2f but should be in range [%.2f; %.2f]",
          leftAtom.getAtomName(),
          rightAtom.getAtomName(),
          leftResidue.getResidueIdentifier(),
          rightResidue.getResidueIdentifier(),
          distance,
          bondLength.getMin(),
          bondLength.getMax());
    }

    @Override
    public int compareTo(@NotNull AtomBasedTorsionAngleType.AtomPair atomPair) {
      return Integer.compare(leftAtom.getSerialNumber(), atomPair.leftAtom.getSerialNumber());
    }
  }
}
