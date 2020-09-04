package pl.poznan.put.pdb.analysis;

import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.circular.ImmutableAngle;
import pl.poznan.put.pdb.ChainNumberICode;
import pl.poznan.put.pdb.PdbResidueIdentifier;
import pl.poznan.put.torsion.MasterTorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class PdbCompactFragment implements ResidueCollection {
  private static final Logger LOGGER = LoggerFactory.getLogger(PdbCompactFragment.class);

  @Value.Parameter
  public abstract String name();

  @Value.Parameter
  public abstract List<PdbResidue> residues();

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

  public final String toPdb() {
    return residues().stream().map(PdbResidue::toPdb).collect(Collectors.joining());
  }

  public final String toSequence() {
    return residues().stream()
        .map(residue -> String.valueOf(residue.oneLetterName()))
        .collect(Collectors.joining());
  }

  public final PdbCompactFragment shift(final int shift, final int size) {
    return ImmutablePdbCompactFragment.of(name(), residues().subList(shift, shift + size));
  }

  public final Set<TorsionAngleType> commonTorsionAngleTypes() {
    final Set<TorsionAngleType> set = new LinkedHashSet<>();
    mapResidueAngleValue().values().stream()
        .flatMap(Collection::stream)
        .map(TorsionAngleValue::getAngleType)
        .forEach(set::add);
    return set;
  }

  public final TorsionAngleValue getTorsionAngleValue(
      final ChainNumberICode chainNumberICode, final MasterTorsionAngleType masterType) {
    final Collection<? extends TorsionAngleType> angleTypes = masterType.getAngleTypes();

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

  public final MoleculeType getMoleculeType() {
    // in compact fragment, all residues have the same molecule type
    return residues().get(0).getMoleculeType();
  }

  @Override
  public final String toString() {
    final PdbResidue first = residues().get(0);
    final PdbResidue last = residues().get(residues().size() - 1);
    return first + " - " + last + " (count: " + residues().size() + ')';
  }
}
