package pl.poznan.put.pdb.analysis;

import pl.poznan.put.torsion.TorsionAngleType;

import java.util.List;

public interface ResidueInformationProvider {
  MoleculeType moleculeType();

  List<ResidueComponent> moleculeComponents();

  String description();

  char oneLetterName();

  String defaultPdbName();

  List<String> allPdbNames();

  List<TorsionAngleType> torsionAngleTypes();
}
