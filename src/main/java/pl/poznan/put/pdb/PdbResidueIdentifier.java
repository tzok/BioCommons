package pl.poznan.put.pdb;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;
import pl.poznan.put.pdb.analysis.DefaultPdbResidue;

/**
 * A residue identifier is used only to address a residue in the structure. To work with residue
 * content, see {@link DefaultPdbResidue}.
 */
@Value.Immutable
public abstract class PdbResidueIdentifier implements ChainNumberICode {
  /**
   * Creates an instance of this class from any implementation of {@link ChainNumberICode}.
   *
   * @param chainNumberICode The instance of (chain, number, icode) to convert.
   * @return An object that can be used to address specific residue.
   */
  public static PdbResidueIdentifier from(final ChainNumberICode chainNumberICode) {
    return ImmutablePdbResidueIdentifier.of(
        chainNumberICode.chainIdentifier(),
        chainNumberICode.residueNumber(),
        chainNumberICode.insertionCode());
  }

  @Value.Parameter(order = 1)
  public abstract String chainIdentifier();

  @Value.Parameter(order = 2)
  public abstract int residueNumber();

  @Value.Parameter(order = 3)
  public abstract Optional<String> insertionCode();

  @Override
  public final String toString() {
    final String chain = StringUtils.isBlank(chainIdentifier()) ? "" : (chainIdentifier() + '.');
    final String icode = insertionCode().orElse("");
    return chain + residueNumber() + icode;
  }
}
