package pl.poznan.put.circular;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
interface Bin {
  @Value.Parameter
  double radiansStart();

  @Value.Parameter
  List<Angle> data();
}
