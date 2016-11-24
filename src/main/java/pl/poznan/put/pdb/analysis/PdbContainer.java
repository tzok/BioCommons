package pl.poznan.put.pdb.analysis;

import java.io.File;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * An implementation of {@link ModelContainer} which uses a single PDB
 * file inside.
 */
public class PdbContainer implements ModelContainer {
    private final File pdbFile;

    public PdbContainer(final File pdbFile) {
        super();
        this.pdbFile = pdbFile;
    }

    @Override
    public boolean isCif() {
        return false;
    }

    @Override
    public File getCifFile() {
        throw new UnsupportedOperationException(
                "Container does not represent mmCIF file");
    }

    @Override
    public Set<File> getPdbFiles() {
        return Collections.singleton(pdbFile);
    }

    @Override
    public String getCifChain(final File pdbFile, final String pdbChain) {
        if (!Objects.equals(this.pdbFile, pdbFile)) {
            // TODO
        }
        return pdbChain;
    }

    @Override
    public String getPdbChain(final File pdbFile, final String cifChain) {
        if (!Objects.equals(this.pdbFile, pdbFile)) {
            // TODO
        }
        return cifChain;
    }
}
