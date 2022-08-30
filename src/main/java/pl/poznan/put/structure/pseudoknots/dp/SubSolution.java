package pl.poznan.put.structure.pseudoknots.dp;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.immutables.value.Value;
import pl.poznan.put.structure.pseudoknots.Region;

/**
 * An entry in the dynamic programming matrix representing one possible solution of regions
 * assignment for keeping/removal.
 */
@Value.Immutable
abstract class SubSolution {
  public static SubSolution merge(final SubSolution left, final SubSolution below) {
    return ImmutableSubSolution.of(
        Stream.concat(left.regions().stream(), below.regions().stream())
            .collect(Collectors.toList()));
  }

  @Value.Parameter(order = 1)
  public abstract List<Region> regions();

  @Value.Lazy
  public int score() {
    return regions().stream().mapToInt(Region::length).sum();
  }

  @Value.Lazy
  public int lowestEndpoint() {
    return regions().stream().map(Region::begin).min(Integer::compareTo).orElse(Integer.MAX_VALUE);
  }

  @Value.Lazy
  public int highestEndpoint() {
    return regions().stream().map(Region::end).max(Integer::compareTo).orElse(Integer.MIN_VALUE);
  }
}
