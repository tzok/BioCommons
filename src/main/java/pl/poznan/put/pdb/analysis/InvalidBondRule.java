package pl.poznan.put.pdb.analysis;

public class InvalidBondRule implements ResidueBondRule {
    @Override
    public final boolean areConnected(
            final PdbResidue r1, final PdbResidue r2) {
        return false;
    }
}
