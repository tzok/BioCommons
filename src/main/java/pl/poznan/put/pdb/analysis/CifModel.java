package pl.poznan.put.pdb.analysis;

import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbExpdtaLine;
import pl.poznan.put.pdb.PdbHeaderLine;
import pl.poznan.put.pdb.PdbModresLine;
import pl.poznan.put.pdb.PdbRemark2Line;
import pl.poznan.put.pdb.PdbRemark465Line;
import pl.poznan.put.structure.secondary.QuantifiedBasePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CifModel extends PdbModel {
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
      final String title,
      final List<PdbAtomLine> chainTerminatedAfter) {
    super(
        headerLine,
        experimentalDataLine,
        resolutionLine,
        modelNumber,
        atoms,
        modifiedResidues,
        missingResidues,
        title,
        chainTerminatedAfter);
    this.basePairs = new ArrayList<>(basePairs);
  }

  public final Iterable<QuantifiedBasePair> getBasePairs() {
    return Collections.unmodifiableList(basePairs);
  }

  @Override
  public final CifModel filteredNewInstance(final MoleculeType moleculeType) {
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
        title,
        chainTerminatedAfter);
  }
}
