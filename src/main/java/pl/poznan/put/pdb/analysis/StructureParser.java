package pl.poznan.put.pdb.analysis;

import java.io.IOException;
import java.util.List;
import pl.poznan.put.pdb.PdbParsingException;

/** An interface for both PDB and mmCIF parsers */
public interface StructureParser {
  List<PdbModel> parse(String structureContent) throws PdbParsingException, IOException;
}
