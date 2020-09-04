package pl.poznan.put.torsion;

import lombok.EqualsAndHashCode;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.samples.AngleSample;
import pl.poznan.put.circular.samples.ImmutableAngleSample;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.torsion.range.Range;
import pl.poznan.put.torsion.range.TorsionRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
public class AverageTorsionAngleType extends TorsionAngleType implements MasterTorsionAngleType {
  private final String displayName;
  private final String exportName;
  private final List<? extends MasterTorsionAngleType> consideredAngles;

  public AverageTorsionAngleType(
      final MoleculeType moleculeType, final MasterTorsionAngleType... masterTypes) {
    super(moleculeType);
    consideredAngles = Arrays.asList(masterTypes);
    displayName = AverageTorsionAngleType.toDisplayName(consideredAngles);
    exportName = AverageTorsionAngleType.toExportName(consideredAngles);
  }

  public AverageTorsionAngleType(
      final MoleculeType moleculeType, final List<MasterTorsionAngleType> consideredAngles) {
    super(moleculeType);
    this.consideredAngles = new ArrayList<>(consideredAngles);
    displayName = AverageTorsionAngleType.toDisplayName(consideredAngles);
    exportName = AverageTorsionAngleType.toExportName(consideredAngles);
  }

  private AverageTorsionAngleType(
      final MoleculeType moleculeType,
      final List<? extends MasterTorsionAngleType> consideredAngles,
      final String displayName,
      final String exportName) {
    super(moleculeType);
    this.consideredAngles = consideredAngles;
    this.displayName = displayName;
    this.exportName = exportName;
  }

  private static String toDisplayName(
      final Iterable<? extends MasterTorsionAngleType> consideredAngles) {
    final Collection<String> angleNames = new LinkedHashSet<>();
    for (final MasterTorsionAngleType angleType : consideredAngles) {
      angleNames.add(angleType.getShortDisplayName());
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

  private static String toExportName(
      final Iterable<? extends MasterTorsionAngleType> consideredAngles) {
    final Collection<String> angleNames = new LinkedHashSet<>();
    for (final MasterTorsionAngleType angleType : consideredAngles) {
      angleNames.add(angleType.getExportName());
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

  @Override
  public final String getLongDisplayName() {
    return displayName;
  }

  @Override
  public final String getShortDisplayName() {
    return displayName;
  }

  @Override
  public final String getExportName() {
    return exportName;
  }

  @Override
  public final String toString() {
    return displayName;
  }

  public final List<MasterTorsionAngleType> getConsideredAngles() {
    return Collections.unmodifiableList(consideredAngles);
  }

  @Override
  public final TorsionAngleValue calculate(
      final List<? extends PdbResidue> residues, final int currentIndex) {
    final PdbResidue residue = residues.get(currentIndex);
    final List<Angle> angles = new ArrayList<>();

    for (final TorsionAngleType type : residue.torsionAngleTypes()) {
      for (final MasterTorsionAngleType masterType : consideredAngles) {
        if (masterType.getAngleTypes().contains(type)) {
          final TorsionAngleValue angleValue = type.calculate(residues, currentIndex);
          angles.add(angleValue.getValue());
        }
      }
    }

    final AngleSample angleSample = ImmutableAngleSample.of(angles);
    return new TorsionAngleValue(this, angleSample.meanDirection());
  }

  public final TorsionAngleValue calculate(
      final Iterable<? extends TorsionAngleValue> values) {
    final List<Angle> angles = new ArrayList<>();

    for (final MasterTorsionAngleType masterType : consideredAngles) {
      for (final TorsionAngleValue angleValue : values) {
        if (masterType.getAngleTypes().contains(angleValue.getAngleType())) {
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
  public final List<TorsionAngleType> getAngleTypes() {
    return Collections.singletonList(this);
  }

  @Override
  public final Range getRange(final Angle angle) {
    return TorsionRange.getProvider().fromAngle(angle);
  }
}
