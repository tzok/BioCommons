package pl.poznan.put.pdb;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;
import pl.poznan.put.pdb.analysis.DefaultPdbResidue;

import java.io.Serializable;

/**
 * A residue identifier is used only to address a residue in the structure. To work with residue
 * content, see {@link DefaultPdbResidue}.
 */
@Value.Immutable
public abstract class PdbResidueIdentifier implements ChainNumberICode, Serializable {
  /**
   * Creates an instance of this class from any implementation of {@link ChainNumberICode}.
   *
   * @return An object that can be used to address specific residue.
   */
  public static PdbResidueIdentifier from(final ChainNumberICode chainNumberICode) {
    return ImmutablePdbResidueIdentifier.of(
        chainNumberICode.chainIdentifier(),
        chainNumberICode.residueNumber(),
        chainNumberICode.insertionCode());
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
  public final String toString() {
    final String chain = StringUtils.isBlank(chainIdentifier()) ? "" : (chainIdentifier() + '.');
    final String icode = StringUtils.isBlank(insertionCode()) ? "" : insertionCode();
    return chain + residueNumber() + icode;
  }
}
