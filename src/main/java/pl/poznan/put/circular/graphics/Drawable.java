package pl.poznan.put.circular.graphics;

import pl.poznan.put.circular.exception.InvalidCircularValueException;

public interface Drawable {
    void draw() throws InvalidCircularValueException;
}
