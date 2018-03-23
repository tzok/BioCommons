package pl.poznan.put.utility.svg;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.math3.stat.StatUtils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;
import pl.poznan.put.utility.ExecHelper;

@Slf4j
public final class SVGHelper {
  private static final NamespaceContext SVG_NAMESPACE = new SVGNamespace();
  private static final DOMImplementation DOM_IMPLEMENTATION =
      SVGDOMImplementation.getDOMImplementation();

  private SVGHelper() {
    super();
  }

  public static SVGDocument fromUri(final URI uri) throws IOException {
    final SAXSVGDocumentFactory factory =
        new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
    return (SVGDocument) factory.createDocument(uri.toString());
  }

  public static SVGDocument fromFile(final File file) throws IOException {
    return SVGHelper.fromUri(file.toURI());
  }

  public static FontMetrics getFontMetrics(final SVGGraphics2D svg) {
    return svg.getFontMetrics();
  }

  public static LineMetrics getLineMetrics(final SVGGraphics2D svg) {
    final FontRenderContext renderContext = svg.getFontRenderContext();
    final Font font = svg.getFont();
    return font.getLineMetrics(
        "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM", renderContext);
  }

  public static NamespaceContext svgNamespaceContext() {
    return SVGHelper.SVG_NAMESPACE;
  }

  public static byte[] export(final SVGDocument svgDocument, final Format format)
      throws IOException {
    if (format == Format.SVG) {
      return SVGHelper.exportInternal(svgDocument, Format.SVG);
    }

    File inputFile = null;
    File outputFile = null;

    try {
      inputFile = File.createTempFile("svg-helper", ".svg");
      FileUtils.writeByteArrayToFile(inputFile, SVGHelper.export(svgDocument, Format.SVG));
      outputFile = File.createTempFile("svg-helper", '.' + format.getExtension());

      ExecHelper.execute(
          "inkscape",
          "--without-gui",
          format.getInkscapeArgument(),
          outputFile.getAbsolutePath(),
          inputFile.getAbsolutePath());

      return FileUtils.readFileToByteArray(outputFile);
    } catch (final ExecuteException ignored) {
      SVGHelper.log.warn("Failed to run inkscape to export image, will try to use Apache FOP");
      return SVGHelper.exportInternal(svgDocument, format);
    } finally {
      FileUtils.deleteQuietly(inputFile);
      FileUtils.deleteQuietly(outputFile);
    }
  }

  private static byte[] exportInternal(final SVGDocument svgDocument, final Format format)
      throws IOException {
    return SVGHelper.exportInternal(svgDocument, format, Collections.emptyMap());
  }

  private static byte[] exportInternal(final SVGDocument svgDocument, final Format format,
                                       final Map<TranscodingHints.Key, Object> transcodingHints)
      throws IOException {
    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    final Writer writer = new OutputStreamWriter(stream, Charset.defaultCharset());

    final Transcoder transcoder = format.getTranscoder();
    for (final Map.Entry<TranscodingHints.Key, Object> entry : transcodingHints.entrySet()) {
      transcoder.addTranscodingHint(entry.getKey(), entry.getValue());
    }

    final TranscoderInput input = new TranscoderInput(svgDocument);
    final TranscoderOutput output =
        (format == Format.SVG) ? new TranscoderOutput(writer) : new TranscoderOutput(stream);

    try {
      transcoder.transcode(input, output);
    } catch (final TranscoderException e) {
      throw new IOException("Failed to save SVG as image", e);
    }

    return stream.toByteArray();
  }

  public static SVGDocument merge(final List<SVGDocument> svgs) {
    if (svgs.isEmpty()) {
      return SVGHelper.emptyDocument();
    }

    final Rectangle2D[] boxes = new Rectangle2D[svgs.size()];
    final boolean[] isValid = new boolean[svgs.size()];

    for (int i = 0, size = svgs.size(); i < size; i++) {
      final SVGDocument svg = svgs.get(i);
      try {
        boxes[i] = SVGHelper.calculateBoundingBox(svg);
        isValid[i] = true;
      } catch (final BridgeException ignored) {
        boxes[i] = new Rectangle2D.Double();
        isValid[i] = false;
      }
    }

    final SVGDocument mergedSvg = svgs.get(0);
    final SVGSVGElement mergedRoot = mergedSvg.getRootElement();
    final double[] widths = new double[svgs.size()];
    final double[] heights = new double[svgs.size()];
    double currentWidth = 0;

    for (int i = 0; i < svgs.size(); i++) {
      final SVGDocument svg = svgs.get(i);

      if (isValid[i]) {
        final Rectangle2D box = boxes[i];
        widths[i] = box.getWidth() + box.getX();
        heights[i] = box.getHeight() + box.getY();

        final SVGSVGElement rootElement = svg.getRootElement();
        rootElement.setAttribute("x", Double.toString(currentWidth));

        currentWidth += widths[i];

        if (i > 0) {
          final Node importedNode = mergedSvg.importNode(svg.getDocumentElement(), true);
          mergedRoot.appendChild(importedNode);
        }
      }
    }

    final double mergedWidth = StatUtils.sum(widths);
    final double mergedHeight = StatUtils.max(heights);
    mergedRoot.setAttribute("width", Double.toString(mergedWidth));
    mergedRoot.setAttribute("height", Double.toString(mergedHeight));
    mergedRoot.setAttribute("viewBox", String.format("0 0 %s %s", mergedWidth, mergedHeight));

    return mergedSvg;
  }

  private static SVGDocument emptyDocument() {
    return (SVGDocument)
        SVGHelper.DOM_IMPLEMENTATION.createDocument(
            SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
  }

  private static Rectangle2D calculateBoundingBox(final Document doc) {
    final GVTBuilder builder = new GVTBuilder();
    final BridgeContext ctx = new BridgeContext(new UserAgentAdapter());
    final GraphicsNode gvtRoot = builder.build(ctx, doc);
    return gvtRoot.getSensitiveBounds();
  }

  private static class SVGNamespace implements NamespaceContext {
    @Override
    public final String getNamespaceURI(final String s) {
      if (s == null) {
        throw new IllegalArgumentException("Null prefix for namespace");
      }
      if (Objects.equals("svg", s)) {
        return SVGDOMImplementation.SVG_NAMESPACE_URI;
      }
      return XMLConstants.NULL_NS_URI;
    }

    @Override
    public final String getPrefix(final String s) {
      throw new UnsupportedOperationException("N/A");
    }

    @Override
    public final Iterator getPrefixes(final String s) {
      throw new UnsupportedOperationException("N/A");
    }
  }
}
