package pl.poznan.put.structure.formats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.pdb.ImmutablePdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.rna.InteractionType;
import pl.poznan.put.structure.BasePair;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.DotBracketSymbol;
import pl.poznan.put.structure.ImmutableAnalyzedBasePair;
import pl.poznan.put.structure.ImmutableBasePair;

/** An extended secondary structure, which contains also non-canonical base pairs. */
@Value.Immutable
public abstract class MultiLineDotBracket {
  /**
   * Creates an instance by reading a set of lines in dot-bracket notation. Each line begins with a
   * Leontis-Westhof notation shortand (e.g. cWW, tSH, etc.), a whitespace, and a dot-bracket. One
   * line contains 'seq' instead of LW notation and it is followed by the sequence. For example:
   *
   * <pre>
   * seq AGGGCGGGU
   * cWW (.......)
   * cWH .([{.}]).
   * </pre>
   *
   * @param input A string containing input in the format specified above.
   * @return An instance of this class.
   */
  public static MultiLineDotBracket fromString(final String input) {
    final Collection<ClassifiedBasePair> basePairs = new ArrayList<>();

    String sequence = "";
    int previousLength = -1;

    for (final String line : StringUtils.split(input, '\n')) {
      final String[] tokens = StringUtils.split(line);

      if (tokens.length != 2) {
        throw new IllegalArgumentException(
            "Each line must contain two entities. An identifier (seq, cWW, etc.) and the content "
                + "(sequence or dot-bracket notation). This line fails the check: "
                + line);
      }

      if (previousLength == -1) {
        previousLength = tokens[1].length();
      }

      if (tokens[1].length() != previousLength) {
        throw new IllegalArgumentException(
            "Sequence and all dot-bracket structures must be of equal size. "
                + "This line fails to meet the criterion: "
                + line);
      }
      previousLength = tokens[1].length();

      if ("seq".equalsIgnoreCase(tokens[0])) {
        sequence = tokens[1];
        continue;
      }

      final LeontisWesthof leontisWesthof = LeontisWesthof.fromString(tokens[0]);
      final char[] dotsAndBrackets = tokens[1].toCharArray();

      final Map<Character, Stack<Integer>> stackMap = new HashMap<>();

      for (int i = 0; i < dotsAndBrackets.length; i++) {
        final char c = dotsAndBrackets[i];

        if (c == ',') {
          continue;
        }

        if (DotBracketSymbol.isOpening(c)) {
          if (!stackMap.containsKey(c)) {
            stackMap.put(c, new Stack<>());
          }

          final Stack<Integer> stack = stackMap.get(c);
          stack.push(i);
        } else if (DotBracketSymbol.isClosing(c)) {
          final char opening = DotBracketSymbol.matchingBracket(c);
          if (!stackMap.containsKey(opening)) {
            throw new IllegalArgumentException(
                String.format(
                    "Invalid dot-bracket structure. Closing bracket '%s' at position %d occurred"
                        + " when unexpected",
                    c, i + 1));
          }

          final Stack<Integer> stack = stackMap.get(opening);
          if (stack.empty()) {
            throw new IllegalArgumentException(
                String.format(
                    "Invalid dot-bracket structure. Closing bracket '%s' at position %d occurred"
                        + " when unexpected",
                    c, i + 1));
          }

          final int openingIndex = stack.pop();
          final PdbNamedResidueIdentifier left =
              ImmutablePdbNamedResidueIdentifier.of(
                  "A",
                  openingIndex + 1,
                  Optional.empty(),
                  sequence.length() > openingIndex ? sequence.charAt(openingIndex) : 'N');
          final PdbNamedResidueIdentifier right =
              ImmutablePdbNamedResidueIdentifier.of(
                  "A", i + 1, Optional.empty(), sequence.length() > i ? sequence.charAt(i) : 'N');
          final BasePair basePair = ImmutableBasePair.of(left, right);
          final ClassifiedBasePair classifiedBasePair =
              ImmutableAnalyzedBasePair.of(basePair).withLeontisWesthof(leontisWesthof);
          basePairs.add(classifiedBasePair);
        } else if (c != '.') {
          throw new IllegalArgumentException(
              "Invalid character '" + c + "' in dot-bracket " + tokens[1]);
        }
      }

      for (final Map.Entry<Character, Stack<Integer>> entry : stackMap.entrySet()) {
        if (!entry.getValue().empty()) {
          throw new IllegalArgumentException(
              "Invalid dot-bracket structure. Not all opened brackets have been closed: "
                  + tokens[1]);
        }
      }
    }

    if (StringUtils.isBlank(sequence)) {
      int maxIndex = Integer.MIN_VALUE;
      for (final ClassifiedBasePair basePair : basePairs) {
        maxIndex = Integer.max(maxIndex, basePair.basePair().right().residueNumber());
      }
      sequence = StringUtils.repeat('N', maxIndex);
    }

    return ImmutableMultiLineDotBracket.of(sequence, basePairs);
  }

  /**
   * @return The sequence of nucleotides.
   */
  @Value.Parameter(order = 1)
  public abstract String sequence();

  /**
   * @return The list of base pairs.
   */
  @Value.Parameter(order = 2)
  public abstract Collection<? extends ClassifiedBasePair> basePairs();

  @Override
  public final String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("seq ").append(sequence()).append('\n');

    final Set<LeontisWesthof> set =
        basePairs5to3().stream()
            .map(ClassifiedBasePair::leontisWesthof)
            .collect(Collectors.toSet());

    for (final LeontisWesthof leontisWesthof : LeontisWesthof.values()) {
      if ((leontisWesthof != LeontisWesthof.UNKNOWN) && set.contains(leontisWesthof)) {
        for (final DotBracket dotBracket : dotBracketFromBasePairs(leontisWesthof)) {
          builder
              .append(leontisWesthof.shortName())
              .append(' ')
              .append(dotBracket.structure())
              .append('\n');
        }
      }
    }

    return builder.toString();
  }

  @Value.Lazy
  protected Collection<ClassifiedBasePair> basePairs5to3() {
    return basePairs().stream()
        .filter(basePair -> basePair.basePair().is5to3())
        .collect(Collectors.toSet());
  }

  private List<DotBracket> dotBracketFromBasePairs(final LeontisWesthof leontisWesthof) {
    final List<ClassifiedBasePair> filteredBasePairs =
        basePairs5to3().stream()
            .filter(cbp -> InteractionType.BASE_BASE.equals(cbp.interactionType()))
            .filter(cbp -> leontisWesthof == cbp.leontisWesthof())
            .sorted(Comparator.comparingInt(cbp -> cbp.basePair().left().residueNumber()))
            .collect(Collectors.toList());

    final List<DotBracket> result = new ArrayList<>();

    do {
      final Collection<ClassifiedBasePair> layer = new LinkedHashSet<>();
      final Collection<Integer> usedIndices = new HashSet<>();

      for (final ClassifiedBasePair classifiedBasePair : filteredBasePairs) {
        final BasePair basePair = classifiedBasePair.basePair();
        final int left = basePair.left().residueNumber();
        final int right = basePair.right().residueNumber();

        if (!usedIndices.contains(left) && !usedIndices.contains(right)) {
          layer.add(classifiedBasePair);
          usedIndices.add(left);
          usedIndices.add(right);
        }
      }

      result.add(basePairsToDotBracket(layer));
      filteredBasePairs.removeAll(layer);
    } while (!filteredBasePairs.isEmpty());

    return result;
  }

  private DotBracket basePairsToDotBracket(final Collection<ClassifiedBasePair> filteredBasePairs) {
    final List<PdbNamedResidueIdentifier> identifiers = new ArrayList<>();
    final char[] array = sequence().toCharArray();

    for (int i = 0; i < array.length; i++) {
      final PdbNamedResidueIdentifier identifier =
          ImmutablePdbNamedResidueIdentifier.of("A", i + 1, Optional.empty(), array[i]);
      identifiers.add(identifier);
    }

    final BpSeq bpSeq = BpSeq.fromBasePairs(identifiers, filteredBasePairs);
    final Converter converter = ImmutableDefaultConverter.of();
    return converter.convert(bpSeq);
  }
}
