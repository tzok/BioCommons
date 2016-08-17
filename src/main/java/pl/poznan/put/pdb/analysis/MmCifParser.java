package pl.poznan.put.pdb.analysis;

import org.apache.commons.io.IOUtils;
import org.biojava.nbio.structure.io.mmcif.MMcifParser;
import org.biojava.nbio.structure.io.mmcif.SimpleMMcifParser;
import pl.poznan.put.pdb.PdbParsingException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

public class MmCifParser {
    private final MMcifParser parser = new SimpleMMcifParser();
    private final MmCifConsumer consumer = new MmCifConsumer();

    public MmCifParser() {
        parser.addMMcifConsumer(consumer);
    }

    public synchronized List<CifModel> parse(String mmCifContent)
            throws IOException, PdbParsingException {
        Reader reader = null;

        try {
            reader = new StringReader(mmCifContent);
            parser.parse(IOUtils.toBufferedReader(reader));
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return consumer.getModels();
    }
}
