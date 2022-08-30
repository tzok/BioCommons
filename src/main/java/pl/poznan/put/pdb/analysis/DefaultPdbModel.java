package pl.poznan.put.pdb.analysis;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;
import org.immutables.value.Value;
import pl.poznan.put.pdb.ImmutablePdbExpdtaLine;
import pl.poznan.put.pdb.ImmutablePdbHeaderLine;
import pl.poznan.put.pdb.ImmutablePdbRemark2Line;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbExpdtaLine;
import pl.poznan.put.pdb.PdbHeaderLine;
import pl.poznan.put.pdb.PdbModresLine;
import pl.poznan.put.pdb.PdbRemark2Line;
import pl.poznan.put.pdb.PdbRemark465Line;
import pl.poznan.put.pdb.PdbResidueIdentifier;

/** A default implementation of a structure parsed from a PDB file. */
@Value.Immutable
public abstract class DefaultPdbModel extends AbstractPdbModel {
  /**
   * Creates an instance of this class with just a list of atoms and all other fields set to default
   * values or empty.
   *
   * @param atoms A list of atoms.
   * @return An instance of a PdbModel without a header, experimental data information, missing or
   *     modified residues.
   */
  public static PdbModel of(final Iterable<PdbAtomLine> atoms) {
    return ImmutableDefaultPdbModel.of(
        ImmutablePdbHeaderLine.of("", new Date(0L), ""),
        ImmutablePdbExpdtaLine.of(Collections.emptyList()),
        ImmutablePdbRemark2Line.of(Double.NaN),
        1,
        atoms,
        Collections.emptyList(),
        Collections.emptyList(),
        "",
        Collections.emptyList());
  }

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
  public PdbModel filteredNewInstance(final MoleculeType moleculeType) {
    return ImmutableDefaultPdbModel.of(
        header(),
        experimentalData(),
        resolution(),
        modelNumber(),
        filteredAtoms(moleculeType),
        modifiedResidues(),
        filteredMissing(moleculeType),
        title(),
        chainTerminatedAfter());
  }

  @Override
  @Value.Lazy
  public List<PdbChain> chains() {
    return super.chains();
  }

  @Override
  @Value.Lazy
  public List<PdbResidue> residues() {
    return super.residues();
  }

  @Value.Check
  protected DefaultPdbModel normalize() {
    Validate.notEmpty(atoms());

    final List<PdbRemark465Line> filteredMissingResidues =
        missingResidues().stream()
            .filter(missing -> missing.modelNumber() == modelNumber())
            .collect(Collectors.toList());

    if (filteredMissingResidues.size() == missingResidues().size()) {
      return this;
    }

    return ImmutableDefaultPdbModel.of(
        header(),
        experimentalData(),
        resolution(),
        modelNumber(),
        atoms(),
        modifiedResidues(),
        filteredMissingResidues,
        title(),
        chainTerminatedAfter());
  }
}
