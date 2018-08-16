package pl.poznan.put.pdb.analysis;

import java.util.List;
import pl.poznan.put.pdb.PdbResidueIdentifier;

public interface ResidueCollection {
  List<PdbResidue> getResidues();

  PdbResidue findResidue(String chainIdentifier, int residueNumber, String insertionCode);

  PdbResidue findResidue(PdbResidueIdentifier query);
}
