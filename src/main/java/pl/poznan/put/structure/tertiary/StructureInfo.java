package pl.poznan.put.structure.tertiary;

import java.io.File;

import pl.poznan.put.pdb.analysis.PdbModel;

public class StructureInfo implements Comparable<StructureInfo> {
    private final PdbModel structure;
    private final File path;
    private final String name;

    public StructureInfo(PdbModel structure, File path, String name) {
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
    public int compareTo(StructureInfo o) {
        return name.compareTo(o.name);
    }
}
