package pl.poznan.put.types;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class DistanceMatrixTest {
  private final List<String> names = Arrays.asList("A", "B", "C");
  private final double[][] data = {{0, 1, 2}, {1, 0, 3}, {2, 3, 0}};
  private final DistanceMatrix distanceMatrix = new DistanceMatrix(names, data);

  @Test
  public final void getNames() {
    final List<String> matrixNames = distanceMatrix.getNames();
    assertEquals(3, matrixNames.size());
    assertEquals("A", matrixNames.get(0));
    assertEquals("B", matrixNames.get(1));
    assertEquals("C", matrixNames.get(2));
  }

  @Test
  public final void getMatrix() {
    final double[][] matrix = distanceMatrix.getMatrix();
    assertEquals(3, matrix.length);

    for (int i = 0; i < 3; i++) {
      assertEquals(3, matrix[i].length);
      for (int j = 0; j < 3; j++) {
        assertEquals(data[i][j], matrix[i][j], 1.0e-3);
      }
    }
  }
}
