package pl.poznan.put.circular.graphics;

import org.w3c.dom.svg.SVGDocument;

import pl.poznan.put.circular.exception.InvalidCircularValueException;

public interface Drawable {
    SVGDocument draw() throws InvalidCircularValueException;
}
