package pl.poznan.put.utility.svg;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.svg.SVGDocument;

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
                throw new NullPointerException("Null prefix for namespace");
            } else if ("svg".equals(prefix)) {
                return SVGDOMImplementation.SVG_NAMESPACE_URI;
            }
            return XMLConstants.NULL_NS_URI;
        }
    }

    private static final NamespaceContext SVG_NAMESPACE = new SVGNamespace();
    private static final DOMImplementation DOM_IMPLEMENTATION = SVGDOMImplementation.getDOMImplementation();
    private static final SAXSVGDocumentFactory SVG_FACTORY = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());

    public static SVGDocument emptyDocument() {
        return (SVGDocument) SVGHelper.DOM_IMPLEMENTATION.createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
    }

    public static SVGDocument fromFile(File file) throws IOException {
        return (SVGDocument) SVGHelper.SVG_FACTORY.createDocument("file://" + file.getAbsolutePath());
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

    public static void export(SVGDocument svgDocument, OutputStream stream, Format format) throws IOException {
        try {
            TranscoderInput input = new TranscoderInput(svgDocument);
            TranscoderOutput output = new TranscoderOutput(stream);
            Transcoder transcoder = format.getTranscoder();
            transcoder.transcode(input, output);
        } catch (TranscoderException e) {
            throw new IOException("Failed to save SVG as image", e);
        }
    }

    private SVGHelper() {
    }
}
