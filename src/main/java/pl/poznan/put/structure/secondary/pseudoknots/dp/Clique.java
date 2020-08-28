package pl.poznan.put.structure.secondary.pseudoknots.dp;

import org.apache.commons.collections4.map.MultiKeyMap;
import pl.poznan.put.structure.secondary.pseudoknots.Region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A collection of regions conflicting with each other. Precisely, this is not a real clique. Each
 * region in this collection has conflict with *at least one* other region. It does not mean that
 * every region conflicts with every other!
 */
public class Clique {
  private final MultiKeyMap<Integer, Region> beginEndRegion = new MultiKeyMap<>();
  private final List<Integer> sortedEndpoints = new ArrayList<>();

  private final Set<Region> regions;

  public Clique(final Set<Region> regions) {
    super();
    this.regions = new HashSet<>(regions);

    for (final Region region : regions) {
      beginEndRegion.put(region.getBegin(), region.getEnd(), region);
      sortedEndpoints.add(region.getBegin());
      sortedEndpoints.add(region.getEnd());
    }
    Collections.sort(sortedEndpoints);
  }

  public final Set<Region> getRegions() {
    return Collections.unmodifiableSet(regions);
  }

  public final Region findRegion(final int begin, final int end) {
    return beginEndRegion.get(begin, end);
  }

  public final int getEndpoint(final int i) {
    return sortedEndpoints.get(i);
  }

  public final int endpointCount() {
    return sortedEndpoints.size();
  }

  public final int indexOfEndpoint(final int endpoint) {
    return sortedEndpoints.indexOf(endpoint);
  }

  public final int size() {
    return regions.size();
  }

  @Override
  public final String toString() {
    return String.format(
        "Clique{firstEndpoint=%d, lastEndpoint=%d, " + "regionCount=%d}",
        sortedEndpoints.get(0), sortedEndpoints.get(sortedEndpoints.size() - 1), regions.size());
  }
}
