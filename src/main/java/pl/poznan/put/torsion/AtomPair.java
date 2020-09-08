package pl.poznan.put.torsion;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.atom.Bond;
import pl.poznan.put.atom.BondLength;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.analysis.PdbResidue;

import java.util.Locale;

@Value.Immutable
public abstract class AtomPair implements Comparable<AtomPair> {
  @Value.Parameter(order = 1)
  public abstract PdbResidue leftResidue();

  @Value.Parameter(order = 2)
  public abstract PdbResidue rightResidue();

  @Value.Parameter(order = 3)
  abstract PdbAtomLine leftAtom();

  @Value.Parameter(order = 4)
  abstract PdbAtomLine rightAtom();

  public final String generateValidationMessage() {
    if (isValid()) {
      return "";
    }

    if (leftResidue().equals(rightResidue())) {
      return String.format(
          Locale.US,
          "%s-%s distance in %s is %.2f but should be in range [%.2f; %.2f]",
          leftAtom().atomName(),
          rightAtom().atomName(),
          leftResidue().toResidueIdentifer(),
          distance(),
          bondLength().min(),
          bondLength().max());
    }

    return String.format(
        Locale.US,
        "%s-%s distance between %s and %s is %.2f but should be in range [%.2f; %.2f]",
        leftAtom().atomName(),
        rightAtom().atomName(),
        leftResidue().toResidueIdentifer(),
        rightResidue().toResidueIdentifer(),
        distance(),
        bondLength().min(),
        bondLength().max());
  }

  @Override
  public final int compareTo(final AtomPair t) {
    return Integer.compare(leftAtom().serialNumber(), t.leftAtom().serialNumber());
  }

  @Value.Lazy
  protected double distance() {
    return leftAtom().distanceTo(rightAtom());
  }

  @Value.Lazy
  protected BondLength bondLength() {
    final AtomName leftAtomName = leftAtom().detectAtomName();
    final AtomName rightAtomName = rightAtom().detectAtomName();
    return Bond.length(leftAtomName.getType(), rightAtomName.getType());
  }

  private boolean isValid() {
    // skip check if any of the residues has icode
    if (StringUtils.isNotBlank(leftResidue().insertionCode())
        || StringUtils.isNotBlank(rightResidue().insertionCode())) {
      return true;
    }

    // skip check if residues are in different chains
    if (!leftResidue().chainIdentifier().equals(rightResidue().chainIdentifier())) {
      return true;
    }

    // skip check if residues are not consecutive
    if (Math.abs(leftResidue().residueNumber() - rightResidue().residueNumber()) > 1) {
      return true;
    }

    return distance() <= bondLength().max() * 1.5;
  }
}
