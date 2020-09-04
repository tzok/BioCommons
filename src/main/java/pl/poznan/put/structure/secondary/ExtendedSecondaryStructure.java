package pl.poznan.put.structure.secondary;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import pl.poznan.put.notation.BPh;
import pl.poznan.put.notation.BR;
import pl.poznan.put.notation.LeontisWesthof;
import pl.poznan.put.notation.Saenger;
import pl.poznan.put.pdb.ImmutablePdbNamedResidueIdentifier;
import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.rna.RNAInteractionType;
import pl.poznan.put.structure.secondary.formats.BpSeq;
import pl.poznan.put.structure.secondary.formats.Converter;
import pl.poznan.put.structure.secondary.formats.DotBracket;
import pl.poznan.put.structure.secondary.formats.InvalidStructureException;
import pl.poznan.put.structure.secondary.formats.LevelByLevelConverter;
import pl.poznan.put.structure.secondary.pseudoknots.elimination.MinGain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

@Data
@Slf4j
public final class ExtendedSecondaryStructure {
  private final String sequence;
  private final Collection<ClassifiedBasePair> basePairs;

  public ExtendedSecondaryStructure(
      final String sequence, final Collection<ClassifiedBasePair> basePairs) {
    super();
    this.sequence = sequence;
    this.basePairs =
        basePairs.stream().filter(ClassifiedBasePair::is5to3).collect(Collectors.toSet());
  }

  /**
   * Creates instance of by reading a set of lines in dot-bracket notation. Each line begins with a
   * Leontis-Westhof notation shortand (e.g. cWW, tSH, etc.), a whitespace, and a dot-bracket. One
   * line contains 'seq' instead of LW notation and it is followed by the sequence. For example:
   *
   * <p>seq AGGGCGGGU
   *
   * <p>cWW (.......)
   *
   * <p>cWH .([{.}]).
   *
   * @param input A string containing input in the format specified above.
   * @return An instance of .
   */
  public static ExtendedSecondaryStructure fromMultilineDotBracket(final String input) {
    final List<ClassifiedBasePair> basePairs = new ArrayList<>();

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
          final char opening = DotBracketSymbol.getMatchingBracket(c);
          if (!stackMap.containsKey(opening)) {
            throw new IllegalArgumentException(
                String.format(
                    "Invalid dot-bracket structure. Closing bracket '%s' at position %d occurred when unexpected",
                    c, i + 1));
          }

          final Stack<Integer> stack = stackMap.get(opening);
          if (stack.empty()) {
            throw new IllegalArgumentException(
                String.format(
                    "Invalid dot-bracket structure. Closing bracket '%s' at position %d occurred when unexpected",
                    c, i + 1));
          }

          final int openingIndex = stack.pop();
          final PdbNamedResidueIdentifier left =
              ImmutablePdbNamedResidueIdentifier.of(
                  "A",
                  openingIndex + 1,
                  " ",
                  sequence.length() > openingIndex ? sequence.charAt(openingIndex) : 'N');
          final PdbNamedResidueIdentifier right =
              ImmutablePdbNamedResidueIdentifier.of(
                  "A", i + 1, " ", sequence.length() > i ? sequence.charAt(i) : 'N');
          final BasePair basePair = new BasePair(left, right);
          final ClassifiedBasePair classifiedBasePair =
              new ClassifiedBasePair(
                  basePair,
                  RNAInteractionType.BASE_BASE,
                  Saenger.UNKNOWN,
                  leontisWesthof,
                  BPh.UNKNOWN,
                  BR.UNKNOWN,
                  HelixOrigin.UNKNOWN);
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
        maxIndex = Integer.max(maxIndex, basePair.getBasePair().getRight().residueNumber());
      }
      sequence = StringUtils.repeat('N', maxIndex);
    }

    return new ExtendedSecondaryStructure(sequence, basePairs);
  }

  public static void main(final String[] args) {
    final String input = "seq ACGUACGUACGU\ncWH (([[..))]]..\ncWW ....((..))..";
    final ExtendedSecondaryStructure secondaryStructure =
        ExtendedSecondaryStructure.fromMultilineDotBracket(input);
    System.out.println(secondaryStructure);
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("seq ").append(sequence).append('\n');

    final Set<LeontisWesthof> set =
        basePairs.stream().map(ClassifiedBasePair::getLeontisWesthof).collect(Collectors.toSet());

    for (final LeontisWesthof leontisWesthof : LeontisWesthof.values()) {
      if ((leontisWesthof != LeontisWesthof.UNKNOWN) && set.contains(leontisWesthof)) {
        for (final DotBracket dotBracket : dotBracketFromBasePairs(leontisWesthof)) {
          builder
              .append(leontisWesthof.getShortName())
              .append(' ')
              .append(dotBracket.getStructure())
              .append('\n');
        }
      }
    }

    return builder.toString();
  }

  private List<DotBracket> dotBracketFromBasePairs(final LeontisWesthof leontisWesthof) {
    try {
      final List<ClassifiedBasePair> filteredBasePairs =
          basePairs.stream()
              .filter(cbp -> RNAInteractionType.BASE_BASE.equals(cbp.getInteractionType()))
              .filter(cbp -> leontisWesthof == cbp.getLeontisWesthof())
              .sorted(Comparator.comparingInt(cbp -> cbp.getBasePair().getLeft().residueNumber()))
              .collect(Collectors.toList());

      final List<DotBracket> result = new ArrayList<>();

      do {
        final Set<ClassifiedBasePair> layer = new LinkedHashSet<>();
        final Set<Integer> usedIndices = new HashSet<>();

        for (final ClassifiedBasePair classifiedBasePair : filteredBasePairs) {
          final BasePair basePair = classifiedBasePair.getBasePair();
          final int left = basePair.getLeft().residueNumber();
          final int right = basePair.getRight().residueNumber();

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
    } catch (final InvalidStructureException e) {
      ExtendedSecondaryStructure.log.error(
          "Failed to generate dot-bracket from list of base pairs", e);
      return Collections.emptyList();
    }
  }

  private DotBracket basePairsToDotBracket(
      final Iterable<? extends ClassifiedBasePair> filteredBasePairs) {
    final List<PdbNamedResidueIdentifier> identifiers = new ArrayList<>();
    final char[] array = sequence.toCharArray();

    for (int i = 0; i < array.length; i++) {
      final PdbNamedResidueIdentifier identifier =
          ImmutablePdbNamedResidueIdentifier.of("A", i + 1, " ", array[i]);
      identifiers.add(identifier);
    }

    final BpSeq bpSeq = BpSeq.fromResidueCollection(identifiers, filteredBasePairs);
    final Converter converter = new LevelByLevelConverter(new MinGain(), 1);
    return converter.convert(bpSeq);
  }
}
