package pl.poznan.put.interfaces;

import org.w3c.dom.svg.SVGDocument;

/** A set of methods allowing to visualize the result. */
public interface Visualizable {
  /**
   * Generates a visualization in SVG format.
   *
   * @return An instance of {@link SVGDocument} with the visualization.
   */
  SVGDocument visualize();

  /** Generates a visualization in 3D if possible. */
  void visualize3D();
}
