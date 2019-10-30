package pl.poznan.put.sequence.alignment;

import lombok.extern.slf4j.Slf4j;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.core.alignment.matrices.SimpleSubstitutionMatrix;
import org.biojava.nbio.core.alignment.matrices.SubstitutionMatrixHelper;
import org.biojava.nbio.core.alignment.template.AlignedSequence;
import org.biojava.nbio.core.alignment.template.Profile;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.RNASequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.core.sequence.compound.NucleotideCompound;
import org.biojava.nbio.core.sequence.compound.RNACompoundSet;
import org.biojava.nbio.core.sequence.template.AbstractCompound;
import org.biojava.nbio.core.sequence.template.AbstractSequence;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbCompactFragment;
import pl.poznan.put.utility.ResourcesHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public final class SequenceAligner {
  private final List<PdbCompactFragment> fragments;
  private final boolean isGlobal;
  private final MoleculeType moleculeType;
  private final Alignments.PairwiseSequenceScorerType type;
  private final SubstitutionMatrix<? extends AbstractCompound> substitutionMatrix;

  public SequenceAligner(final List<PdbCompactFragment> fragments, final boolean isGlobal) {
    super();
    this.fragments = new ArrayList<>(fragments);
    this.isGlobal = isGlobal;

    moleculeType = fragments.get(0).getMoleculeType();
    type =
        isGlobal
            ? Alignments.PairwiseSequenceScorerType.GLOBAL
            : Alignments.PairwiseSequenceScorerType.LOCAL;
    substitutionMatrix =
        (moleculeType == MoleculeType.RNA)
            ? SequenceAligner.getRNASubstitutionMatrix()
            : SequenceAligner.getProteinSubstitutionMatrix();
  }

  private static SubstitutionMatrix<NucleotideCompound> getRNASubstitutionMatrix() {
    try {
      final String matrixInput = ResourcesHelper.loadResource("NUC44.txt");
      return new SimpleSubstitutionMatrix<>(
          RNACompoundSet.getRNACompoundSet(), matrixInput, "NUC44");
    } catch (final IOException e) {
      SequenceAligner.log.error("Failed to load substitution matrix for RNA", e);
    }

    // warning, the default will not work with MSA for RNAs!
    return SubstitutionMatrixHelper.getNuc4_4();
  }

  private static SubstitutionMatrix<AminoAcidCompound> getProteinSubstitutionMatrix() {
    return SubstitutionMatrixHelper.getBlosum62();
  }

  @SuppressWarnings({"rawtypes"})
  public SequenceAlignment align() throws CompoundNotFoundException {
    if (fragments.isEmpty()) {
      return new SequenceAlignment(isGlobal, "");
    }

    final List<AbstractSequence> sequences = new ArrayList<>();
    final Map<AbstractSequence, PdbCompactFragment> mapSequenceName = new HashMap<>();

    for (final PdbCompactFragment fragment : fragments) {
      final String fragmentSequence = fragment.toSequence();
      final AbstractSequence sequence;

      sequence =
          moleculeType == MoleculeType.RNA
              ? new RNASequence(fragmentSequence.replace('T', 'U'))
              : new ProteinSequence(fragmentSequence);

      sequences.add(sequence);
      mapSequenceName.put(sequence, fragment);
    }

    final Profile profile =
        Alignments.getMultipleSequenceAlignment(sequences, substitutionMatrix, type);

    /*
     * Convert every sequence into an array of characters
     */
    final List<? extends AlignedSequence> alignedSequences = profile.getAlignedSequences();
    final char[][] sequencesAsChars = new char[alignedSequences.size()][];

    for (int i = 0; i < alignedSequences.size(); i++) {
      sequencesAsChars[i] = alignedSequences.get(i).toString().toCharArray();
      assert sequencesAsChars[i].length == sequencesAsChars[0].length;
    }

    /*
     * Format alignment to clustalw
     */
    final StringBuilder builder = new StringBuilder();

    for (int i = 0; i < sequencesAsChars[0].length; i += 60) {
      final char[][] copy = new char[alignedSequences.size()][];

      for (int j = 0; j < alignedSequences.size(); j++) {
        copy[j] =
            Arrays.copyOfRange(
                sequencesAsChars[j], i, Math.min(i + 60, sequencesAsChars[j].length));

        final AlignedSequence alignedSequence = alignedSequences.get(j);
        final AbstractSequence sequence = (AbstractSequence) alignedSequence.getOriginalSequence();

        final PdbCompactFragment fragment = mapSequenceName.get(sequence);
        String name = fragment.getName();
        name = name.substring(0, Math.min(name.length(), 11));

        builder.append(String.format("%-12s", name));
        builder.append(copy[j]);
        builder.append('\n');
      }

      builder.append("            ");

      for (int k = 0; k < copy[0].length; k++) {
        boolean flag = true;
        for (int j = 0; j < alignedSequences.size(); j++) {
          if (copy[j][k] != copy[0][k]) {
            flag = false;
            break;
          }
        }
        builder.append(flag ? '*' : ' ');
      }
      builder.append("\n\n");
    }

    final String alignment = builder.toString();
    return new SequenceAlignment(isGlobal, alignment);
  }
}
