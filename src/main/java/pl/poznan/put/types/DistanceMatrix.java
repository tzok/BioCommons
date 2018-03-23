package pl.poznan.put.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DistanceMatrix {
  private final List<String> names;
  private final double[][] matrix;

  public DistanceMatrix(final List<String> names, final double[][] matrix) {
    super();
    this.names = new ArrayList<>(names);
    this.matrix = matrix.clone();
  }

  public final List<String> getNames() {
    return Collections.unmodifiableList(names);
  }

  public final double[][] getMatrix() {
    return matrix.clone();
  }
}
