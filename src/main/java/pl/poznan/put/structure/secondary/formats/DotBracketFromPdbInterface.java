package pl.poznan.put.structure.secondary.formats;

import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.structure.secondary.ClassifiedBasePair;
import pl.poznan.put.structure.secondary.DotBracketSymbol;

import java.util.List;

interface DotBracketFromPdbInterface extends DotBracketInterface {
  PdbResidueIdentifier getResidueIdentifier(final DotBracketSymbol symbol);

  DotBracketSymbol getSymbol(final PdbResidueIdentifier residueIdentifier);

  boolean contains(final PdbResidueIdentifier residueIdentifier);

  List<CombinedStrandFromPdb> combineStrands(
      List<? extends ClassifiedBasePair> availableNonCanonical);
}
