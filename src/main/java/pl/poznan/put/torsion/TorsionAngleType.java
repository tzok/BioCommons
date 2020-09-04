package pl.poznan.put.torsion;

import lombok.Data;
import pl.poznan.put.interfaces.DisplayableExportable;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;

import java.util.List;

@Data
public abstract class TorsionAngleType implements DisplayableExportable {
  private final MoleculeType moleculeType;

  public abstract TorsionAngleValue calculate(
      List<? extends PdbResidue> residues, int currentIndex);
}
