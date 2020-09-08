package pl.poznan.put.pdb.analysis;

import org.immutables.value.Value;
import pl.poznan.put.circular.ImmutableAngle;
import pl.poznan.put.pdb.ChainNumberICode;
import pl.poznan.put.pdb.PdbResidueIdentifier;
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

@Value.Immutable
public abstract class PdbCompactFragment implements ResidueCollection {
  @Value.Parameter(order = 1)
  public abstract String name();

  @Value.Parameter(order = 2)
  public abstract List<PdbResidue> residues();

  public final PdbCompactFragment shifted(final int shift, final int size) {
    return ImmutablePdbCompactFragment.of(name(), residues().subList(shift, shift + size));
  }

  @Value.Lazy
  public Set<TorsionAngleType> commonTorsionAngleTypes() {
    return mapResidueAngleValue().values().stream()
        .flatMap(Collection::stream)
        .map(TorsionAngleValue::getAngleType)
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

      mapResidueAngleValue.put(residue.toResidueIdentifer(), values);
    }

    return mapResidueAngleValue;
  }

  public final TorsionAngleValue torsionAngleValue(
      final ChainNumberICode chainNumberICode, final MasterTorsionAngleType masterType) {
    final Collection<? extends TorsionAngleType> angleTypes = masterType.angleTypes();

    for (final TorsionAngleValue angleValue :
        mapResidueAngleValue().get(chainNumberICode.toResidueIdentifer())) {
      for (final TorsionAngleType angleType : angleTypes) {
        if (Objects.equals(angleType, angleValue.getAngleType())) {
          return angleValue;
        }
      }
    }

    final TorsionAngleType first = angleTypes.iterator().next();
    return new TorsionAngleValue(first, ImmutableAngle.of(Double.NaN));
  }

  public final MoleculeType moleculeType() {
    // in compact fragment, all residues have the same molecule type
    return residues().get(0).moleculeType();
  }

  @Override
  public final String toString() {
    final PdbResidue first = residues().get(0);
    final PdbResidue last = residues().get(residues().size() - 1);
    return first + " - " + last + " (count: " + residues().size() + ')';
  }
}
