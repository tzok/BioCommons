package pl.poznan.put.circular.graphics;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;
import pl.poznan.put.utility.svg.SVGHelper;

import java.awt.geom.Rectangle2D;

public abstract class AbstractDrawable implements Drawable {
    private SVGDocument svgDocument = SVGHelper.emptyDocument();
    private SVGGraphics2D svgGraphics = new SVGGraphics2D(svgDocument);

    protected final SVGDocument finalizeDrawingAndGetSvg() {
        SVGSVGElement rootElement = svgDocument.getRootElement();
        svgGraphics.getRoot(rootElement);
        Rectangle2D box = SVGHelper.calculateBoundingBox(svgDocument);
        rootElement.setAttributeNS(null, SVGConstants.SVG_VIEW_BOX_ATTRIBUTE,
                                   box.getX() + " " + box.getY() + ' ' + box
                                           .getWidth() + ' ' + box.getHeight());
        rootElement.setAttributeNS(null, SVGConstants.SVG_WIDTH_ATTRIBUTE,
                                   Double.toString(box.getWidth()));
        rootElement.setAttributeNS(null, SVGConstants.SVG_HEIGHT_ATTRIBUTE,
                                   Double.toString(box.getHeight()));
        return svgDocument;
    }

    public final SVGDocument getSvgDocument() {
        return svgDocument;
    }

    public final void setSvgDocument(final SVGDocument svgDocument) {
        this.svgDocument = svgDocument;
    }

    public final SVGGraphics2D getSvgGraphics() {
        return svgGraphics;
    }

    public final void setSvgGraphics(final SVGGraphics2D svgGraphics) {
        this.svgGraphics = svgGraphics;
    }
}
