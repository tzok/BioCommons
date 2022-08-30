package pl.poznan.put.torsion;

import java.util.List;
import pl.poznan.put.interfaces.DisplayableExportable;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;

/** A torsion angle. */
public interface TorsionAngleType extends DisplayableExportable {
  /**
   * @return The molecule this torsion angle is defined for.
   */
  MoleculeType moleculeType();

  /**
   * Calculates the value of this torsion angle.
   *
   * @param residues The list of residues.
   * @param currentIndex The index of current residue.
   * @return The value of torsion angle of this type.
   */
  TorsionAngleValue calculate(List<PdbResidue> residues, int currentIndex);
}
