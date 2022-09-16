package pl.poznan.put.types;

import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
public interface DistanceMatrix {
  @Value.Parameter(order = 1)
  List<String> names();

  @Value.Parameter(order = 2)
  double[][] matrix();
}
