package pl.poznan.put.structure.formats;

import pl.poznan.put.structure.DotBracketSymbol;
import pl.poznan.put.structure.ImmutableDotBracketSymbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractCombinedStrand implements DotBracket {
  protected abstract List<Strand> inputStrands();

  @Override
  public List<DotBracketSymbol> symbols() {
    return strands().stream()
        .map(Strand::symbols)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  @Override
  public List<Strand> strands() {
    final List<Strand> strands = new ArrayList<>();
    int i = 0;

    for (final Strand strand : inputStrands()) {
      final List<DotBracketSymbol> renumbered = new ArrayList<>(strand.symbols().size());

      for (final DotBracketSymbol symbol : strand.symbols()) {
        renumbered.add(ImmutableDotBracketSymbol.copyOf(symbol).withIndex(i));
        i++;
      }

      strands.add(ImmutableDefaultStrand.of(strand.name(), renumbered));
    }

    return strands;
  }
}
