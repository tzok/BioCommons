package pl.poznan.put.structure.formats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import pl.poznan.put.structure.DotBracketSymbol;

/** A continuous segment of residues. It might span the whole chain in PDB or just its fragments. */
public interface Strand extends DotBracket {
  /**
   * @return The name of the strand.
   */
  String name();

  /**
   * @return The missing residues at 5' end.
   */
  default TerminalMissing missingBegin() {
    final List<DotBracketSymbol> missing = new ArrayList<>();
    for (final DotBracketSymbol symbol : symbols()) {
      if (!symbol.isMissing()) {
        break;
      }
      missing.add(symbol);
    }
    return ImmutableTerminalMissing.of(missing);
  }

  /**
   * @return The missing residues at 3' end.
   */
  default TerminalMissing missingEnd() {
    final List<DotBracketSymbol> missing = new ArrayList<>();
    for (int i = symbols().size() - 1; i >= 0; i--) {
      final DotBracketSymbol symbol = symbols().get(i);
      if (!symbol.isMissing()) {
        break;
      }
      missing.add(symbol);
    }
    return ImmutableTerminalMissing.of(missing);
  }

  /**
   * Prepares description of strand in RNAComposer format. The format has 5 elements: index-from,
   * index-to, sequence, structure, RY-sequence.
   *
   * @return A description of strand in RNAComposer format.
   */
  default String description() {
    return String.format(
        "%d %d %s %s %s",
        symbols().get(0).index(),
        symbols().get(symbols().size() - 1).index(),
        sequence(),
        structure(),
        sequenceRY());
  }

  /**
   * @return The index of the first residue in this strand.
   */
  default int begin() {
    return symbols().isEmpty() ? 1 : symbols().get(0).index();
  }

  /**
   * @return The index of the last residue in this strand.
   */
  default int end() {
    return symbols().isEmpty() ? 1 : symbols().get(symbols().size() - 1).index();
  }

  /**
   * Checks if this strand is "single strand" which means that neither of its base pairs starts and
   * ends in this strand.
   *
   * @return True if there is no base-pair inside of this strand. An opening or closing bracket is
   *     allowed as long as it points somewhere outside this strand.
   */
  default boolean isSingleStrand() {
    final Map<DotBracketSymbol, DotBracketSymbol> pairs = pairs();
    return symbols().stream()
        .filter(DotBracketSymbol::isPairing)
        .map(pairs::get)
        .noneMatch(symbol -> symbols().contains(symbol));
  }

  /**
   * @return A sequence of R (instead of A and G) and Y (instead of C, U or T).
   */
  default String sequenceRY() {
    return sequence()
        .chars()
        .mapToObj(i -> (char) i)
        .map(character -> character == 'A' || character == 'G' ? "R" : "Y")
        .collect(Collectors.joining());
  }
}
