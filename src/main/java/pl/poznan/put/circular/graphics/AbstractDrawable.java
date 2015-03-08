package pl.poznan.put.circular.graphics;

import java.awt.geom.Rectangle2D;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

import pl.poznan.put.utility.svg.SVGHelper;

public abstract class AbstractDrawable implements Drawable {
    protected SVGDocument svgDocument = SVGHelper.emptyDocument();
    protected SVGGraphics2D svgGraphics = new SVGGraphics2D(svgDocument);

    public SVGDocument finalizeDrawingAndGetSVG() {
        SVGSVGElement rootElement = svgDocument.getRootElement();
        svgGraphics.getRoot(rootElement);
        Rectangle2D box = SVGHelper.calculateBoundingBox(svgDocument);
        rootElement.setAttributeNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "viewBox", box.getX() + " " + box.getY() + " " + box.getWidth() + " " + box.getHeight());
        return svgDocument;
    }
}
