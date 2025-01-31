package pl.poznan.put.pdb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

/**
 * Class that represents a residue identifier with a known name. In some cases, the name is known
 * only after post-processing e.g. when finding out the name of a residue based on the atom content.
 */
@Value.Immutable
@JsonSerialize(as = ImmutablePdbNamedResidueIdentifier.class)
@JsonDeserialize(as = ImmutablePdbNamedResidueIdentifier.class)
public abstract class PdbNamedResidueIdentifier implements ChainNumberICode, Serializable {
  @Value.Parameter(order = 1)
  public abstract String chainIdentifier();

  @Value.Parameter(order = 2)
  public abstract int residueNumber();

  @Value.Parameter(order = 3)
  public abstract Optional<String> insertionCode();

  /**
   * @return The one letter name of the residue.
   */
  @Value.Parameter(order = 4)
  public abstract char oneLetterName();

  /**
   * @return An instance of identifier without the oneLetterName field.
   */
  public final PdbResidueIdentifier toResidueIdentifier() {
    return ImmutablePdbResidueIdentifier.from(this);
  }

  @Override
  public final String toString() {
    final String chain = StringUtils.isBlank(chainIdentifier()) ? "" : (chainIdentifier() + '.');
    final String icode = insertionCode().orElse("");
    final String name = (oneLetterName() == ' ') ? "" : Character.toString(oneLetterName());
    return chain + name + residueNumber() + icode;
  }
}
