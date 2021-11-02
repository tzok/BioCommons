package pl.poznan.put.structure;

import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.pdb.analysis.ImmutableDefaultResidueCollection;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.ResidueCollection;
import pl.poznan.put.structure.formats.BpSeq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/** A set of methods to analyze 3D data to extract the canonical 2D structure. */
public final class CanonicalStructureExtractor {
  private CanonicalStructureExtractor() {
    super();
    // empty constructor
  }

  /**
   * Extracts the canonical secondary structure from the 3D data and outputs it in BPSEQ format.
   *
   * @param residueCollection A collection of residues to analyze.
   * @return A secondary structure in BPSEQ format.
   */
  public static BpSeq bpSeq(final ResidueCollection residueCollection) {
    final List<PdbResidue> residues =
        residueCollection.residues().stream()
            .filter(
                residue -> residue.residueInformationProvider().moleculeType() == MoleculeType.RNA)
            .collect(Collectors.toList());
    final ResidueCollection collection = ImmutableDefaultResidueCollection.of(residues);
    final Collection<ClassifiedBasePair> basePairs =
        CanonicalStructureExtractor.basePairs(collection);
    return BpSeq.fromBasePairs(collection.namedResidueIdentifiers(), basePairs);
  }

  /*
   * This is just a simple implementation. For a robust solution, see RNApdbee
   * http://rnapdbee.cs.put.poznan.pl
   */
  public static Collection<ClassifiedBasePair> basePairs(
      final ResidueCollection residueCollection) {
    final List<PdbResidue> residues = residueCollection.residues();
    final Collection<ClassifiedBasePair> basePairs = new ArrayList<>();
    final Collection<PdbNamedResidueIdentifier> paired = new HashSet<>();

    for (int i = 0; i < residues.size(); i++) {
      final PdbResidue left = residues.get(i);

      for (int j = i + 1; j < residues.size(); j++) {
        final PdbResidue right = residues.get(j);

        if (BasePair.isCanonicalPair(left, right)) {
          final PdbNamedResidueIdentifier leftId = left.namedResidueIdentifier();
          final PdbNamedResidueIdentifier rightId = right.namedResidueIdentifier();

          if (!paired.contains(leftId) && !paired.contains(rightId)) {
            basePairs.add(ImmutableAnalyzedBasePair.of(ImmutableBasePair.of(leftId, rightId)));
            paired.add(leftId);
            paired.add(rightId);
          }
        }
      }
    }

    return basePairs;
  }
}
