package pl.poznan.put.pdb.analysis;

import org.biojava.nbio.structure.Group;
import org.biojava.nbio.structure.ResidueNumber;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.atom.AtomName;
import pl.poznan.put.pdb.ImmutablePdbResidueIdentifier;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.pdb.PdbResidueIdentifier;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/** A default implementation of a residue (nucleotide or amino acid). */
@Value.Immutable
public abstract class DefaultPdbResidue implements PdbResidue {
  /**
   * Creates an instance of this class from Group object defined in BioJava.
   *
   * @param group A BioJava counterpart.
   * @return An instance of this class with contents equal to those from the BioJava counterpart.
   */
  public static PdbResidue fromBioJavaGroup(final Group group) {
    final ResidueNumber residueNumberObject = group.getResidueNumber();
    final String chainIdentifier = residueNumberObject.getChainName();
    final int residueNumber = residueNumberObject.getSeqNum();
    final String insertionCode =
        (residueNumberObject.getInsCode() == null)
            ? " "
            : Character.toString(residueNumberObject.getInsCode());
    final PdbResidueIdentifier residueIdentifier =
        ImmutablePdbResidueIdentifier.of(chainIdentifier, residueNumber, insertionCode);
    final List<PdbAtomLine> atoms =
        group.getAtoms().stream().map(PdbAtomLine::fromBioJavaAtom).collect(Collectors.toList());

    final String residueName = group.getPDBName();
    return ImmutableDefaultPdbResidue.of(residueIdentifier, residueName, residueName, atoms);
  }

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
    final String insertionCode = identifier().insertionCode();
    return chainIdentifier
        + '.'
        + modifiedResidueName()
        + residueNumber
        + (Objects.equals(" ", insertionCode) ? "" : insertionCode);
  }
}
