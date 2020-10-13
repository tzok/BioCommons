package pl.poznan.put.pdb.analysis;

import java.io.Closeable;
import java.io.File;
import java.util.List;

/**
 * A set of PDB files which all correspond to the same structure and also their chain names'
 * mapping.
 */
public interface ModelContainer extends Closeable {
  /** @return A list of PDB files corresponding to this container. */
  List<File> pdbFiles();

  /**
   * Checks mapping of chains to get the original mmCIF chain name from the generated PDB chain name.
   *
   * @param pdbFile A PDB file.
   * @param pdbChain Chain name in the PDB file.
   * @return Original chain name in the mmCIF file.
   */
  String originalCifChainName(final File pdbFile, final String pdbChain);

  /**
   * Checks mapping of chains to get the generated PDB chain name from the original mmCIF chain name.
   *
   * @param pdbFile A PDB file.
   * @param cifChain Chain name in the mmCif file.
   * @return Original chain name in the PDB file.
   */
  String convertedPdbChainName(final File pdbFile, final String cifChain);
}
