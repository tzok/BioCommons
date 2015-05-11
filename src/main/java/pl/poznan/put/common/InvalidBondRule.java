package pl.poznan.put.common;

import pl.poznan.put.pdb.analysis.PdbResidue;

public class InvalidBondRule implements ResidueBondRule {
    @Override
    public boolean areConnected(PdbResidue r1, PdbResidue r2) {
        return false;
    }
}
