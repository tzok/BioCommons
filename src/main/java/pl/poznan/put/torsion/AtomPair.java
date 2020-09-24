package pl.poznan.put.torsion;

import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.atom.AtomType;
import pl.poznan.put.atom.Bond;
import pl.poznan.put.atom.BondLength;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbResidueIdentifier;

import java.util.Locale;

/** A pair of atoms. */
@Value.Immutable
public abstract class AtomPair implements Comparable<AtomPair> {
  /** @return The first atom. */
  @Value.Parameter(order = 1)
  abstract PdbAtomLine leftAtom();

  /** @return The second atom. */
  @Value.Parameter(order = 2)
  abstract PdbAtomLine rightAtom();

  /**
   * Generate a validation message if the distance between this atom pair is larger than 150% of the
   * maximum expected bond length (see {@link Bond#length(AtomType, AtomType)}).
   *
   * @return A message detailing the invalid distance or empty string ("") if all is fine.
   */
  public final String generateValidationMessage() {
    if (isValid()) {
      return "";
    }

    final PdbResidueIdentifier leftIdentifier = PdbResidueIdentifier.from(leftAtom());
    final PdbResidueIdentifier rightIdentifier = PdbResidueIdentifier.from(rightAtom());

    if (leftIdentifier.equals(rightIdentifier)) {
      return String.format(
          Locale.US,
          "%s-%s distance in %s is %.2f but should be in range [%.2f; %.2f]",
          leftAtom().atomName(),
          rightAtom().atomName(),
          leftIdentifier,
          distance(),
          bondLength().min(),
          bondLength().max());
    }

    return String.format(
        Locale.US,
        "%s-%s distance between %s and %s is %.2f but should be in range [%.2f; %.2f]",
        leftAtom().atomName(),
        rightAtom().atomName(),
        leftIdentifier,
        rightIdentifier,
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
    return distance() <= bondLength().max() * 1.5;
  }
}
