package pl.poznan.put.pdb.analysis;

import org.immutables.value.Value;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbExpdtaLine;
import pl.poznan.put.pdb.PdbHeaderLine;
import pl.poznan.put.pdb.PdbModresLine;
import pl.poznan.put.pdb.PdbRemark2Line;
import pl.poznan.put.pdb.PdbRemark465Line;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.structure.secondary.QuantifiedBasePair;

import java.util.List;
import java.util.Set;

@Value.Immutable
public abstract class CifModel implements StructureModel {
  public static CifModel of(
      final PdbHeaderLine header,
      final PdbExpdtaLine experimentalData,
      final PdbRemark2Line resolution,
      final int modelNumber,
      final Iterable<? extends PdbAtomLine> atoms,
      final Iterable<? extends PdbModresLine> modifiedResidues,
      final Iterable<? extends PdbRemark465Line> missingResidues,
      final String title,
      final Iterable<? extends PdbResidueIdentifier> chainTerminatedAfter,
      final Iterable<? extends QuantifiedBasePair> basePairs) {
    return ImmutableCifModel.of(
        ImmutablePdbModel.of(
            header,
            experimentalData,
            resolution,
            modelNumber,
            atoms,
            modifiedResidues,
            missingResidues,
            title,
            chainTerminatedAfter),
        basePairs);
  }

  @Value.Parameter(order = 1)
  protected abstract PdbModel pdbModel();

  @Value.Parameter(order = 2)
  public abstract List<QuantifiedBasePair> basePairs();

  @Override
  public final PdbHeaderLine header() {
    return pdbModel().header();
  }

  @Override
  public final PdbExpdtaLine experimentalData() {
    return pdbModel().experimentalData();
  }

  @Override
  public final PdbRemark2Line resolution() {
    return pdbModel().resolution();
  }

  @Override
  public final int modelNumber() {
    return pdbModel().modelNumber();
  }

  @Override
  public final List<PdbModresLine> modifiedResidues() {
    return pdbModel().modifiedResidues();
  }

  @Override
  public final List<PdbRemark465Line> missingResidues() {
    return pdbModel().missingResidues();
  }

  @Override
  public final List<PdbAtomLine> atoms() {
    return pdbModel().atoms();
  }

  @Override
  public final List<PdbChain> chains() {
    return pdbModel().chains();
  }

  @Override
  public final List<PdbResidue> residues() {
    return pdbModel().residues();
  }

  @Override
  public final String title() {
    return pdbModel().title();
  }

  @Override
  public final Set<PdbResidueIdentifier> chainTerminatedAfter() {
    return pdbModel().chainTerminatedAfter();
  }

  @Override
  public final CifModel filteredNewInstance(final MoleculeType moleculeType) {
    return ImmutableCifModel.of(
        header(),
        experimentalData(),
        resolution(),
        modelNumber(),
        filteredAtoms(moleculeType),
        modifiedResidues(),
        filteredMissing(moleculeType),
        title(),
        chainTerminatedAfter(),
        basePairs());
  }
}
