package pl.poznan.put.pdb.analysis;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.io.FileUtils;
import org.immutables.value.Value;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * An implementation of {@link ModelContainer} which is created from mmCIF file and its possible
 * split into multiple PDB files.
 */
@Value.Immutable
public abstract class CifContainer implements ModelContainer {
  /**
   * Create an empty instance (without any chain mapping).
   *
   * @param cifFile Path to the mmCIF file.
   * @return An instance without any chain mapping.
   */
  public static ModelContainer emptyInstance(final File cifFile) {
    return ImmutableCifContainer.of(cifFile, Collections.emptyMap());
  }

  /** @return The value of the {@code fileChainMap} attribute, */
  @Value.Parameter(order = 2)
  public abstract Map<File, BidiMap<String, String>> fileChainMap();

  /** @return The value of the {@code cifFile} attribute, */
  @Value.Parameter(order = 1)
  public abstract File cifFile();

  @Override
  public final List<File> pdbFiles() {
    return new ArrayList<>(fileChainMap().keySet());
  }

  @Override
  public final String originalCifChainName(final File pdbFile, final String pdbChain) {
    if (!fileChainMap().containsKey(pdbFile)) {
      throw new IllegalArgumentException(
          "Failed to find PDBx/mmCIF chain name, missing data for file: " + pdbFile);
    }
    return fileChainMap().get(pdbFile).getKey(pdbChain);
  }

  @Override
  public final String convertedPdbChainName(final File pdbFile, final String cifChain) {
    if (!fileChainMap().containsKey(pdbFile)) {
      throw new IllegalArgumentException(
          "Failed to find PDB chain name, missing data for file: " + pdbFile);
    }
    return fileChainMap().get(pdbFile).get(cifChain);
  }

  /**
   * This class is {@link AutoCloseable} and upon closing, all files (PDB and PDBx/mmCIF) will be
   * deleted.
   */
  @Override
  public final void close() {
    FileUtils.deleteQuietly(cifFile());
    for (final File file : fileChainMap().keySet()) {
      FileUtils.deleteQuietly(file);
    }
  }
}
