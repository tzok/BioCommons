package pl.poznan.put.pdb.analysis;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * An implementation of model container which has no PDB or mmCIF files.
 */
public final class EmptyModelContainer implements ModelContainer {
    private static final ModelContainer INSTANCE = new EmptyModelContainer();

    public static ModelContainer getInstance() {
        return EmptyModelContainer.INSTANCE;
    }

    private EmptyModelContainer() {
        super();
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
    public List<File> getPdbFiles() {
        return Collections.emptyList();
    }

    @Override
    public String getCifChain(final File pdbFile, final String pdbChain) {
        return pdbChain;
    }

    @Override
    public String getPdbChain(final File pdbFile, final String cifChain) {
        return cifChain;
    }

    @Override
    public void close() {
        // do nothing
    }
}
