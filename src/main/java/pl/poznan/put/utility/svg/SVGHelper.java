package pl.poznan.put.utility.svg;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

public class SVGHelper {
    private static class SVGNamespace implements NamespaceContext {
        @Override
        public Iterator getPrefixes(String arg0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getPrefix(String arg0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getNamespaceURI(String prefix) {
            if (prefix == null) {
                throw new IllegalArgumentException("Null prefix for namespace");
            } else if ("svg".equals(prefix)) {
                return SVGDOMImplementation.SVG_NAMESPACE_URI;
            }
            return XMLConstants.NULL_NS_URI;
        }
    }

    private static final NamespaceContext SVG_NAMESPACE = new SVGNamespace();
    private static final DOMImplementation DOM_IMPLEMENTATION = SVGDOMImplementation.getDOMImplementation();

    public static SVGDocument emptyDocument() {
        return (SVGDocument) SVGHelper.DOM_IMPLEMENTATION.createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
    }

    public static SVGDocument fromFile(File file) throws IOException {
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
        return (SVGDocument) factory.createDocument("file://" + file.getAbsolutePath());
    }

    public static FontMetrics getFontMetrics(SVGGraphics2D svg) {
        return svg.getFontMetrics();
    }

    public static LineMetrics getLineMetrics(SVGGraphics2D svg) {
        FontRenderContext renderContext = svg.getFontRenderContext();
        Font font = svg.getFont();
        return font.getLineMetrics("qwertyuiopasdfghjklzxcvbnm" + "QWERTYUIOPASDFGHJKLZXCVBNM", renderContext);
    }

    public static NamespaceContext getSVGNamespaceContext() {
        return SVGHelper.SVG_NAMESPACE;
    }

    public static void export(SVGDocument svgDocument, OutputStream stream,
            Format format, Map<TranscodingHints.Key, Object> transcodingHints) throws IOException {
        if (format == Format.EPS || format == Format.SVG) {
            OutputStreamWriter writer = null;

            try {
                writer = new OutputStreamWriter(stream);
                SVGHelper.export(svgDocument, writer, format, transcodingHints);
            } finally {
                IOUtils.closeQuietly(writer);
            }

            return;
        }

        try {
            TranscoderInput input = new TranscoderInput(svgDocument);
            TranscoderOutput output = new TranscoderOutput(stream);
            Transcoder transcoder = format.getTranscoder();

            if (transcodingHints != null) {
                for (Entry<TranscodingHints.Key, Object> entry : transcodingHints.entrySet()) {
                    transcoder.addTranscodingHint(entry.getKey(), entry.getValue());
                }
            }

            transcoder.transcode(input, output);
        } catch (TranscoderException e) {
            throw new IOException("Failed to save SVG as image", e);
        }
    }

    private static void export(SVGDocument svgDocument, Writer writer,
            Format format, Map<TranscodingHints.Key, Object> transcodingHints) throws IOException {
        try {
            TranscoderInput input = new TranscoderInput(svgDocument);
            TranscoderOutput output = new TranscoderOutput(writer);
            Transcoder transcoder = format.getTranscoder();

            if (transcodingHints != null) {
                for (Entry<TranscodingHints.Key, Object> entry : transcodingHints.entrySet()) {
                    transcoder.addTranscodingHint(entry.getKey(), entry.getValue());
                }
            }

            transcoder.transcode(input, output);
        } catch (TranscoderException e) {
            throw new IOException("Failed to save SVG as image", e);
        }
    }

    public static Rectangle2D calculateBoundingBox(SVGDocument doc) {
        GVTBuilder builder = new GVTBuilder();
        BridgeContext ctx = new BridgeContext(new UserAgentAdapter());
        GraphicsNode gvtRoot = builder.build(ctx, doc);
        return gvtRoot.getSensitiveBounds();
    }

    public static SVGDocument merge(List<SVGDocument> svgs) {
        if (svgs.size() == 0) {
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
                Node importedNode = mergedSvg.importNode(svg.getDocumentElement(), true);
                mergedRoot.appendChild(importedNode);
            }
        }

        double mergedWidth = StatUtils.sum(widths);
        double mergedHeight = StatUtils.max(heights);
        mergedRoot.setAttribute("width", Double.toString(mergedWidth));
        mergedRoot.setAttribute("height", Double.toString(mergedHeight));
        mergedRoot.setAttribute("viewBox", "0 0 " + mergedWidth + " " + mergedHeight);

        return mergedSvg;
    }

    private SVGHelper() {
    }
}
