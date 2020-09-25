package pl.poznan.put.pdb.analysis;

import pl.poznan.put.structure.QuantifiedBasePair;

import java.util.List;

/** A structure parsed from an mmCIF file. */
public interface CifModel extends PdbModel {
  /** @return The list of base pairs as parsed from mmCIF file. */
  List<QuantifiedBasePair> basePairs();
}
