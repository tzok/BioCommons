package pl.poznan.put.circular.graphics;

import org.w3c.dom.svg.SVGDocument;

public interface Drawable {
    void draw();

    SVGDocument finalizeDrawing();
}
