package pl.poznan.put.types;

import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableDistanceMatrix.class)
@JsonDeserialize(as = ImmutableDistanceMatrix.class)
public interface DistanceMatrix {
  @Value.Parameter(order = 1)
  List<String> names();

  @Value.Parameter(order = 2)
  double[][] matrix();
}
