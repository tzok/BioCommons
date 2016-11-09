package pl.poznan.put.pdb.analysis;

import java.io.File;
import java.util.Collections;
import java.util.Set;

/**
 * An implementation of {@link ModelContainer} which uses a single PDB
 * file inside.
 */
public class PdbContainer implements ModelContainer {
    private final File pdbFile;

    public PdbContainer(File pdbFile) {
        this.pdbFile = pdbFile;
    }

    @Override
    public boolean isCif() {
        return false;
    }

    public File getCifFile() {
        throw new UnsupportedOperationException(
                "Container does not represent mmCIF file");
    }

    @Override
    public Set<File> getPdbFiles() {
        return Collections.singleton(pdbFile);
    }

    @Override
    public String getCifChain(File pdbFile, String pdbChain) {
        if (!this.pdbFile.equals(pdbFile)) {
            // TODO
        }
        return pdbChain;
    }

    @Override
    public String getPdbChain(File pdbFile, String cifChain) {
        if (!this.pdbFile.equals(pdbFile)) {
            // TODO
        }
        return cifChain;
    }
}
