package pl.poznan.put.structure.secondary.formats;

import pl.poznan.put.structure.secondary.DotBracketSymbol;

import java.util.List;

public interface DotBracketInterface {
  String getSequence();

  String getStructure();

  List<DotBracketSymbol> getSymbols();

  DotBracketSymbol getSymbol(int index);

  String toStringWithStrands();

  List<Strand> getStrands();

  List<? extends CombinedStrand> combineStrands();

  /**
   * Return *real* index of a dot-bracket symbol. The index can reflect PDB residue number or other
   * data source.
   *
   * @param symbol Dot-bracket symbol for which a real index is sought.
   * @return An index which reflects the numbering in real structure (e.g. PDB).
   */
  int getRealSymbolIndex(DotBracketSymbol symbol);
}
