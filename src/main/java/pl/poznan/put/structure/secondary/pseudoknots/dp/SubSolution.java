package pl.poznan.put.structure.secondary.pseudoknots.dp;

import lombok.EqualsAndHashCode;
import pl.poznan.put.structure.secondary.pseudoknots.Region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A component of a single entry in the dynamic programming matrix representing one possible
 * solution of regions assignment for keeping/removal.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SubSolution {
  @EqualsAndHashCode.Include private final List<Region> regions;
  private final int lowestEndpoint;
  private final int highestEndpoint;
  private final int score;

  public SubSolution(final Region region) {
    this(Collections.singletonList(region));
  }

  private SubSolution(final List<Region> regions) {
    super();
    this.regions = Collections.unmodifiableList(regions);

    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    int sum = 0;

    for (final Region region : regions) {
      min = Math.min(min, region.getBegin());
      max = Math.max(max, region.getEnd());
      sum += region.getLength();
    }

    lowestEndpoint = min;
    highestEndpoint = max;
    score = sum;
  }

  public static SubSolution merge(final SubSolution left, final SubSolution below) {
    final List<Region> regions = new ArrayList<>(left.regions);
    regions.addAll(below.regions);
    return new SubSolution(regions);
  }

  @Override
  public final String toString() {
    return String.format(
        "SubSolution{regionCount=%d, lowestEndpoint=%d, " + "highestEndpoint=%d, score=%d}",
        regions.size(), lowestEndpoint, highestEndpoint, score);
  }

  public final Iterable<Region> getRegions() {
    return Collections.unmodifiableList(regions);
  }

  public final int getLowestEndpoint() {
    return lowestEndpoint;
  }

  public final int getHighestEndpoint() {
    return highestEndpoint;
  }

  public final int getScore() {
    return score;
  }
}
