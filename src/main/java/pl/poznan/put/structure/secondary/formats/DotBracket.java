package pl.poznan.put.structure.secondary.formats;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import org.apache.commons.lang3.tuple.Pair;
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

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Slf4j
public class DotBracket implements DotBracketInterface, Serializable {
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
  private static final Pattern SEQUENCE_PATTERN = Pattern.compile("[ACGUTRYNacgutryn]+");
  private static final Pattern STRUCTURE_PATTERN = Pattern.compile("[-.()\\[\\]{}<>A-Za-z]+");
  final List<Strand> strands = new ArrayList<>();
  final List<ModifiableDotBracketSymbol> symbols = new ArrayList<>();

  @EqualsAndHashCode.Include private final String sequence;
  @EqualsAndHashCode.Include private final String structure;

  public DotBracket(final String sequence, final String structure) {
    super();
    this.sequence = sequence;
    this.structure = structure;

    if (!DotBracket.SEQUENCE_PATTERN.matcher(sequence).matches()
        || !DotBracket.STRUCTURE_PATTERN.matcher(structure).matches()) {
      throw new InvalidStructureException("Invalid dot-bracket:\n" + sequence + '\n' + structure);
    }

    buildSymbolList();
    analyzePairing();

    strands.add(ImmutableStrandView.of("", this, 0, structure.length()));
  }

  public static DotBracket fromString(final String data) {
    final Matcher matcher = DotBracket.DOTBRACKET_PATTERN.matcher(data);

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

    final DotBracket dotBracket =
        new DotBracket(sequenceBuilder.toString(), structureBuilder.toString());
    dotBracket.strands.clear();

    int index = 0;
    for (final Pair<Integer, Integer> pair : pairBeginEnd) {
      dotBracket.strands.add(
          ImmutableStrandView.of(
              strandNames.get(index), dotBracket, pair.getLeft(), pair.getRight()));
      index += 1;
    }

    return dotBracket;
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

  @Override
  public final String toString() {
    return ">strand\n" + sequence + '\n' + structure;
  }

  @Override
  public final String getSequence() {
    return sequence;
  }

  @Override
  public final String getStructure() {
    return structure;
  }

  @Override
  public final List<DotBracketSymbol> getSymbols() {
    return Collections.unmodifiableList(symbols);
  }

  @Override
  public final DotBracketSymbol getSymbol(final int index) {
    assert index < symbols.size();
    return symbols.get(index);
  }

  @Override
  public final String toStringWithStrands() {
    return strands.stream()
        .map(strand -> String.valueOf(strand) + '\n')
        .collect(Collectors.joining());
  }

  @Override
  public final List<Strand> getStrands() {
    return Collections.unmodifiableList(strands);
  }

  @Override
  public List<? extends CombinedStrand> combineStrands() {
    return DotBracket.candidatesToCombine(strands).stream()
        .map(CombinedStrand::new)
        .collect(Collectors.toList());
  }

  @Override
  public int getRealSymbolIndex(final DotBracketSymbol symbol) {
    return symbol.index() + 1;
  }

  public final int getLength() {
    return structure.length();
  }

  public final int getStrandCount() {
    return strands.size();
  }

  public final void splitStrands(final Ct ct) {
    strands.clear();

    int start = 0;
    int i = 0;

    for (final Ct.ExtendedEntry e : ct.getEntries()) {
      if (e.getAfter() == 0) {
        final Strand strand = ImmutableStrandView.of("", this, start, i + 1);
        strands.add(strand);
        start = i + 1;
      }

      i += 1;
    }
  }

  private void buildSymbolList() {
    final char[] seq = sequence.toCharArray();
    final char[] str = structure.toCharArray();
    assert seq.length == str.length;

    ModifiableDotBracketSymbol current = ModifiableDotBracketSymbol.create(seq[0], str[0], 0);

    for (int i = 1; i < seq.length; i++) {
      final ModifiableDotBracketSymbol next = ModifiableDotBracketSymbol.create(seq[i], str[i], i);
      current.setNext(next);
      next.setPrevious(current);
      symbols.add(current);
      current = next;
    }
    symbols.add(current);

    assert symbols.size() == seq.length;
  }

  private void analyzePairing() {
    final BidiMap<Character, Character> parentheses = new TreeBidiMap<>();
    parentheses.put('(', ')');
    parentheses.put('[', ']');
    parentheses.put('{', '}');
    parentheses.put('<', '>');

    for (final char c : "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()) {
      parentheses.put(c, Character.toLowerCase(c));
    }

    final Map<Character, Stack<ModifiableDotBracketSymbol>> parenthesesStacks = new HashMap<>();
    for (final char c : parentheses.keySet()) {
      parenthesesStacks.put(c, new Stack<>());
    }

    for (final ModifiableDotBracketSymbol symbol : symbols) {
      final char str = symbol.structure();

      // catch dot '.'
      if ((str == '.') || (str == '-')) {
        symbol.setPair(Optional.empty());
        continue;
      }

      // catch opening '(', '[', etc.
      if (parentheses.containsKey(str)) {
        final Stack<ModifiableDotBracketSymbol> stack = parenthesesStacks.get(str);
        stack.push(symbol);
        continue;
      }

      // catch closing ')', ']', etc.
      if (parentheses.containsValue(str)) {
        final char opening = parentheses.getKey(str);
        final Stack<ModifiableDotBracketSymbol> stack = parenthesesStacks.get(opening);

        if (stack.empty()) {
          throw new InvalidStructureException(
              "Invalid dot-bracket input:\n" + sequence + '\n' + structure);
        }

        final ModifiableDotBracketSymbol pair = stack.pop();
        symbol.setPair(pair);
        pair.setPair(symbol);
        continue;
      }

      DotBracket.log.error("Unknown symbol in dot-bracket string: {}", str);
    }
  }
}
