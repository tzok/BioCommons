package pl.poznan.put.torsion;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.atom.Bond;
import pl.poznan.put.atom.BondLength;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.analysis.PdbResidue;

import java.util.Locale;

@Data
public final class AtomPair implements Comparable<AtomPair> {
  private final PdbResidue leftResidue;
  private final PdbResidue rightResidue;
  private final PdbAtomLine leftAtom;
  private final PdbAtomLine rightAtom;

  private final double distance;
  private final BondLength bondLength;

  AtomPair(
      final PdbResidue leftResidue,
      final PdbResidue rightResidue,
      final PdbAtomLine leftAtom,
      final PdbAtomLine rightAtom) {
    super();
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
  public int hashCode() {
    return leftResidue.hashCode()
        + rightResidue.hashCode()
        + leftAtom.hashCode()
        + rightAtom.hashCode();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final AtomPair atomPair = (AtomPair) o;
    return (leftResidue.equals(atomPair.leftResidue)
            && rightResidue.equals(atomPair.rightResidue)
            && leftAtom.equals(atomPair.leftAtom)
            && rightAtom.equals(atomPair.rightAtom))
        || (leftResidue.equals(atomPair.rightResidue)
            && rightResidue.equals(atomPair.leftResidue)
            && leftAtom.equals(atomPair.rightAtom)
            && rightAtom.equals(atomPair.leftAtom));
  }

  public String generateValidationMessage() {
    if (isValid()) {
      return "";
    }

    if (leftResidue.equals(rightResidue)) {
      return String.format(
          Locale.US,
          "%s-%s distance in %s is %.2f but should be in range [%.2f; %.2f]",
          leftAtom.atomName(),
          rightAtom.atomName(),
          leftResidue.toResidueIdentifer(),
          distance,
          bondLength.min(),
          bondLength.max());
    }

    return String.format(
        Locale.US,
        "%s-%s distance between %s and %s is %.2f but should be in range [%.2f; %.2f]",
        leftAtom.atomName(),
        rightAtom.atomName(),
        leftResidue.toResidueIdentifer(),
        rightResidue.toResidueIdentifer(),
        distance,
        bondLength.min(),
        bondLength.max());
  }

  @Override
  public int compareTo(final AtomPair t) {
    return Integer.compare(leftAtom.serialNumber(), t.leftAtom.serialNumber());
  }

  private boolean isValid() {
    // skip check if any of the residues has icode
    if (StringUtils.isNotBlank(leftResidue.insertionCode())
        || StringUtils.isNotBlank(rightResidue.insertionCode())) {
      return true;
    }

    // skip check if residues are in different chains
    if (!leftResidue.chainIdentifier().equals(rightResidue.chainIdentifier())) {
      return true;
    }

    // skip check if residues are not consecutive
    if (Math.abs(leftResidue.residueNumber() - rightResidue.residueNumber()) > 1) {
      return true;
    }

    return distance <= bondLength.max() * 1.5;
  }
}
