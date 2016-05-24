package pl.poznan.put.pdb;


public interface ChainNumberICode {
    String getChainIdentifier();

    int getResidueNumber();

    String getInsertionCode();
    
    PdbResidueIdentifier getResidueIdentifier();
}
