package pl.poznan.put.interfaces;

import pl.poznan.put.types.DistanceMatrix;

@FunctionalInterface
public interface Clusterable {
  DistanceMatrix getDataForClustering();
}
