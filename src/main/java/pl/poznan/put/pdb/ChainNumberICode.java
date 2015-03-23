package pl.poznan.put.pdb;

import pl.poznan.put.pdb.analysis.PdbResidueIdentifier;

public interface ChainNumberICode {
    char getChainIdentifier();

    int getResidueNumber();

    char getInsertionCode();
    
    PdbResidueIdentifier getResidueIdentifier();
}
