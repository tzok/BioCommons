package pl.poznan.put.pdb.analysis;

import java.util.List;
import java.util.Set;
import org.immutables.value.Value;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbResidueIdentifier;

/** A default implementation of a residue (nucleotide or amino acid). */
@Value.Immutable
public abstract class DefaultPdbResidue implements PdbResidue {
  @Override
  @Value.Parameter(order = 1)
  public abstract PdbResidueIdentifier identifier();

  @Override
  @Value.Parameter(order = 2)
  public abstract String standardResidueName();

  @Override
  @Value.Parameter(order = 3)
  public abstract String modifiedResidueName();

  @Override
  @Value.Parameter(order = 4)
  public abstract List<PdbAtomLine> atoms();

  @Override
  @Value.Lazy
  public ResidueInformationProvider residueInformationProvider() {
    return PdbResidue.super.residueInformationProvider();
  }

  @Override
  @Value.Lazy
  public Set<AtomName> atomNames() {
    return PdbResidue.super.atomNames();
  }

  @Override
  @Value.Lazy
  public boolean hasAllHeavyAtoms() {
    return PdbResidue.super.hasAllHeavyAtoms();
  }

  @Override
  public final String toString() {
    final String chainIdentifier = identifier().chainIdentifier();
    final int residueNumber = identifier().residueNumber();
    final String insertionCode = identifier().insertionCode().orElse("");
    return chainIdentifier + '.' + modifiedResidueName() + residueNumber + insertionCode;
  }
}
