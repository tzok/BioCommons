package pl.poznan.put.structure.secondary.formats;

import java.util.List;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

public interface DotBracketInterface {
  String getSequence();

  String getStructure();

  List<DotBracketSymbol> getSymbols();

  DotBracketSymbol getSymbol(int index);

  String toStringWithStrands();

  List<? extends CombinedStrand> combineStrands();
}
