package pl.poznan.put.interfaces;

import org.w3c.dom.svg.SVGDocument;

/** A set of methods allowing to visualize the result. */
public interface Visualizable {
  /**
   * Generate a visualization in SVG format.
   *
   * @return An instance of {@link SVGDocument} with the visualization.
   */
  SVGDocument visualize();

  /** Generate a visualization in 3D if possible. */
  void visualize3D();
}
