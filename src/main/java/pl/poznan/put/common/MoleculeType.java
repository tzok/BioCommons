package pl.poznan.put.common;

import pl.poznan.put.pdb.analysis.InvalidBondRule;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.ProteinBondRule;
import pl.poznan.put.pdb.analysis.RNABondRule;
import pl.poznan.put.pdb.analysis.ResidueBondRule;

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
