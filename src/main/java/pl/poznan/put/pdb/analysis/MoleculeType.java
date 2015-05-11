package pl.poznan.put.pdb.analysis;

public enum MoleculeType {
    RNA(new RNABondRule()),
    PROTEIN(new ProteinBondRule()),
    UNKNOWN(new InvalidBondRule());

    private final ResidueBondRule bondRule;

    private MoleculeType(ResidueBondRule bondRule) {
        this.bondRule = bondRule;
    }

    public boolean areConnected(PdbResidue r1, PdbResidue r2) {
        return bondRule.areConnected(r1, r2);
    }
}
