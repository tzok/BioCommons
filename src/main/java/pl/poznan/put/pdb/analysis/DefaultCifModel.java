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

/** A default implementation of a structure parsed from an mmCIF file. */
@Value.Immutable
public abstract class DefaultCifModel extends AbstractPdbModel implements CifModel {
  @Override
  @Value.Parameter(order = 1)
  @Value.Auxiliary
  public abstract PdbHeaderLine header();

  @Override
  @Value.Parameter(order = 2)
  @Value.Auxiliary
  public abstract PdbExpdtaLine experimentalData();

  @Override
  @Value.Parameter(order = 3)
  @Value.Auxiliary
  public abstract PdbRemark2Line resolution();

  @Override
  @Value.Parameter(order = 4)
  @Value.Auxiliary
  public abstract int modelNumber();

  @Override
  @Value.Parameter(order = 5)
  public abstract List<PdbAtomLine> atoms();

  @Override
  @Value.Parameter(order = 6)
  @Value.Auxiliary
  public abstract List<PdbModresLine> modifiedResidues();

  @Override
  @Value.Parameter(order = 7)
  @Value.Auxiliary
  public abstract List<PdbRemark465Line> missingResidues();

  @Override
  @Value.Parameter(order = 8)
  @Value.Auxiliary
  public abstract String title();

  @Override
  @Value.Parameter(order = 9)
  @Value.Auxiliary
  public abstract Set<PdbResidueIdentifier> chainTerminatedAfter();

  @Override
  public final CifModel filteredNewInstance(final MoleculeType moleculeType) {
    return ImmutableDefaultCifModel.of(
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

  @Override
  @Value.Parameter(order = 10)
  @Value.Auxiliary
  public abstract List<QuantifiedBasePair> basePairs();

  @Override
  @Value.Lazy
  public List<SingleTypedResidueCollection> chains() {
    return super.chains();
  }

  @Override
  @Value.Lazy
  public List<PdbResidue> residues() {
    return super.residues();
  }
}
