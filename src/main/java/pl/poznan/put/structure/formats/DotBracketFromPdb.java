package pl.poznan.put.structure.formats;

import pl.poznan.put.pdb.PdbNamedResidueIdentifier;
import pl.poznan.put.structure.BasePair;
import pl.poznan.put.structure.ClassifiedBasePair;
import pl.poznan.put.structure.DotBracketSymbol;
import pl.poznan.put.structure.ImmutableBasePair;

import java.util.List;
import java.util.Set;

/** A dot-bracket structure with correspondence to a 3D structure. */
public interface DotBracketFromPdb extends DotBracket {
  /**
   * Maps the given dot-bracket symbol to its corresponding residue identifier.
   *
   * @param symbol The symbol to look for.
   * @return The named PDB residue identifier.
   */
  PdbNamedResidueIdentifier identifier(final DotBracketSymbol symbol);

  /**
   * Maps the given residue identifier to its corresponding dot-bracket symbol.
   *
   * @param residueIdentifier The residue identifier to look for.
   * @return The dot-bracket symbol.
   */
  DotBracketSymbol symbol(final PdbNamedResidueIdentifier residueIdentifier);

  /**
   * Checks if this structure contains a mapping for the given residue identifier.
   *
   * @param residueIdentifier The residue identifier to check.
   * @return True, if there is a mapping for the given residue identifier.
   */
  boolean contains(final PdbNamedResidueIdentifier residueIdentifier);

  /**
   * Combines strands which are connected via canonical or non-canonical base pairs.
   *
   * @param nonCanonical The list of non-canonical base pairs to take into account.
   * @return The list of dot-bracket structures from the combined strands.
   */
  List<DotBracketFromPdb> combineStrands(List<ClassifiedBasePair> nonCanonical);

  /** @return The set of residue identifiers used in this structure. */
  Set<PdbNamedResidueIdentifier> identifierSet();

  /**
   * Maps a dot-bracket symbol to an instance of base pair.
   *
   * @param symbol A dot-bracket symbol.
   * @return An instance of base pair.
   */
  default BasePair basePair(final DotBracketSymbol symbol) {
    if (pairs().containsKey(symbol)) {
      return ImmutableBasePair.of(identifier(symbol), identifier(pairs().get(symbol)));
    }
    throw new IllegalArgumentException(
        "Cannot create base pair from unpaired nucleotide: " + symbol);
  }
}
