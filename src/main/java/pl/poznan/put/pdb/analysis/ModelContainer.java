package pl.poznan.put.pdb.analysis;

import java.io.File;
import java.util.Set;

/**
 * An interface representing a set of PDB files which all correspond to the
 * same structure and also their chain names' mapping.
 */
public interface ModelContainer {
    boolean isMmCif();

    File getMmCifFile();

    Set<File> getPdbFiles();

    String getMmCifChain(final File pdbFile, final String pdbChain);

    String getPdbChain(final File pdbFile, final String mmCifChain);
}
