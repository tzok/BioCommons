package pl.poznan.put.types;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class DistanceMatrixTest {
  private final List<String> names = Arrays.asList("A", "B", "C");
  private final double[][] data = {{0, 1.0, 2.0}, {1.0, 0, 3.0}, {2.0, 3.0, 0}};
  private final DistanceMatrix distanceMatrix = new DistanceMatrix(names, data);

  @Test
  public final void getNames() {
    final List<String> matrixNames = distanceMatrix.getNames();
    assertThat(matrixNames.size(), is(3));
    assertThat(matrixNames.get(0), is("A"));
    assertThat(matrixNames.get(1), is("B"));
    assertThat(matrixNames.get(2), is("C"));
  }

  @Test
  public final void getMatrix() {
    final double[][] matrix = distanceMatrix.getMatrix();
    assertThat(matrix.length, is(3));

    for (int i = 0; i < 3; i++) {
      assertThat(matrix[i].length, is(3));
      for (int j = 0; j < 3; j++) {
        assertEquals(data[i][j], matrix[i][j], 1.0e-3);
      }
    }
  }
}
