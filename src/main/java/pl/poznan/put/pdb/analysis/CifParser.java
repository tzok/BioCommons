package pl.poznan.put.pdb.analysis;

import org.apache.commons.io.IOUtils;
import org.biojava.nbio.structure.io.mmcif.MMcifParser;
import org.biojava.nbio.structure.io.mmcif.SimpleMMcifParser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

public class CifParser implements StructureParser {
  private final MMcifParser parser = new SimpleMMcifParser();
  private final CifConsumer consumer = new CifConsumer();

  public CifParser() {
    super();
    parser.addMMcifConsumer(consumer);
  }

  @Override
  public final List<PdbModel> parse(final String structureContent)
      throws IOException {
    synchronized (parser) {
      try (final Reader reader = new StringReader(structureContent)) {
        parser.parse(IOUtils.toBufferedReader(reader));
      }
      return consumer.getModels();
    }
  }
}
