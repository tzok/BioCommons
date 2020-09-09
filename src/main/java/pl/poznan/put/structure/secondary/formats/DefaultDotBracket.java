package pl.poznan.put.structure.secondary.formats;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.structure.secondary.DotBracketSymbol;
import pl.poznan.put.structure.secondary.ModifiableDotBracketSymbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class DefaultDotBracket implements DotBracket, Serializable {
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDotBracket.class);

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
      Pattern.compile("(>.+\\r?\\n)?([ACGUTRYNacgutryn]+)\\r?\\n([-.()" + "\\[\\]{}<>A-Za-z]+)");

  private static final String SEQUENCE_PATTERN = "[ACGUTRYNacgutryn]+";
  private static final String STRUCTURE_PATTERN = "[-.()\\[\\]{}<>A-Za-z]+";

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
        throw new InvalidStructureException("Invalid dot-bracket string:\n" + data);
      }

      strandNames.add(strandName.replaceFirst("strand_", ""));
      sequenceBuilder.append(sequence);
      structureBuilder.append(structure);

      end += sequence.length();
      pairBeginEnd.add(Pair.of(begin, end));
      begin = end;
    }

    if ((sequenceBuilder.length() == 0) || (structureBuilder.length() == 0)) {
      throw new InvalidStructureException("Cannot parse dot-bracket:\n" + data);
    }

    final DefaultDotBracket dotBracket =
        ImmutableDefaultDotBracket.of(sequenceBuilder.toString(), structureBuilder.toString());

    final List<Strand> strands = new ArrayList<>();
    int index = 0;
    for (final Pair<Integer, Integer> pair : pairBeginEnd) {
      strands.add(
          ImmutableStrandView.of(
              strandNames.get(index), dotBracket, pair.getLeft(), pair.getRight()));
      index += 1;
    }

    return ImmutableDefaultDotBracket.copyOf(dotBracket).withStrands(strands);
  }

  public static List<List<Strand>> candidatesToCombine(final Iterable<? extends Strand> strands) {
    final List<List<Strand>> result = new ArrayList<>();
    final List<Strand> toCombine = new ArrayList<>();
    int level = 0;

    for (final Strand strand : strands) {
      toCombine.add(strand);

      for (final DotBracketSymbol symbol : strand.symbols()) {
        level += symbol.isOpening() ? 1 : 0;
        level -= symbol.isClosing() ? 1 : 0;
      }

      if (level == 0) {
        result.add(new ArrayList<>(toCombine));
        toCombine.clear();
      }
    }

    return result;
  }

  @Value.Parameter(order = 1)
  public abstract String sequence();

  @Value.Parameter(order = 2)
  public abstract String structure();

  @Value.Lazy
  public List<DotBracketSymbol> symbols() {
    final List<DotBracketSymbol> symbols = new ArrayList<>();
    final char[] seq = sequence().toCharArray();
    final char[] str = structure().toCharArray();

    ModifiableDotBracketSymbol current = ModifiableDotBracketSymbol.create(seq[0], str[0], 0);
    for (int i = 1; i < seq.length; i++) {
      final ModifiableDotBracketSymbol next = ModifiableDotBracketSymbol.create(seq[i], str[i], i);
      current.setNext(next);
      next.setPrevious(current);
      symbols.add(current);
      current = next;
    }
    symbols.add(current);

    analyzePairing(symbols);

    return symbols;
  }

  @Value.Default
  @Value.Auxiliary
  public List<Strand> strands() {
    return Collections.singletonList(ImmutableStrandView.of("", this, 0, structure().length()));
  }

  @Override
  public List<DotBracket> combineStrands() {
    return DefaultDotBracket.candidatesToCombine(strands()).stream()
        .map(ImmutableCombinedStrand::of)
        .collect(Collectors.toList());
  }

  @Override
  public int getRealSymbolIndex(final DotBracketSymbol symbol) {
    return symbol.index() + 1;
  }

  @Override
  public final String toString() {
    return ">strand\n" + sequence() + '\n' + structure();
  }

  public final DefaultDotBracket splitStrands(final Ct ct) {
    final Collection<Strand> ctStrands = new ArrayList<>();
    int start = 0;
    int i = 0;

    for (final Ct.ExtendedEntry e : ct.getEntries()) {
      if (e.after() == 0) {
        final Strand strand = ImmutableStrandView.of("", this, start, i + 1);
        ctStrands.add(strand);
        start = i + 1;
      }

      i += 1;
    }

    return ImmutableDefaultDotBracket.copyOf(this).withStrands(ctStrands);
  }

  @Value.Check
  protected void validate() {
    Validate.matchesPattern(sequence(), DefaultDotBracket.SEQUENCE_PATTERN);
    Validate.matchesPattern(structure(), DefaultDotBracket.STRUCTURE_PATTERN);
    Validate.isTrue(
        sequence().length() == structure().length(),
        "Sequence and structure must be of the same length");
  }

  private void analyzePairing(final Iterable<DotBracketSymbol> symbols) {
    final BidiMap<Character, Character> parentheses = new TreeBidiMap<>();
    parentheses.put('(', ')');
    parentheses.put('[', ']');
    parentheses.put('{', '}');
    parentheses.put('<', '>');

    for (final char c : "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()) {
      parentheses.put(c, Character.toLowerCase(c));
    }

    final Map<Character, Stack<DotBracketSymbol>> parenthesesStacks = new HashMap<>();
    for (final char c : parentheses.keySet()) {
      parenthesesStacks.put(c, new Stack<>());
    }

    for (final DotBracketSymbol symbol : symbols) {
      assert symbol instanceof ModifiableDotBracketSymbol;

      final char str = symbol.structure();

      // catch dot '.'
      if ((str == '.') || (str == '-')) {
        ((ModifiableDotBracketSymbol) symbol).setPair(Optional.empty());
        continue;
      }

      // catch opening '(', '[', etc.
      if (parentheses.containsKey(str)) {
        final Stack<DotBracketSymbol> stack = parenthesesStacks.get(str);
        stack.push(symbol);
        continue;
      }

      // catch closing ')', ']', etc.
      if (parentheses.containsValue(str)) {
        final char opening = parentheses.getKey(str);
        final Stack<DotBracketSymbol> stack = parenthesesStacks.get(opening);

        if (stack.empty()) {
          throw new InvalidStructureException(
              "Invalid dot-bracket input:\n" + sequence() + '\n' + structure());
        }

        final DotBracketSymbol pair = stack.pop();
        assert pair instanceof ModifiableDotBracketSymbol;

        ((ModifiableDotBracketSymbol) symbol).setPair(pair);
        ((ModifiableDotBracketSymbol) pair).setPair(symbol);
        continue;
      }

      DefaultDotBracket.LOGGER.error("Unknown symbol in dot-bracket string: {}", str);
    }
  }
}
