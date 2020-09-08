package pl.poznan.put.pdb;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A residue identifier is used only to address a residue in the structure. To work with residue
 * content, see {@link pl.poznan.put.pdb.analysis.PdbResidue}.
 */
@Value.Immutable
public abstract class PdbResidueIdentifier
    implements ChainNumberICode, Comparable<PdbResidueIdentifier>, Serializable {
  @Override
  public final String toString() {
    final String chain = StringUtils.isBlank(chainIdentifier()) ? "" : (chainIdentifier() + '.');
    final String icode = StringUtils.isBlank(insertionCode()) ? "" : insertionCode();
    return chain + residueNumber() + icode;
  }

  /** @return The value of the {@code chainIdentifier} attribute */
  @Value.Parameter(order = 1)
  public abstract String chainIdentifier();

  /** @return The value of the {@code residueNumber} attribute */
  @Value.Parameter(order = 2)
  public abstract int residueNumber();

  /** @return The value of the {@code insertionCode} attribute */
  @Value.Parameter(order = 3)
  public abstract String insertionCode();

  @Override
  public final int compareTo(@Nonnull final PdbResidueIdentifier t) {
    if (!Objects.equals(chainIdentifier(), t.chainIdentifier())) {
      return chainIdentifier().compareTo(t.chainIdentifier());
    }

    if (residueNumber() != t.residueNumber()) {
      return (residueNumber() < t.residueNumber()) ? -1 : 1;
    }

    return insertionCode().compareTo(t.insertionCode());
  }
}
