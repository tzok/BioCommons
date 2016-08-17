package pl.poznan.put.pdb.analysis;

import pl.poznan.put.pdb.PdbResidueIdentifier;

import java.util.List;

public interface ResidueCollection {
    List<PdbResidue> getResidues();

    PdbResidue findResidue(String chainIdentifier, int residueNumber,
                           String insertionCode);

    PdbResidue findResidue(PdbResidueIdentifier query);
}
