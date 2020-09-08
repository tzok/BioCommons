package pl.poznan.put.pdb.analysis;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Value.Immutable
public abstract class PdbModel implements Serializable, StructureModel {
  public static PdbModel of(final Iterable<? extends PdbAtomLine> atoms) {
    return ImmutablePdbModel.of(
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

  @Value.Lazy
  public List<PdbChain> chains() {
    final Map<String, List<PdbResidue>> chainResidues = new LinkedHashMap<>();
    residues()
        .forEach(
            residue -> {
              chainResidues.putIfAbsent(residue.chainIdentifier(), new ArrayList<>());
              chainResidues.get(residue.chainIdentifier()).add(residue);
            });
    return chainResidues.values().stream()
        .flatMap(residueGroup -> residueGroupToChains(residueGroup).stream())
        .collect(Collectors.toList());
  }

  @Override
  public PdbModel filteredNewInstance(final MoleculeType moleculeType) {
    return ImmutablePdbModel.of(
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

  @Value.Lazy
  public List<PdbResidue> residues() {
    // group atoms by common (chain, number, icode)
    final Map<PdbResidueIdentifier, List<PdbAtomLine>> atomGroups = new LinkedHashMap<>();
    atoms()
        .forEach(
            atom -> {
              atomGroups.putIfAbsent(atom.toResidueIdentifer(), new ArrayList<>());
              atomGroups.get(atom.toResidueIdentifer()).add(atom);
            });

    // create residues out of atom groups and leave only those detected as nucleotides or amino
    // acids
    final Stream<PdbResidue> existingResidueStream =
        atomGroups.values().stream()
            .map(this::atomGroupToResidue)
            .filter(PdbResidue::wasSuccessfullyDetected);

    // create residues out of information about missing residues in the headers
    final Stream<PdbResidue> missingResidueStream =
        missingResidues().stream().map(PdbRemark465Line::toResidue);

    // create a list of residues
    return Stream.concat(existingResidueStream, missingResidueStream)
        .sorted()
        .collect(Collectors.toList());
  }

  @Value.Check
  protected void check() {
    Validate.notEmpty(atoms());
  }

  private PdbModresLine modificationDetails(final PdbResidueIdentifier residueIdentifier) {
    return modifiedResidues().stream()
        .filter(modifiedResidue -> modifiedResidue.toResidueIdentifer().equals(residueIdentifier))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Failed to find information about modification of: " + residueIdentifier));
  }

  private boolean isModified(final PdbResidueIdentifier residueIdentifier) {
    return modifiedResidues().stream()
        .anyMatch(
            modifiedResidue -> modifiedResidue.toResidueIdentifer().equals(residueIdentifier));
  }

  private PdbResidue atomGroupToResidue(final List<? extends PdbAtomLine> residueAtoms) {
    final PdbResidueIdentifier residueIdentifier = residueAtoms.get(0).toResidueIdentifer();
    final boolean isModified = isModified(residueIdentifier);
    final String residueName = residueAtoms.get(0).residueName();
    final String modifiedResidueName =
        isModified ? modificationDetails(residueIdentifier).standardResidueName() : residueName;
    return ImmutablePdbResidue.of(
        residueIdentifier, residueName, modifiedResidueName, residueAtoms, isModified, false);
  }

  private List<PdbChain> residueGroupToChains(final List<? extends PdbResidue> residueGroup) {
    final List<PdbChain> chains = new ArrayList<>();
    final List<Integer> branchingPoints =
        IntStream.range(0, residueGroup.size())
            .filter(i -> chainTerminatedAfter().contains(residueGroup.get(i).toResidueIdentifer()))
            .mapToObj(i -> i)
            .collect(Collectors.toList());

    int begin = 0;
    for (final int branchingPoint : branchingPoints) {
      // move `end` past all missing residues after TER line
      int end = branchingPoint + 1;
      for (; end < residueGroup.size() && residueGroup.get(end).isMissing(); end++)
        ;
      chains.add(
          ImmutablePdbChain.of(
              residueGroup.get(0).chainIdentifier(), residueGroup.subList(begin, end)));
      begin = end;
    }

    if (begin < residueGroup.size()) {
      chains.add(
          ImmutablePdbChain.of(
              residueGroup.get(0).chainIdentifier(),
              residueGroup.subList(begin, residueGroup.size())));
    }

    return chains;
  }
}
