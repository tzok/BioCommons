package pl.poznan.put.utility;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.svg.SVGDocument;

public class SVGHelper {
    public static SVGDocument emptyDocument() {
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        return (SVGDocument) impl.createDocument(svgNS, "svg", null);
    }

    public static FontMetrics getFontMetrics(SVGGraphics2D svg) {
        return svg.getFontMetrics();
    }

    public static LineMetrics getLineMetrics(SVGGraphics2D svg) {
        FontRenderContext renderContext = svg.getFontRenderContext();
        Font font = svg.getFont();
        return font.getLineMetrics("qwertyuiopasdfghjklzxcvbnm"
                + "QWERTYUIOPASDFGHJKLZXCVBNM", renderContext);
    }

    private SVGHelper() {
    }
}
