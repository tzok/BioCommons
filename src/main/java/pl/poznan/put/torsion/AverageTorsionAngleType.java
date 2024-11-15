package pl.poznan.put.torsion;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.samples.AngleSample;
import pl.poznan.put.circular.samples.ImmutableAngleSample;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.torsion.range.Range;
import pl.poznan.put.torsion.range.TorsionRange;

/** An average of one or more basic angle types. */
@Value.Immutable
@JsonSerialize(as = ImmutableAverageTorsionAngleType.class)
@JsonDeserialize(as = ImmutableAverageTorsionAngleType.class)
public abstract class AverageTorsionAngleType implements TorsionAngleType, MasterTorsionAngleType {
  /**
   * @return The default instance for proteins based on {@link
   *     pl.poznan.put.protein.AminoAcidTorsionAngle#PHI}, {@link
   *     pl.poznan.put.protein.AminoAcidTorsionAngle#PSI}, and {@link
   *     pl.poznan.put.protein.AminoAcidTorsionAngle#OMEGA}.
   */
  public static AverageTorsionAngleType forProtein() {
    return ImmutableAverageTorsionAngleType.of(
        MoleculeType.PROTEIN, MoleculeType.PROTEIN.mainAngleTypes());
  }

  /**
   * @return The default instance for nucleic acids based on {@link
   *     pl.poznan.put.rna.NucleotideTorsionAngle#ALPHA}, {@link
   *     pl.poznan.put.rna.NucleotideTorsionAngle#BETA}, {@link
   *     pl.poznan.put.rna.NucleotideTorsionAngle#GAMMA}, {@link
   *     pl.poznan.put.rna.NucleotideTorsionAngle#DELTA}, {@link
   *     pl.poznan.put.rna.NucleotideTorsionAngle#EPSILON}, {@link
   *     pl.poznan.put.rna.NucleotideTorsionAngle#ZETA} and {@link
   *     pl.poznan.put.rna.NucleotideTorsionAngle#CHI}.
   */
  public static AverageTorsionAngleType forNucleicAcid() {
    return ImmutableAverageTorsionAngleType.of(MoleculeType.RNA, MoleculeType.RNA.mainAngleTypes());
  }

  @Override
  @Value.Parameter(order = 1)
  public abstract MoleculeType moleculeType();

  /**
   * Calculates the average torsion angle value by calculating basic angle values and creating an
   * {@link AngleSample} out of them to get the mean direction.
   *
   * @param residues The list of residues.
   * @param currentIndex The index of current residue.
   * @return The average value of torsion angles condigured for this type.
   */
  @Override
  public final TorsionAngleValue calculate(
      final List<PdbResidue> residues, final int currentIndex) {
    final List<Angle> angles =
        residues.get(currentIndex).residueInformationProvider().torsionAngleTypes().stream()
            .filter(consideredBasicAngleTypes()::contains)
            .map(angleType -> angleType.calculate(residues, currentIndex))
            .map(TorsionAngleValue::value)
            .collect(Collectors.toList());
    final AngleSample angleSample = ImmutableAngleSample.of(angles);
    return ImmutableTorsionAngleValue.of(this, angleSample.meanDirection());
  }

  /**
   * @return The list of angle types to calculate average from.
   */
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

  /**
   * Calculates the average torsion angle value by collecting basic angle values and creating an
   * {@link AngleSample} out of them to get the mean direction.
   *
   * @param values The collection of torsion angle values.
   * @return The average value of torsion angles condigured for this type.
   */
  public final TorsionAngleValue calculate(final Collection<TorsionAngleValue> values) {
    final List<Angle> angles =
        values.stream()
            .filter(angleValue -> consideredBasicAngleTypes().contains(angleValue.angleType()))
            .map(TorsionAngleValue::value)
            .collect(Collectors.toList());
    final AngleSample angleSample = ImmutableAngleSample.of(angles);
    return ImmutableTorsionAngleValue.of(this, angleSample.meanDirection());
  }

  @Override
  public final List<TorsionAngleType> angleTypes() {
    return Collections.singletonList(this);
  }

  @Override
  public final Range range(final Angle angle) {
    return TorsionRange.rangeProvider().fromAngle(angle);
  }

  @Value.Lazy
  protected Set<TorsionAngleType> consideredBasicAngleTypes() {
    return consideredAngles().stream()
        .map(MasterTorsionAngleType::angleTypes)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }
}
