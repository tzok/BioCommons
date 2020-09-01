package pl.poznan.put.atom;

import org.immutables.value.Value;

/** Information about atomic bond length. */
@Value.Immutable
public interface BondLength {
  /** @return The minimum length. */
  @Value.Parameter
  double min();

  /** @return The naximum length. */
  @Value.Parameter
  double max();

  /** @return The average length. */
  @Value.Parameter
  double avg();
}
