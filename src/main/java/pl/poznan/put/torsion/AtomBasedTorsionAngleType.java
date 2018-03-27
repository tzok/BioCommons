package pl.poznan.put.torsion;

import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.types.Quadruplet;

@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public abstract class AtomBasedTorsionAngleType extends TorsionAngleType {
  @XmlElement private String displayName;
  @XmlTransient private Quadruplet<AtomName> atoms;
  @XmlTransient private Quadruplet<Integer> residueRule;

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
  public final boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if ((o == null) || (getClass() != o.getClass())) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    final AtomBasedTorsionAngleType other = (AtomBasedTorsionAngleType) o;
    return Objects.equals(displayName, other.displayName)
        && Objects.equals(atoms, other.atoms)
        && Objects.equals(residueRule, other.residueRule);
  }

  @Override
  public final int hashCode() {
    return Objects.hash(super.hashCode(), displayName, atoms, residueRule);
  }

  @Override
  public final TorsionAngleValue calculate(
      final List<PdbResidue> residues, final int currentIndex) {
    final PdbAtomLine[] foundAtoms = new PdbAtomLine[4];

    for (int i = 0; i < 4; i++) {
      final int index = currentIndex + residueRule.get(i);
      if ((index < 0) || (index >= residues.size())) {
        return TorsionAngleValue.invalidInstance(this);
      }

      final PdbResidue residue = residues.get(index);
      if (!residue.hasAtom(atoms.get(i))) {
        return TorsionAngleValue.invalidInstance(this);
      }

      foundAtoms[i] = residue.findAtom(atoms.get(i));
    }

    return new TorsionAngleValue(
        this,
        TorsionAnglesHelper.calculateTorsionAngle(
            foundAtoms[0], foundAtoms[1],
            foundAtoms[2], foundAtoms[3]));
  }

  public final TorsionAngleValue calculate(
      final PdbAtomLine a1, final PdbAtomLine a2, final PdbAtomLine a3, final PdbAtomLine a4) {
    return new TorsionAngleValue(this, TorsionAnglesHelper.calculateTorsionAngle(a1, a2, a3, a4));
  }
}
