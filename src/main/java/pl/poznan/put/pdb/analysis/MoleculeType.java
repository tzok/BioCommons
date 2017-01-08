package pl.poznan.put.pdb.analysis;

public enum MoleculeType {
    RNA(new RNABondRule()),
    PROTEIN(new ProteinBondRule()),
    UNKNOWN(new InvalidBondRule());

    private final ResidueBondRule bondRule;

    MoleculeType(final ResidueBondRule bondRule) {
        this.bondRule = bondRule;
    }

    public boolean areConnected(final PdbResidue r1, final PdbResidue r2) {
        return bondRule.areConnected(r1, r2);
    }
}
