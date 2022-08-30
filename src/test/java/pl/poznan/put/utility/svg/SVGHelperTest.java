package pl.poznan.put.utility.svg;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.net.URI;
import javax.xml.XMLConstants;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.junit.Test;
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
  public final void svgNamespaceContext() {
    assertThat(
        SVGHelper.svgNamespaceContext().getNamespaceURI("svg"),
        is(SVGDOMImplementation.SVG_NAMESPACE_URI));
    assertThat(SVGHelper.svgNamespaceContext().getNamespaceURI(""), is(XMLConstants.NULL_NS_URI));
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
