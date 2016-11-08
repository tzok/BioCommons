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
    public boolean isMmCif() {
        return false;
    }

    @Override
    public File getMmCifFile() {
        throw new UnsupportedOperationException(
                "Container does not represent mmCIF file");
    }

    @Override
    public Set<File> getPdbFiles() {
        return Collections.singleton(pdbFile);
    }

    @Override
    public String getMmCifChain(File pdbFile, String pdbChain) {
        if (!this.pdbFile.equals(pdbFile)) {
            // TODO
        }
        return pdbChain;
    }

    @Override
    public String getPdbChain(File pdbFile, String mmCifChain) {
        if (!this.pdbFile.equals(pdbFile)) {
            // TODO
        }
        return mmCifChain;
    }
}
