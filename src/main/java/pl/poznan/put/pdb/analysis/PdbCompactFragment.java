package pl.poznan.put.pdb.analysis;

import org.immutables.value.Value;
import pl.poznan.put.circular.ImmutableAngle;
import pl.poznan.put.pdb.ChainNumberICode;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.torsion.ImmutableTorsionAngleValue;
import pl.poznan.put.torsion.MasterTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/** A collection of residues such that (i, i+1) are connected. */
@Value.Immutable
public abstract class PdbCompactFragment implements SingleTypedResidueCollection {
  @Value.Parameter(order = 1)
  public abstract List<PdbResidue> residues();

  public final PdbCompactFragment shifted(final int shift, final int size) {
    return ImmutablePdbCompactFragment.of(residues().subList(shift, shift + size));
  }

  @Value.Lazy
  public Set<TorsionAngleType> commonTorsionAngleTypes() {
    return mapResidueAngleValue().values().stream()
        .flatMap(Collection::stream)
        .map(TorsionAngleValue::angleType)
        .collect(Collectors.toSet());
  }

  @Value.Lazy
  public Map<PdbResidueIdentifier, List<TorsionAngleValue>> mapResidueAngleValue() {
    final Map<PdbResidueIdentifier, List<TorsionAngleValue>> mapResidueAngleValue =
        new LinkedHashMap<>();

    for (int i = 0; i < residues().size(); i++) {
      final PdbResidue residue = residues().get(i);
      final List<TorsionAngleValue> values = new ArrayList<>();

      for (final TorsionAngleType type : residue.torsionAngleTypes()) {
        final TorsionAngleValue value = type.calculate(residues(), i);
        values.add(value);
      }

      mapResidueAngleValue.put(PdbResidueIdentifier.from(residue), values);
    }

    return mapResidueAngleValue;
  }

  public final TorsionAngleValue torsionAngleValue(
      final ChainNumberICode chainNumberICode, final MasterTorsionAngleType masterType) {
    final Collection<? extends TorsionAngleType> angleTypes = masterType.angleTypes();

    for (final TorsionAngleValue angleValue :
        mapResidueAngleValue().get(PdbResidueIdentifier.from(chainNumberICode))) {
      for (final TorsionAngleType angleType : angleTypes) {
        if (Objects.equals(angleType, angleValue.angleType())) {
          return angleValue;
        }
      }
    }

    final TorsionAngleType first = angleTypes.iterator().next();
    return ImmutableTorsionAngleValue.of(first, ImmutableAngle.of(Double.NaN));
  }

  @Override
  public final String toString() {
    final PdbResidue first = residues().get(0);
    final PdbResidue last = residues().get(residues().size() - 1);
    return first + " - " + last + " (count: " + residues().size() + ')';
  }
}
