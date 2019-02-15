package pl.poznan.put.pdb.analysis;

import pl.poznan.put.pdb.*;
import pl.poznan.put.structure.secondary.QuantifiedBasePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CifModel extends PdbModel {
  private static final long serialVersionUID = 7057234621953328374L;
  private final List<QuantifiedBasePair> basePairs;

  public CifModel(
      final PdbHeaderLine headerLine,
      final PdbExpdtaLine experimentalDataLine,
      final PdbRemark2Line resolutionLine,
      final int modelNumber,
      final List<PdbAtomLine> atoms,
      final List<PdbModresLine> modifiedResidues,
      final List<PdbRemark465Line> missingResidues,
      final List<QuantifiedBasePair> basePairs,
      final String title)
      throws PdbParsingException {
    super(
        headerLine,
        experimentalDataLine,
        resolutionLine,
        modelNumber,
        atoms,
        modifiedResidues,
        missingResidues,
        title);
    this.basePairs = new ArrayList<>(basePairs);
  }

  public final Iterable<QuantifiedBasePair> getBasePairs() {
    return Collections.unmodifiableList(basePairs);
  }

  @Override
  public final CifModel filteredNewInstance(final MoleculeType moleculeType)
      throws PdbParsingException {
    final List<PdbAtomLine> filteredAtoms = filterAtoms(moleculeType);
    final List<PdbRemark465Line> filteredMissing = filterMissing(moleculeType);
    return new CifModel(
        headerLine,
        experimentalDataLine,
        resolutionLine,
        modelNumber,
        filteredAtoms,
        modifiedResidues,
        filteredMissing,
        basePairs,
        title);
  }
}
