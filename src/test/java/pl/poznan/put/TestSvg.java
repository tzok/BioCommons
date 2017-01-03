package pl.poznan.put;

import org.junit.Test;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.utility.svg.Format;
import pl.poznan.put.utility.svg.SVGHelper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class TestSvg {
    @Test
    public final void testFormats() throws Exception {
        for (final Format format : Format.values()) {
            TestSvg.exportExampleSvg(format);
        }
    }

    private static void exportExampleSvg(final Format format)
            throws IOException, URISyntaxException {
        URI uri = TestSvg.class.getResource("/java.svg").toURI();
        SVGDocument svgDocument = SVGHelper.fromUri(uri);
        SVGHelper.export(svgDocument, format);
    }
}
