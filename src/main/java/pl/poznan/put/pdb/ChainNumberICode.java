package pl.poznan.put.pdb;

import java.io.Serializable;
import java.util.Optional;
import org.apache.commons.lang3.builder.CompareToBuilder;

/**
 * A set of methods that allow to address a residue by its chain name, residue number and insertion
 * code.
 */
public interface ChainNumberICode extends Comparable<ChainNumberICode>, Serializable {
  /**
   * @return The identifier of the chain a residue belongs to.
   */
  String chainIdentifier();

  /**
   * @return The number of a residue in the chain.
   */
  int residueNumber();

  /**
   * @return Optional insertion code, used in some PDB and mmCIF files to represent "inserted"
   *     residues while maintaining the original numbering.
   */
  Optional<String> insertionCode();

  @Override
  default int compareTo(final ChainNumberICode t) {
    return new CompareToBuilder()
        .append(chainIdentifier(), t.chainIdentifier())
        .append(residueNumber(), t.residueNumber())
        .append(insertionCode().orElse(" "), t.insertionCode().orElse(" "))
        .build();
  }
}
