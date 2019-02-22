package pl.poznan.put.pdb.analysis;

import pl.poznan.put.torsion.TorsionAngleType;

import java.util.Collections;
import java.util.List;

public class InvalidResidueInformationProvider implements ResidueInformationProvider {
  private final MoleculeType moleculeType;
  private final String pdbName;

  public InvalidResidueInformationProvider(final String pdbName) {
    super();
    moleculeType = MoleculeType.UNKNOWN;
    this.pdbName = pdbName;
  }

  @Override
  public final MoleculeType getMoleculeType() {
    return moleculeType;
  }

  @Override
  public final List<ResidueComponent> getAllMoleculeComponents() {
    return Collections.emptyList();
  }

  @Override
  public final String getDescription() {
    return "";
  }

  @Override
  public final char getOneLetterName() {
    assert (pdbName != null) && !pdbName.isEmpty();
    return pdbName.charAt(pdbName.length() - 1);
  }

  @Override
  public final String getDefaultPdbName() {
    return pdbName;
  }

  @Override
  public final List<String> getPdbNames() {
    return Collections.singletonList(pdbName);
  }

  @Override
  public final List<TorsionAngleType> getTorsionAngleTypes() {
    return Collections.emptyList();
  }
}
