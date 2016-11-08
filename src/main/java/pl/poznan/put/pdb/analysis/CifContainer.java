package pl.poznan.put.pdb.analysis;

import org.apache.commons.collections4.BidiMap;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of {@link ModelContainer} which is created from
 * mmCIF file and its possible split into multiple PDB files.
 */
public class CifContainer implements ModelContainer {
    private final File cifFile;
    private final Map<File, BidiMap<String, String>> fileChainMap;

    public CifContainer(final File cifFile,
                        final Map<File, BidiMap<String, String>> fileChainMap) {
        this.cifFile = cifFile;
        this.fileChainMap = fileChainMap;
    }

    @Override
    public boolean isCif() {
        return true;
    }

    public File getCifFile() {
        return cifFile;
    }

    public Set<File> getPdbFiles() {
        return fileChainMap.keySet();
    }

    public String getCifChain(final File pdbFile, final String pdbChain) {
        if (!fileChainMap.containsKey(pdbFile)) {
            // TODO
        }
        return fileChainMap.get(pdbFile).getKey(pdbChain);
    }

    public String getPdbChain(final File pdbFile, final String cifChain) {
        if (!fileChainMap.containsKey(pdbFile)) {
            // TODO
        }
        return fileChainMap.get(pdbFile).get(cifChain);
    }
}
