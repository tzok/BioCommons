package pl.poznan.put.pdb.analysis;

import java.io.Serializable;

public interface ResidueBondRule extends Serializable {
  boolean areConnected(PdbResidue r1, PdbResidue r2);
}
