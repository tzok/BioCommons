package pl.poznan.put.structure.tertiary;

import pl.poznan.put.pdb.analysis.PdbModel;

import java.io.File;

public class StructureInfo implements Comparable<StructureInfo> {
    private final PdbModel structure;
    private final File path;
    private final String name;

    public StructureInfo(
            final PdbModel structure, final File path, final String name) {
        super();
        this.structure = structure;
        this.path = path;
        this.name = name;
    }

    public PdbModel getStructure() {
        return structure;
    }

    public File getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(final StructureInfo t) {
        if (t == null) {
            throw new NullPointerException();
        }
        return name.compareTo(t.name);
    }
}
