package pl.poznan.put.pdb.analysis;

import org.apache.commons.io.IOUtils;
import org.biojava.nbio.structure.io.mmcif.MMcifParser;
import org.biojava.nbio.structure.io.mmcif.SimpleMMcifParser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

/** A parser of mmCIF format. */
public final class CifParser {
  private CifParser() {
    super();
  }

  /**
   * Parse content in mmCIF format.
   *
   * @param structureContent A string with data in mmCIF format.
   * @return A parsed object representing a molecular structure.
   * @throws IOException When parsing of the data fails.
   */
  public static List<CifModel> parse(final String structureContent) throws IOException {
    final MMcifParser parser = new SimpleMMcifParser();
    final CifConsumer consumer = new CifConsumer();
    parser.addMMcifConsumer(consumer);

    try (final Reader reader = new StringReader(structureContent)) {
      parser.parse(IOUtils.toBufferedReader(reader));
    }
    return consumer.getModels();
  }
}
