package pl.poznan.put.torsion;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.types.Quadruplet;

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
