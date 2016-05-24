package pl.poznan.put.pdb.analysis;

import org.apache.commons.io.IOUtils;
import org.biojava.nbio.structure.io.mmcif.MMcifParser;
import org.biojava.nbio.structure.io.mmcif.SimpleMMcifParser;
import pl.poznan.put.pdb.PdbParsingException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

/**
 * Created by tzok on 24.05.16.
 */
public class MmCifParser {
    private final MMcifParser parser = new SimpleMMcifParser();
    private final MmCifConsumer consumer = new MmCifConsumer();

    public MmCifParser() {
        parser.addMMcifConsumer(consumer);
    }

    public synchronized List<PdbModel> parse(String mmCifContent) throws IOException, PdbParsingException {
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
