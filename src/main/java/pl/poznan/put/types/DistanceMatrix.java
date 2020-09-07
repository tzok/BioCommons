package pl.poznan.put.types;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface DistanceMatrix {
  @Value.Parameter
  List<String> names();

  @Value.Parameter
  double[][] matrix();
}
