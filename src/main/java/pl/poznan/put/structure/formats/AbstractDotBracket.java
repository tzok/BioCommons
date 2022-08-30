package pl.poznan.put.structure.formats;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import pl.poznan.put.structure.DotBracketSymbol;
import pl.poznan.put.structure.ImmutableDotBracketSymbol;

abstract class AbstractDotBracket implements DotBracket {
  @Override
  public List<DotBracketSymbol> symbols() {
    return IntStream.range(0, sequence().length())
        .mapToObj(i -> ImmutableDotBracketSymbol.of(sequence().charAt(i), structure().charAt(i), i))
        .collect(Collectors.toList());
  }

  protected final List<List<Strand>> candidatesToCombine() {
    final List<List<Strand>> result = new ArrayList<>();
    final List<Strand> toCombine = new ArrayList<>();
    int level = 0;

    for (final Strand strand : strands()) {
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
}
