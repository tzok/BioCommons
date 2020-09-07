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
  public final MoleculeType moleculeType() {
    return moleculeType;
  }

  @Override
  public final List<ResidueComponent> moleculeComponents() {
    return Collections.emptyList();
  }

  @Override
  public final String description() {
    return "";
  }

  @Override
  public final char oneLetterName() {
    assert (pdbName != null) && !pdbName.isEmpty();
    return pdbName.charAt(pdbName.length() - 1);
  }

  @Override
  public final String defaultPdbName() {
    return pdbName;
  }

  @Override
  public final List<String> allPdbNames() {
    return Collections.singletonList(pdbName);
  }

  @Override
  public final List<TorsionAngleType> torsionAngleTypes() {
    return Collections.emptyList();
  }
}
