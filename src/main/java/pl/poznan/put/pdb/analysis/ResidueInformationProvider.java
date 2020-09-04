package pl.poznan.put.pdb.analysis;

import pl.poznan.put.atom.AtomName;
import pl.poznan.put.torsion.TorsionAngleType;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public interface ResidueInformationProvider {
  default Set<AtomName> centralAtoms() {
    switch (moleculeType()) {
      case RNA:
        return EnumSet.of(AtomName.P, AtomName.PA);
      case PROTEIN:
        return Collections.singleton(AtomName.CA);
      case UNKNOWN:
      default:
        return Collections.singleton(AtomName.UNKNOWN);
    }
  }

  MoleculeType moleculeType();

  List<ResidueComponent> moleculeComponents();

  String description();

  char oneLetterName();

  String defaultPdbName();

  List<String> allPdbNames();

  List<TorsionAngleType> torsionAngleTypes();
}
