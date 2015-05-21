package pl.poznan.put.interfaces;

import org.w3c.dom.svg.SVGDocument;

public interface Visualizable {
    SVGDocument visualize();

    void visualize3D();
}
