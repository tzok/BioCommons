package pl.poznan.put.common;

import pl.poznan.put.pdb.analysis.PdbResidue;

public interface ResidueBondRule {
    boolean areConnected(PdbResidue r1, PdbResidue r2);
}
