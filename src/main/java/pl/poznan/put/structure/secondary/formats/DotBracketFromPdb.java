package pl.poznan.put.structure.secondary.formats;

import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.structure.secondary.ClassifiedBasePair;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

import java.util.List;

public interface DotBracketFromPdb extends DotBracket {
  PdbNamedResidueIdentifier getResidueIdentifier(final DotBracketSymbol symbol);

  DotBracketSymbol getSymbol(final PdbNamedResidueIdentifier residueIdentifier);

  boolean contains(final PdbNamedResidueIdentifier residueIdentifier);

  List<CombinedStrandFromPdb> combineStrands(List<ClassifiedBasePair> availableNonCanonical);
}
