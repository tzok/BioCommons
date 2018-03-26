package pl.poznan.put.utility.svg;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;
import javax.xml.XMLConstants;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.junit.Test;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.utility.ResourcesHelper;

public class SVGHelperTest {
  @Test
  public void fromUri() throws Exception {
    final URI uri = ResourcesHelper.loadResourceUri("java.svg");
    SVGHelper.fromUri(uri);
  }

  @Test
  public void fromFile() throws Exception {
    final File file = ResourcesHelper.loadResourceFile("java.svg");
    SVGHelper.fromFile(file);
  }

  @Test
  public void getFontMetrics() {
    // TODO: add unit test
  }

  @Test
  public void getLineMetrics() {
    // TODO: add unit test
  }

  @Test
  public void export() throws Exception {
    final URI uri = ResourcesHelper.loadResourceUri("java.svg");
    final SVGDocument svgDocument = SVGHelper.fromUri(uri);

    for (final Format format : Format.values()) {
      SVGHelper.export(svgDocument, format);
    }
  }

  @Test
  public void merge() throws Exception {
    final SVGDocument document = SVGHelper.fromFile(ResourcesHelper.loadResourceFile("java.svg"));
    final SVGDocument merged = SVGHelper.merge(Arrays.asList(document, document));
    final byte[] bytes = SVGHelper.export(merged, Format.SVG);
    final String actualString = new String(bytes, Charset.defaultCharset());
    final String expectedString = ResourcesHelper.loadResource("merged.svg");
    assertEquals(expectedString, actualString);
  }

  @Test
  public void emptyDocument() throws IOException {
    final SVGDocument emptyDocument = SVGHelper.emptyDocument();
    final byte[] bytes = SVGHelper.export(emptyDocument, Format.SVG);
    final String actualString = new String(bytes, Charset.defaultCharset());
    final String expectedString = ResourcesHelper.loadResource("empty.svg");
    assertEquals(expectedString, actualString);
  }

  @Test
  public void svgNamespaceContext() {
    assertEquals(
        SVGDOMImplementation.SVG_NAMESPACE_URI,
        SVGHelper.svgNamespaceContext().getNamespaceURI("svg"));
    assertEquals(XMLConstants.NULL_NS_URI, SVGHelper.svgNamespaceContext().getNamespaceURI(""));
  }

  @Test(expected = IllegalArgumentException.class)
  public void svgNamespaceContextNullNamespace() {
    SVGHelper.svgNamespaceContext().getNamespaceURI(null);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void svgNamespaceContextUnsupportedPrefix() {
    SVGHelper.svgNamespaceContext().getPrefix("");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void svgNamespaceContextUnsupportedPrefixes() {
    SVGHelper.svgNamespaceContext().getPrefixes("");
  }
}
