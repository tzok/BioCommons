package pl.poznan.put.pdb.analysis;

public class InvalidBondRule implements ResidueBondRule {
    @Override
    public boolean areConnected(PdbResidue r1, PdbResidue r2) {
        return false;
    }
}
