package pl.poznan.put.pdb.analysis;

import org.apache.commons.collections4.BidiMap;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of {@link ModelContainer} which is created from
 * mmCIF file and its possible split into multiple PDB files.
 */
public class MmCifContainer implements ModelContainer {
    private final File mmCifFile;
    private final Map<File, BidiMap<String, String>> fileChainMap;

    public MmCifContainer(final File mmCifFile,
                          final Map<File, BidiMap<String, String>>
                                  fileChainMap) {
        this.mmCifFile = mmCifFile;
        this.fileChainMap = fileChainMap;
    }

    @Override
    public boolean isMmCif() {
        return true;
    }

    @Override
    public File getMmCifFile() {
        return mmCifFile;
    }

    public Set<File> getPdbFiles() {
        return fileChainMap.keySet();
    }

    public String getMmCifChain(final File pdbFile, final String pdbChain) {
        if (!fileChainMap.containsKey(pdbFile)) {
            // TODO
        }
        return fileChainMap.get(pdbFile).getKey(pdbChain);
    }

    public String getPdbChain(final File pdbFile, final String mmCifChain) {
        if (!fileChainMap.containsKey(pdbFile)) {
            // TODO
        }
        return fileChainMap.get(pdbFile).get(mmCifChain);
    }
}
