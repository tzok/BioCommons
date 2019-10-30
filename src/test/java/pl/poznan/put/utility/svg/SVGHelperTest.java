package pl.poznan.put.utility.svg;

import static org.hamcrest.CoreMatchers.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;
import javax.xml.XMLConstants;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.svg.SVGDocument;
import pl.poznan.put.utility.ResourcesHelper;

public class SVGHelperTest {
  @Test
  public final void fromUri() throws Exception {
    final URI uri = ResourcesHelper.loadResourceUri("java.svg");
    SVGHelper.fromUri(uri);
  }

  @Test
  public final void fromFile() throws Exception {
    final File file = ResourcesHelper.loadResourceFile("java.svg");
    SVGHelper.fromFile(file);
  }

  @Test
  public final void merge() throws Exception {
    final SVGDocument document = SVGHelper.fromFile(ResourcesHelper.loadResourceFile("java.svg"));
    final SVGDocument merged = SVGHelper.merge(Arrays.asList(document, document));
    final byte[] bytes = SVGHelper.export(merged, Format.SVG);
    final String actualString = new String(bytes, Charset.defaultCharset());
    final String expectedString = ResourcesHelper.loadResource("merged.svg");
    Assert.assertThat(actualString, is(expectedString));
  }

  @Test
  public final void emptyDocument() throws IOException {
    final SVGDocument emptyDocument = SVGHelper.emptyDocument();
    final byte[] bytes = SVGHelper.export(emptyDocument, Format.SVG);
    final String actualString = new String(bytes, Charset.defaultCharset());
    final String expectedString = ResourcesHelper.loadResource("empty.svg");
    Assert.assertThat(actualString, is(expectedString));
  }

  @Test
  public final void svgNamespaceContext() {
    Assert.assertThat(SVGHelper.svgNamespaceContext().getNamespaceURI("svg"), is(SVGDOMImplementation.SVG_NAMESPACE_URI));
    Assert.assertThat(SVGHelper.svgNamespaceContext().getNamespaceURI(""), is(XMLConstants.NULL_NS_URI));
  }

  @Test(expected = IllegalArgumentException.class)
  public final void svgNamespaceContextNullNamespace() {
    SVGHelper.svgNamespaceContext().getNamespaceURI(null);
  }

  @Test(expected = UnsupportedOperationException.class)
  public final void svgNamespaceContextUnsupportedPrefix() {
    SVGHelper.svgNamespaceContext().getPrefix("");
  }

  @Test(expected = UnsupportedOperationException.class)
  public final void svgNamespaceContextUnsupportedPrefixes() {
    SVGHelper.svgNamespaceContext().getPrefixes("");
  }
}
