package pl.poznan.put.pdb.analysis;

import java.io.IOException;
import java.util.List;

/** An interface for both PDB and mmCIF parsers */
@FunctionalInterface
public interface StructureParser {
  List<PdbModel> parse(String structureContent) throws IOException;
}
