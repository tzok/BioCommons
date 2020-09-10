package pl.poznan.put.atom;

import org.immutables.value.Value;

/** An atomic bond length. */
@Value.Immutable
public interface BondLength {
  /** @return The minimum length. */
  @Value.Parameter(order = 1)
  double min();

  /** @return The maximum length. */
  @Value.Parameter(order = 2)
  double max();

  /** @return The average length. */
  @Value.Parameter(order = 3)
  double avg();
}
