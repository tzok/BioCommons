package pl.poznan.put.pdb.analysis;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An implementation of {@link ModelContainer} which is created from mmCIF file and its possible
 * split into multiple PDB files.
 */
public class CifContainer implements ModelContainer {
  private final File cifFile;
  private final Map<File, BidiMap<String, String>> fileChainMap;

  public CifContainer(final File cifFile, final Map<File, BidiMap<String, String>> fileChainMap) {
    super();
    this.cifFile = cifFile;
    this.fileChainMap = new HashMap<>(fileChainMap);
  }

  @Override
  public final boolean isCif() {
    return true;
  }

  @Override
  public final File getCifFile() {
    return cifFile;
  }

  @Override
  public final List<File> getPdbFiles() {
    return new ArrayList<>(fileChainMap.keySet());
  }

  @Override
  public final String getCifChain(final File pdbFile, final String pdbChain) {
    if (!fileChainMap.containsKey(pdbFile)) {
      // TODO
    }
    return fileChainMap.get(pdbFile).getKey(pdbChain);
  }

  @Override
  public final String getPdbChain(final File pdbFile, final String cifChain) {
    if (!fileChainMap.containsKey(pdbFile)) {
      // TODO
    }
    return fileChainMap.get(pdbFile).get(cifChain);
  }

  @Override
  public void close() throws IOException {
    FileUtils.forceDelete(cifFile);
    for (final File file : fileChainMap.keySet()) {
      FileUtils.forceDelete(file);
    }
  }
}
