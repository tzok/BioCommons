package pl.poznan.put.circular.graphics;

import java.awt.geom.Rectangle2D;
import java.util.Locale;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;
import pl.poznan.put.utility.svg.SVGHelper;

public abstract class AbstractDrawable implements Drawable {
  protected SVGDocument svgDocument = SVGHelper.emptyDocument();
  protected SVGGraphics2D svgGraphics = new SVGGraphics2D(svgDocument);

  @Override
  public final SVGDocument finalizeDrawing() {
    final SVGSVGElement rootElement = svgDocument.getRootElement();
    svgGraphics.getRoot(rootElement);

    final Rectangle2D box = SVGHelper.calculateBoundingBox(svgDocument);
    final String viewBox =
        String.format(
            Locale.US, "%f %f %f %f", box.getX(), box.getY(), box.getWidth(), box.getHeight());
    rootElement.setAttributeNS(null, SVGConstants.SVG_VIEW_BOX_ATTRIBUTE, viewBox);
    rootElement.setAttributeNS(
        null, SVGConstants.SVG_WIDTH_ATTRIBUTE, Double.toString(box.getWidth()));
    rootElement.setAttributeNS(
        null, SVGConstants.SVG_HEIGHT_ATTRIBUTE, Double.toString(box.getHeight()));
    return svgDocument;
  }
}
