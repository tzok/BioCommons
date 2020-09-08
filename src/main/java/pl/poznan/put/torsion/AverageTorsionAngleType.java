package pl.poznan.put.torsion;

import org.immutables.value.Value;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.samples.AngleSample;
import pl.poznan.put.circular.samples.ImmutableAngleSample;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.torsion.range.Range;
import pl.poznan.put.torsion.range.TorsionRange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

@Value.Immutable
public abstract class AverageTorsionAngleType implements TorsionAngleType, MasterTorsionAngleType {
  @Value.Parameter(order = 1)
  public abstract MoleculeType moleculeType();

  @Override
  public final TorsionAngleValue calculate(
      final List<? extends PdbResidue> residues, final int currentIndex) {
    final PdbResidue residue = residues.get(currentIndex);
    final Collection<Angle> angles = new ArrayList<>();

    for (final TorsionAngleType type : residue.torsionAngleTypes()) {
      for (final MasterTorsionAngleType masterType : consideredAngles()) {
        if (masterType.angleTypes().contains(type)) {
          final TorsionAngleValue angleValue = type.calculate(residues, currentIndex);
          angles.add(angleValue.getValue());
        }
      }
    }

    final AngleSample angleSample = ImmutableAngleSample.of(angles);
    return new TorsionAngleValue(this, angleSample.meanDirection());
  }

  @Value.Parameter(order = 2)
  public abstract List<MasterTorsionAngleType> consideredAngles();

  @Override
  @Value.Lazy
  public String shortDisplayName() {
    final Collection<String> angleNames = new LinkedHashSet<>();
    for (final MasterTorsionAngleType angleType : consideredAngles()) {
      angleNames.add(angleType.shortDisplayName());
    }

    final StringBuilder builder = new StringBuilder("MCQ(");
    final Iterator<String> iterator = angleNames.iterator();
    builder.append(iterator.next());

    while (iterator.hasNext()) {
      builder.append(", ");
      builder.append(iterator.next());
    }

    builder.append(')');
    return builder.toString();
  }

  @Override
  public String longDisplayName() {
    return shortDisplayName();
  }

  @Override
  @Value.Lazy
  public String exportName() {
    final Collection<String> angleNames = new LinkedHashSet<>();
    for (final MasterTorsionAngleType angleType : consideredAngles()) {
      angleNames.add(angleType.exportName());
    }

    final StringBuilder builder = new StringBuilder("MCQ_");
    final Iterator<String> iterator = angleNames.iterator();
    builder.append(iterator.next());

    while (iterator.hasNext()) {
      builder.append('_');
      builder.append(iterator.next());
    }

    return builder.toString();
  }

  public final TorsionAngleValue calculate(final Iterable<? extends TorsionAngleValue> values) {
    final Collection<Angle> angles = new ArrayList<>();

    for (final MasterTorsionAngleType masterType : consideredAngles()) {
      for (final TorsionAngleValue angleValue : values) {
        if (masterType.angleTypes().contains(angleValue.getAngleType())) {
          if (angleValue.getValue().isValid()) {
            angles.add(angleValue.getValue());
          }
        }
      }
    }

    final AngleSample angleSample = ImmutableAngleSample.of(angles);
    return new TorsionAngleValue(this, angleSample.meanDirection());
  }

  @Override
  public final List<TorsionAngleType> angleTypes() {
    return Collections.singletonList(this);
  }

  @Override
  public final Range range(final Angle angle) {
    return TorsionRange.getProvider().fromAngle(angle);
  }
}
