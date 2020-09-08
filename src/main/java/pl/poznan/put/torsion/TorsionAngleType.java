package pl.poznan.put.torsion;

import pl.poznan.put.interfaces.DisplayableExportable;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;

import java.util.List;

public interface TorsionAngleType extends DisplayableExportable {
  MoleculeType moleculeType();

  TorsionAngleValue calculate(List<? extends PdbResidue> residues, int currentIndex);
}
