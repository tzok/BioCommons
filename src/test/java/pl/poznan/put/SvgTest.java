package pl.poznan.put;

import org.junit.Test;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.utility.ResourcesHelper;
import pl.poznan.put.utility.svg.Format;
import pl.poznan.put.utility.svg.SVGHelper;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class SvgTest {
    @Test
    public final void testFromFile() throws Exception {
        final File file = ResourcesHelper.loadResourceFile("java.svg");
        SVGHelper.fromFile(file);
    }

    @Test
    public final void testFromUri() throws Exception {
        final URI uri = ResourcesHelper.loadResourceUri("java.svg");
        SVGHelper.fromUri(uri);
    }

    @Test
    public final void testFormats() throws Exception {
        for (final Format format : Format.values()) {
            SvgTest.exportExampleSvg(format);
        }
    }

    private static void exportExampleSvg(final Format format)
            throws IOException, URISyntaxException {
        final URI uri = ResourcesHelper.loadResourceUri("java.svg");
        final SVGDocument svgDocument = SVGHelper.fromUri(uri);
        SVGHelper.export(svgDocument, format);
    }
}
