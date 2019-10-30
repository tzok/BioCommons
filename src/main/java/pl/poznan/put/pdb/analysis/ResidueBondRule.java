package pl.poznan.put.pdb.analysis;

import java.io.Serializable;

@FunctionalInterface
interface ResidueBondRule extends Serializable {
  boolean areConnected(PdbResidue r1, PdbResidue r2);
}
