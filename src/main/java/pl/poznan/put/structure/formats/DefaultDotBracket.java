package pl.poznan.put.structure.formats;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.immutables.value.Value;
import pl.poznan.put.structure.DotBracketSymbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** A default implementation of a dot-bracket. */
@Value.Immutable
public abstract class DefaultDotBracket extends AbstractDotBracket implements Serializable {
  static final String SEQUENCE_PATTERN = "[ACGUTRYNacgutryn]+";
  static final String STRUCTURE_PATTERN = "[-.()\\[\\]{}<>A-Za-z]+";

  /*
   * Regex:
   * (>.+\r?\n)?([ACGUTRYNacgutryn]+)\r?\n([-.()\[\]{}<>A-Za-z]+)
   *
   * Groups:
   *  1: strand name with leading '>' or null
   *  2: sequence
   *  3: structure
   */
  private static final Pattern DOTBRACKET_PATTERN =
      Pattern.compile("(>.+\\r?\\n)?([ACGUTRYNacgutryn]+)\\r?\\n([-.()\\[\\]{}<>A-Za-z]+)");

  /**
   * Creates a copy of existing instance and applies the information in CT format to divide strands
   * accordingly.
   *
   * @param input The input dot-bracket structure.
   * @param ct The CT data to take strand information from.
   * @return An instance of this class.
   */
  public static DotBracket copyWithStrands(final DotBracket input, final Ct ct) {
    final DefaultDotBracket dotBracket =
        input instanceof DefaultDotBracket
            ? ImmutableDefaultDotBracket.copyOf((DefaultDotBracket) input)
            : ImmutableDefaultDotBracket.of(input.sequence(), input.structure());

    final List<Ct.ExtendedEntry> entries = new ArrayList<>(ct.entries());
    final List<Integer> ends =
        IntStream.range(0, entries.size())
            .filter(i -> entries.get(i).after() == 0)
            .boxed()
            .collect(Collectors.toList());
    final List<Strand> strands =
        IntStream.range(1, ends.size())
            .mapToObj(
                i -> ImmutableStrandView.of("", dotBracket, ends.get(i - 1) + 1, ends.get(i) + 1))
            .collect(Collectors.toList());
    strands.add(0, ImmutableStrandView.of("", dotBracket, 0, ends.get(0) + 1));

    return ImmutableDefaultDotBracket.copyOf(dotBracket).withStrands(strands);
  }

  /**
   * Parses a string into an instance of this class. The string must be in format:
   *
   * <pre>
   *     &gt;strand_name
   *     ACGU
   *     .().
   * </pre>
   *
   * <p>The first line (strand name) is optional. The three lines might appear multiple times.
   *
   * @param data The string to parse.
   * @return An instance of this class.
   */
  public static DefaultDotBracket fromString(final String data) {
    final Matcher matcher = DefaultDotBracket.DOTBRACKET_PATTERN.matcher(data);

    final Collection<Pair<Integer, Integer>> pairBeginEnd = new ArrayList<>();
    final List<String> strandNames = new ArrayList<>();
    final StringBuilder sequenceBuilder = new StringBuilder(data.length());
    final StringBuilder structureBuilder = new StringBuilder(data.length());
    int begin = 0;
    int end = 0;

    while (matcher.find()) {
      final String strandName =
          (matcher.group(1) != null) ? matcher.group(1).substring(1).trim() : "";
      final String sequence = matcher.group(2);
      final String structure = matcher.group(3);

      if (sequence.length() != structure.length()) {
        throw new IllegalArgumentException("Invalid dot-bracket string:\n" + data);
      }

      strandNames.add(strandName.replaceFirst("strand_", ""));
      sequenceBuilder.append(sequence);
      structureBuilder.append(structure);

      end += sequence.length();
      pairBeginEnd.add(Pair.of(begin, end));
      begin = end;
    }

    if ((sequenceBuilder.length() == 0) || (structureBuilder.length() == 0)) {
      throw new IllegalArgumentException("Cannot parse dot-bracket:\n" + data);
    }

    final DefaultDotBracket dotBracket =
        ImmutableDefaultDotBracket.of(sequenceBuilder.toString(), structureBuilder.toString());

    final Collection<Strand> strands = new ArrayList<>();
    int index = 0;
    for (final Pair<Integer, Integer> pair : pairBeginEnd) {
      strands.add(
          ImmutableStrandView.of(
              strandNames.get(index), dotBracket, pair.getLeft(), pair.getRight()));
      index += 1;
    }

    return ImmutableDefaultDotBracket.copyOf(dotBracket).withStrands(strands);
  }

  @Override
  public final List<DotBracket> combineStrands() {
    return candidatesToCombine().stream()
        .map(ImmutableCombinedStrand::of)
        .collect(Collectors.toList());
  }

  @Override
  @Value.Auxiliary
  @Value.Default
  public List<Strand> strands() {
    return super.strands();
  }

  @Override
  @Value.Parameter(order = 1)
  public abstract String sequence();

  @Override
  @Value.Parameter(order = 2)
  public abstract String structure();

  @Override
  @Value.Lazy
  @Value.Auxiliary
  public Map<DotBracketSymbol, DotBracketSymbol> pairs() {
    return super.pairs();
  }

  @Override
  public final String toString() {
    return ">strand\n" + sequence() + '\n' + structure();
  }

  @Value.Check
  protected void validate() {
    Validate.matchesPattern(sequence(), DefaultDotBracket.SEQUENCE_PATTERN);
    Validate.matchesPattern(structure(), DefaultDotBracket.STRUCTURE_PATTERN);

    Validate.isTrue(
        sequence().length() == structure().length(),
        "Sequence and structure must be of the same length");
  }
}
