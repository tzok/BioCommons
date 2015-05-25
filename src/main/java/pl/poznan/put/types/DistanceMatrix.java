package pl.poznan.put.types;

import java.util.Collections;
import java.util.List;

public class DistanceMatrix {
    private final List<String> names;
    private final double[][] matrix;

    public DistanceMatrix(List<String> names, double[][] matrix) {
        super();
        this.names = names;
        this.matrix = matrix.clone();
    }

    public List<String> getNames() {
        return Collections.unmodifiableList(names);
    }

    public double[][] getMatrix() {
        return matrix;
    }
}
