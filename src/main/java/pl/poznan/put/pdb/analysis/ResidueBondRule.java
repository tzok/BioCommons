package pl.poznan.put.pdb.analysis;


public interface ResidueBondRule {
    boolean areConnected(PdbResidue r1, PdbResidue r2);
}
