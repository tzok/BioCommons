package pl.poznan.put.pdb;

import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

import java.io.Serializable;

/**
 * Class that represents a residue identifier with a known name. In some cases, the name is known
 * only after post-processing e.g. when finding out the name of a residue based on the atom content.
 */
@Value.Immutable
public abstract class PdbNamedResidueIdentifier implements ChainNumberICode, Serializable {
  @Value.Parameter(order = 1)
  public abstract String chainIdentifier();

  @Value.Parameter(order = 2)
  public abstract int residueNumber();

  @Value.Parameter(order = 3)
  public abstract String insertionCode();

  /** @return The one letter name of the residue. */
  @Value.Parameter(order = 4)
  public abstract char oneLetterName();

  @Override
  public final String toString() {
    final String chain = StringUtils.isBlank(chainIdentifier()) ? "" : (chainIdentifier() + '.');
    final String icode = StringUtils.isBlank(insertionCode()) ? "" : insertionCode();
    final String name = (oneLetterName() == ' ') ? "" : Character.toString(oneLetterName());
    return chain + name + residueNumber() + icode;
  }
}
