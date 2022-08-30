package pl.poznan.put.interfaces;

import pl.poznan.put.types.DistanceMatrix;

/** A set of methods that a data structure must implement to be clustered. */
@FunctionalInterface
public interface Clusterable {
  /**
   * @return A distance matrix.
   */
  DistanceMatrix distanceMatrix();
}
