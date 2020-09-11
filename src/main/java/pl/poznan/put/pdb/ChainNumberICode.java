package pl.poznan.put.pdb;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.io.Serializable;

/**
 * A set of methods that allow to address a residue by its chain name, residue number and insertion
 * code.
 */
public interface ChainNumberICode extends Comparable<ChainNumberICode>, Serializable {
  /** @return The identifier of the chain a residue belongs to. */
  String chainIdentifier();

  /** @return The number of a residue in the chain. */
  int residueNumber();

  /**
   * @return Optional insertion code, used in some PDB and mmCIF files to represent "inserted"
   *     residues while maintaining the original numbering.
   */
  String insertionCode();

  @Override
  default int compareTo(final ChainNumberICode t) {
    return new CompareToBuilder()
        .append(chainIdentifier(), t.chainIdentifier())
        .append(residueNumber(), t.residueNumber())
        .append(insertionCode(), t.insertionCode())
        .build();
  }
}
