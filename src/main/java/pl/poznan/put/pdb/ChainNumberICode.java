package pl.poznan.put.pdb;


public interface ChainNumberICode {
    char getChainIdentifier();

    int getResidueNumber();

    char getInsertionCode();
    
    PdbResidueIdentifier getResidueIdentifier();
}
