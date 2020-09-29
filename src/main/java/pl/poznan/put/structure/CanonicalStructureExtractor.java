package pl.poznan.put.structure;

import pl.poznan.put.notation.BPh;
import pl.poznan.put.notation.BR;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.pdb.analysis.ImmutableDefaultResidueCollection;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.pdb.analysis.ResidueCollection;
import pl.poznan.put.rna.InteractionType;
import pl.poznan.put.structure.formats.BpSeq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public final class CanonicalStructureExtractor {
  private CanonicalStructureExtractor() {
    super();
    // empty constructor
  }

  public static BpSeq bpSeq(final ResidueCollection residueCollection) {
    final List<PdbResidue> residues =
        residueCollection.residues().stream()
            .filter(
                pdbResidue ->
                    pdbResidue.residueInformationProvider().moleculeType() == MoleculeType.RNA)
            .collect(Collectors.toList());
    final ResidueCollection collection = ImmutableDefaultResidueCollection.of(residues);
    final Collection<ClassifiedBasePair> basePairs =
        CanonicalStructureExtractor.basePairs(collection);
    return BpSeq.fromResidueCollection(collection.namedResidueIdentifiers(), basePairs);
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
      final PdbNamedResidueIdentifier leftId = left.namedResidueIdentifer();

      for (int j = i + 1; j < residues.size(); j++) {
        final PdbResidue right = residues.get(j);
        final PdbNamedResidueIdentifier rightId = right.namedResidueIdentifer();

        if (BasePair.isCanonicalPair(left, right)) {
          final BasePair basePair = new BasePair(leftId, rightId);
          final ClassifiedBasePair classifiedBasePair =
              ModifiableAnalyzedBasePair.create(
                  basePair,
                  InteractionType.BASE_BASE,
                  Saenger.XIX,
                  LeontisWesthof.CWW,
                  BPh.UNKNOWN,
                  BR.UNKNOWN,
                  HelixOrigin.UNKNOWN,
                  false);

          if (!paired.contains(leftId) && !paired.contains(rightId)) {
            basePairs.add(classifiedBasePair);
            paired.add(leftId);
            paired.add(rightId);
          }
        }
      }
    }
    return basePairs;
  }
}
