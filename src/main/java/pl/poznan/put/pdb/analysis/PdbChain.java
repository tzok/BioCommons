package pl.poznan.put.pdb.analysis;

import org.biojava.nbio.structure.Chain;
import org.immutables.value.Value;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class PdbChain implements Comparable<PdbChain>, Serializable, ResidueCollection {
  public static PdbChain fromBioJavaChain(final Chain chain) {
    final List<PdbResidue> residues =
        chain.getAtomGroups().stream()
            .map(PdbResidue::fromBioJavaGroup)
            .collect(Collectors.toList());
    return ImmutablePdbChain.of(chain.getId(), residues);
  }

  @Value.Parameter(order = 1)
  public abstract String identifier();

  @Value.Parameter(order = 2)
  public abstract List<PdbResidue> residues();

  public MoleculeType moleculeType() {
    int rnaCounter = 0;
    int proteinCounter = 0;

    for (final PdbResidue residue : residues()) {
      switch (residue.getMoleculeType()) {
        case PROTEIN:
          proteinCounter += 1;
          break;
        case RNA:
          rnaCounter += 1;
          break;
        case UNKNOWN:
          break;
      }
    }

    return (rnaCounter > proteinCounter) ? MoleculeType.RNA : MoleculeType.PROTEIN;
  }

  @Override
  public final int compareTo(@Nonnull final PdbChain t) {
    return identifier().compareTo(t.identifier());
  }
}
