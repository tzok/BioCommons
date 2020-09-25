package pl.poznan.put.structure.pseudoknots.dp;

import org.immutables.value.Value;
import pl.poznan.put.structure.pseudoknots.Region;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A component of a single entry in the dynamic programming matrix representing one possible
 * solution of regions assignment for keeping/removal.
 */
@Value.Immutable
public abstract class SubSolution {
  public static SubSolution merge(final SubSolution left, final SubSolution below) {
    return ImmutableSubSolution.of(
        Stream.concat(left.regions().stream(), below.regions().stream())
            .collect(Collectors.toList()));
  }

  @Value.Parameter(order = 1)
  public abstract List<Region> regions();

  @Value.Lazy
  public int score() {
    return regions().stream().map(Region::getLength).reduce(Integer::sum).orElse(0);
  }

  @Value.Lazy
  public int lowestEndpoint() {
    return regions().stream()
        .map(Region::getBegin)
        .min(Comparator.comparingInt(Integer::intValue))
        .orElse(Integer.MAX_VALUE);
  }

  @Value.Lazy
  public int highestEndpoint() {
    return regions().stream()
        .map(Region::getEnd)
        .max(Comparator.comparingInt(Integer::intValue))
        .orElse(Integer.MIN_VALUE);
  }
}
