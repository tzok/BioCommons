package pl.poznan.put.pdb.analysis;

import org.immutables.value.Value;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleValue;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** A collection of residues such that (i, i+1) are connected. */
@Value.Immutable
public abstract class PdbCompactFragment implements SingleTypedResidueCollection {
  @Override
  @Value.Parameter(order = 1)
  public abstract List<PdbResidue> residues();

  /** @return The name of this compact fragment. */
  @Value.Default
  public String name() {
    if (residues().isEmpty()) {
      return "";
    }
    return residues().get(0) + ":" + residues().size();
  }

  /**
   * Creates a new instance from this one which starts from a shifted position and has a limited
   * size.
   *
   * @param shift Starting position.
   * @param size Length of the new compact fragment.
   * @return An instance of this class created by taking a sublist of the residues.
   */
  public final PdbCompactFragment shifted(final int shift, final int size) {
    return ImmutablePdbCompactFragment.of(residues().subList(shift, shift + size));
  }

  /**
   * Computes torsion angles' values for a given residue.
   *
   * @param identifier A residue identifier.
   * @return An object containing values of torsion angles in the given residue.
   */
  public final ResidueTorsionAngles torsionAngles(final PdbResidueIdentifier identifier) {
    return angleValues().stream()
        .filter(angles -> Objects.equals(identifier, angles.identifier()))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Failed to find torsion angles values for residue: " + identifier));
  }

  @Override
  public final String toString() {
    final PdbResidue first = residues().get(0);
    final PdbResidue last = residues().get(residues().size() - 1);
    return first + " - " + last + " (count: " + residues().size() + ')';
  }

  @Value.Lazy
  protected Set<TorsionAngleType> angleTypes() {
    return angleValues().stream()
        .map(ResidueTorsionAngles::values)
        .flatMap(Collection::stream)
        .map(TorsionAngleValue::angleType)
        .collect(Collectors.toSet());
  }

  @Value.Lazy
  protected List<ResidueTorsionAngles> angleValues() {
    return IntStream.range(0, residues().size())
        .boxed()
        .map(i -> ResidueTorsionAngles.calculate(residues(), i))
        .collect(Collectors.toList());
  }
}
