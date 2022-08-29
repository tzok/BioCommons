package pl.poznan.put.pdb.analysis;

import java.util.List;
import pl.poznan.put.torsion.TorsionAngleType;

/**
 * A provider of detailed information about a residue (its type, expected atoms, torsion angles
 * etc).
 */
public interface ResidueInformationProvider {
  /**
   * @return The type of molecule of this residue (RNA or protein).
   */
  MoleculeType moleculeType();

  /**
   * @return The list of components this residue consists of.
   */
  List<ResidueComponent> moleculeComponents();

  /**
   * @return A one letter name to describe this type of residue.
   */
  char oneLetterName();

  /**
   * @return The list of all names this residues may be found in PDB and mmCIF files.
   */
  List<String> aliases();

  /**
   * @return The list of torsion angle types defined for this residue.
   */
  List<TorsionAngleType> torsionAngleTypes();

  /**
   * @return The default name of this residue.
   */
  default String defaultName() {
    return aliases().stream().findFirst().orElse("UNK");
  }
}
