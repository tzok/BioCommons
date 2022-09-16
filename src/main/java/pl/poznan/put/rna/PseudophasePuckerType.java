package pl.poznan.put.rna;

import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.math3.util.FastMath;
import org.immutables.value.Value;
import pl.poznan.put.circular.ImmutableAngle;
import pl.poznan.put.pdb.analysis.MoleculeType;
import pl.poznan.put.pdb.analysis.PdbResidue;
import pl.poznan.put.torsion.ImmutableTorsionAngleValue;
import pl.poznan.put.torsion.TorsionAngleType;
import pl.poznan.put.torsion.TorsionAngleValue;

/** An angle describing the pseudophase pucker. */
@Value.Immutable(singleton = true)
public abstract class PseudophasePuckerType implements TorsionAngleType {
  @Override
  public final String shortDisplayName() {
    return "P";
  }

  @Override
  public final String longDisplayName() {
    return "P";
  }

  @Override
  public final String exportName() {
    return "P";
  }

  @Override
  public final MoleculeType moleculeType() {
    return MoleculeType.RNA;
  }

  /**
   * Calculates the value of pseudophase pucker according to the formula in Saenger's "Principles of
   * ...". Namely: atan2((nu1 + nu4) - (nu0 - nu3), nu2 * 2.0 * sin(36 deg) + sin(72 deg))
   *
   * @param residues The list of residues.
   * @param currentIndex The index of residue on the list.
   * @return The value of pseudophase pucker angle.
   */
  @Override
  public final TorsionAngleValue calculate(
      final List<PdbResidue> residues, final int currentIndex) {
    final TorsionAngleValue nu0 =
        NucleotideTorsionAngle.NU0.angleTypes().get(0).calculate(residues, currentIndex);
    final TorsionAngleValue nu1 =
        NucleotideTorsionAngle.NU1.angleTypes().get(0).calculate(residues, currentIndex);
    final TorsionAngleValue nu2 =
        NucleotideTorsionAngle.NU2.angleTypes().get(0).calculate(residues, currentIndex);
    final TorsionAngleValue nu3 =
        NucleotideTorsionAngle.NU3.angleTypes().get(0).calculate(residues, currentIndex);
    final TorsionAngleValue nu4 =
        NucleotideTorsionAngle.NU4.angleTypes().get(0).calculate(residues, currentIndex);

    if (Stream.of(nu0, nu1, nu2, nu3, nu4)
        .map(TorsionAngleValue::value)
        .anyMatch(angle -> !angle.isValid())) {
      return ImmutableTorsionAngleValue.of(this, ImmutableAngle.of(Double.NaN));
    }

    final double scale =
        2.0 * (FastMath.sin(FastMath.toRadians(36.0)) + FastMath.sin(FastMath.toRadians(72.0)));
    final double y =
        (nu1.value().radians() + nu4.value().radians())
            - (nu0.value().radians() + nu3.value().radians());
    final double x = nu2.value().radians() * scale;
    return ImmutableTorsionAngleValue.of(this, ImmutableAngle.of(FastMath.atan2(y, x)));
  }
}
