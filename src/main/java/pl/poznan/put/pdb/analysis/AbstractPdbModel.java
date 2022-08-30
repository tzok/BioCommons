package pl.poznan.put.pdb.analysis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbRemark465Line;
import pl.poznan.put.pdb.PdbResidueIdentifier;

/** A structure which detects residues from atoms alone and then chains from residues. */
public abstract class AbstractPdbModel implements PdbModel {
  /**
   * Groups together residues in the same chain and repeat that for every chain.
   *
   * @return A list of chains in the structure.
   */
  @Override
  public List<PdbChain> chains() {
    final List<PdbResidue> residues = residues();
    final List<PdbChain> chains = new ArrayList<>();

    List<PdbResidue> group = new ArrayList<>();

    for (int i = 0, size = residues.size(); i < size; i++) {
      final PdbResidue current = residues.get(i);

      if (group.isEmpty()) {
        group.add(current);
        continue;
      }

      final PdbResidue previous = group.get(group.size() - 1);

      // check if two residues are of a different molecule type
      boolean isNewChain =
          previous.residueInformationProvider().moleculeType()
              != current.residueInformationProvider().moleculeType();

      // check if they have a different chain name
      isNewChain = isNewChain || !previous.chainIdentifier().equals(current.chainIdentifier());

      if (previous.isMissing() || current.isMissing()) {
        // if either residue is missing, check only their relative residue numbers
        isNewChain =
            isNewChain
                || ((current.residueNumber() - previous.residueNumber() != 1)
                    && !previous.insertionCode().equals(current.insertionCode()));
      } else {
        // otherwise, check the distance between connecting atoms
        isNewChain = isNewChain || !previous.isConnectedTo(current);
      }

      if (isNewChain) {
        chains.addAll(residueGroupToChains(group));
        group = new ArrayList<>();
      }

      group.add(current);
    }

    if (!group.isEmpty()) {
      chains.addAll(residueGroupToChains(group));
    }

    return chains;
  }

  /**
   * Groups together atoms from the same residue and repeat that for every residue.
   *
   * @return A list of residues in the structure.
   */
  @Override
  public List<PdbResidue> residues() {
    // group atoms by common (chain, number, icode)
    final Map<PdbResidueIdentifier, List<PdbAtomLine>> atomGroups = new LinkedHashMap<>();
    atoms()
        .forEach(
            atom -> {
              atomGroups.putIfAbsent(PdbResidueIdentifier.from(atom), new ArrayList<>());
              atomGroups.get(PdbResidueIdentifier.from(atom)).add(atom);
            });

    // create residues out of atom groups and leave only those detected as nucleotides or amino
    // acids
    final List<PdbResidue> existingResidues =
        atomGroups.values().stream()
            .map(this::atomGroupToResidue)
            .filter(
                residue ->
                    residue.residueInformationProvider().moleculeType() != MoleculeType.UNKNOWN)
            .collect(Collectors.toList());

    // construct a set of non-missing residue identifiers
    final Set<PdbResidueIdentifier> existingIdentifiers =
        existingResidues.stream().map(PdbResidueIdentifier::from).collect(Collectors.toSet());

    // create residues out of information about missing residues in the headers
    final Stream<PdbResidue> missingResidueStream =
        missingResidues().stream()
            .filter(missing -> missing.modelNumber() == modelNumber())
            .filter(missing -> !existingIdentifiers.contains(PdbResidueIdentifier.from(missing)))
            .map(PdbRemark465Line::toResidue);

    // maintain chain order from the input file
    final List<String> order =
        atoms().stream().map(PdbAtomLine::chainIdentifier).distinct().collect(Collectors.toList());

    // create a list of residues
    // the comparator applies chain order, but within a chain it goes back to
    // ChainNumberICode::compareTo in order to put missing residues in correct places
    return Stream.concat(existingResidues.stream(), missingResidueStream)
        .sorted(
            (t, t1) -> {
              if (t.chainIdentifier().equals(t1.chainIdentifier())) return t.compareTo(t1);
              return Integer.compare(
                  order.indexOf(t.chainIdentifier()), order.indexOf(t1.chainIdentifier()));
            })
        .collect(Collectors.toList());
  }

  private PdbResidue atomGroupToResidue(final List<PdbAtomLine> residueAtoms) {
    final PdbResidueIdentifier residueIdentifier = PdbResidueIdentifier.from(residueAtoms.get(0));
    final boolean isModified = isModified(residueIdentifier);
    final String residueName = residueAtoms.get(0).residueName();
    final String modifiedResidueName =
        isModified ? modificationDetails(residueIdentifier).standardResidueName() : residueName;
    return ImmutableDefaultPdbResidue.of(
        residueIdentifier, residueName, modifiedResidueName, residueAtoms);
  }

  private List<PdbChain> residueGroupToChains(final List<PdbResidue> residueGroup) {
    final List<PdbChain> chains = new ArrayList<>();
    final List<Integer> branchingPoints =
        IntStream.range(0, residueGroup.size())
            .filter(
                i ->
                    chainTerminatedAfter().contains(PdbResidueIdentifier.from(residueGroup.get(i))))
            .boxed()
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
