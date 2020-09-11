package pl.poznan.put.protein;

import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.ResidueComponent;
import pl.poznan.put.pdb.analysis.ResidueInformationProvider;

/** A sidechain in a protein. */
public interface Sidechain extends ResidueComponent, ResidueInformationProvider {
  @Override
  default MoleculeType moleculeType() {
    return MoleculeType.PROTEIN;
  }
}
