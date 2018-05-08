package pl.poznan.put.pdb.analysis;

import pl.poznan.put.pdb.PdbParsingException;

import java.io.IOException;
import java.util.List;

/** An interface for both PDB and mmCIF parsers */
public interface StructureParser {
  List<? extends PdbModel> parse(String structureContent) throws PdbParsingException, IOException;
}
