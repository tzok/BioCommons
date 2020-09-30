package pl.poznan.put.structure.pseudoknots.dp;

import org.immutables.value.Value;
import pl.poznan.put.structure.pseudoknots.Region;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A collection of regions conflicting with each other. Precisely, this is not a real clique. Each
 * region in this collection has conflict with <strong>at least one</strong> other region. It does
 * not mean that every region conflicts with every other.
 */
@Value.Immutable
public abstract class ConflictClique {
  /** @return The set of regions in this conflict clique. */
  @Value.Parameter(order = 1)
  public abstract Set<Region> regions();

  /**
   * Searches for a region given its begin and end indices.
   *
   * @param begin The index of beginning nucleotide.
   * @param end The index of ending nucleotide.
   * @return The region which matches the given {@code begin} and {@code end}.
   */
  public final Optional<Region> findRegion(final int begin, final int end) {
    return regions().stream()
        .filter(region -> region.begin() == begin)
        .filter(region -> region.end() == end)
        .findFirst();
  }

  /**
   * Returns the endpoint of the given index. An endpoint is a nucleotide index in the original
   * BPSEQ structure which matches a 5' or 3' partner of a base pair.
   *
   * @param index The index of an endpoint.
   * @return The nucleotide index in the original BPSEQ structure.
   */
  public final int endpoint(final int index) {
    return sortedEndpoints().get(index);
  }

  /** @return The number of endpoints (should be equal to the number of regions times two). */
  public final int endpointCount() {
    return sortedEndpoints().size();
  }

  /**
   * Finds the index of an endpoint in this conflict clique.
   *
   * @param endpoint The value of an endpoint.
   * @return The index of this endpoint in this conflict clique.
   */
  public final int indexOfEndpoint(final int endpoint) {
    return sortedEndpoints().indexOf(endpoint);
  }

  /** @return The number of regions. */
  public final int size() {
    return regions().size();
  }

  @Value.Lazy
  protected List<Integer> sortedEndpoints() {
    return regions().stream()
        .flatMap(region -> Stream.of(region.begin(), region.end()))
        .sorted(Integer::compareTo)
        .collect(Collectors.toList());
  }
}
