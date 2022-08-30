package pl.poznan.put.pdb.analysis;

import java.util.List;
import pl.poznan.put.structure.QuantifiedBasePair;

/** A structure parsed from an mmCIF file. */
public interface CifModel extends PdbModel {
  /**
   * @return The list of base pairs as parsed from mmCIF file.
   */
  List<QuantifiedBasePair> basePairs();
}
