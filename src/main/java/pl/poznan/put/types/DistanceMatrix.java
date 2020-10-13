package pl.poznan.put.types;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface DistanceMatrix {
  @Value.Parameter(order = 1)
  List<String> names();

  @Value.Parameter(order = 2)
  double[][] matrix();
}
