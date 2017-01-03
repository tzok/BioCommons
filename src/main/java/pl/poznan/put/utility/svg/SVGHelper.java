package pl.poznan.put.utility.svg;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.bridge.BridgeContext;
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
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.math3.stat.StatUtils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
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

public final class SVGHelper {
    private static final NamespaceContext SVG_NAMESPACE = new SVGNamespace();
    private static final DOMImplementation DOM_IMPLEMENTATION =
            SVGDOMImplementation.getDOMImplementation();

    private SVGHelper() {
        super();
    }

    public static SVGDocument fromUri(final URI uri) throws IOException {
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(
                XMLResourceDescriptor.getXMLParserClassName());
        return (SVGDocument) factory.createDocument(uri.toString());
    }

    public static SVGDocument fromFile(final File file) throws IOException {
        return SVGHelper.fromUri(file.toURI());
    }

    public static FontMetrics getFontMetrics(final SVGGraphics2D svg) {
        return svg.getFontMetrics();
    }

    public static LineMetrics getLineMetrics(final SVGGraphics2D svg) {
        FontRenderContext renderContext = svg.getFontRenderContext();
        Font font = svg.getFont();
        return font.getLineMetrics(
                "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM",
                renderContext);
    }

    public static NamespaceContext getSVGNamespaceContext() {
        return SVGHelper.SVG_NAMESPACE;
    }

    public static byte[] export(
            final SVGDocument svgDocument, final Format format)
            throws IOException {
        return SVGHelper.export(svgDocument, format,
                                Collections.<TranscodingHints.Key,
                                        Object>emptyMap());
    }

    public static byte[] export(
            final SVGDocument svgDocument, final Format format,
            final Map<TranscodingHints.Key, Object> transcodingHints)
            throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Writer writer =
                new OutputStreamWriter(stream, Charset.defaultCharset());

        Transcoder transcoder = format.getTranscoder();
        for (final Map.Entry<TranscodingHints.Key, Object> entry :
                transcodingHints
                .entrySet()) {
            transcoder.addTranscodingHint(entry.getKey(), entry.getValue());
        }

        TranscoderInput input = new TranscoderInput(svgDocument);
        TranscoderOutput output =
                (format == Format.SVG) ? new TranscoderOutput(writer)
                                       : new TranscoderOutput(stream);

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

        SVGDocument mergedSvg = svgs.get(0);
        SVGSVGElement mergedRoot = mergedSvg.getRootElement();
        double[] widths = new double[svgs.size()];
        double[] heights = new double[svgs.size()];
        double currentWidth = 0;

        for (int i = 0; i < svgs.size(); i++) {
            SVGDocument svg = svgs.get(i);

            Rectangle2D box = SVGHelper.calculateBoundingBox(svg);
            widths[i] = box.getWidth() + box.getX();
            heights[i] = box.getHeight() + box.getY();

            SVGSVGElement rootElement = svg.getRootElement();
            rootElement.setAttribute("x", Double.toString(currentWidth));

            currentWidth += widths[i];

            if (i > 0) {
                Node importedNode =
                        mergedSvg.importNode(svg.getDocumentElement(), true);
                mergedRoot.appendChild(importedNode);
            }
        }

        double mergedWidth = StatUtils.sum(widths);
        double mergedHeight = StatUtils.max(heights);
        mergedRoot.setAttribute("width", Double.toString(mergedWidth));
        mergedRoot.setAttribute("height", Double.toString(mergedHeight));
        mergedRoot.setAttribute("viewBox",
                                "0 0 " + mergedWidth + ' ' + mergedHeight);

        return mergedSvg;
    }

    public static SVGDocument emptyDocument() {
        return (SVGDocument) SVGHelper.DOM_IMPLEMENTATION
                .createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg",
                                null);
    }

    public static Rectangle2D calculateBoundingBox(final Document doc) {
        GVTBuilder builder = new GVTBuilder();
        BridgeContext ctx = new BridgeContext(new UserAgentAdapter());
        GraphicsNode gvtRoot = builder.build(ctx, doc);
        return gvtRoot.getSensitiveBounds();
    }

    private static class SVGNamespace implements NamespaceContext {
        @Override
        public String getNamespaceURI(final String s) {
            if (s == null) {
                throw new IllegalArgumentException("Null prefix for namespace");
            }
            if (Objects.equals("svg", s)) {
                return SVGDOMImplementation.SVG_NAMESPACE_URI;
            }
            return XMLConstants.NULL_NS_URI;
        }

        @Override
        public String getPrefix(final String s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterator getPrefixes(final String s) {
            throw new UnsupportedOperationException();
        }
    }
}
