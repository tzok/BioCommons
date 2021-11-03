package pl.poznan.put.rna;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import org.immutables.value.Value;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.ImmutableAngle;

@Value.Immutable
public abstract class BasePairParameters {
  public static BasePairParameters of(
      final StandardReferenceFrame primary, final StandardReferenceFrame secondary) {
    final StandardReferenceFrame corrected =
        primary.z().dotProduct(secondary.z()) > 0
            ? secondary
            : ImmutableStandardReferenceFrame.copyOf(secondary)
                .withY(secondary.y().negate())
                .withZ(secondary.z().negate());

    final StandardReferenceFrame pairFrame = StandardReferenceFrame.ofBasePair(primary, corrected);

    final double shear = pairFrame.origin().dotProduct(pairFrame.x());
    final double stretch = pairFrame.origin().dotProduct(pairFrame.y());
    final double stagger = pairFrame.origin().dotProduct(pairFrame.z());
    final Angle buckle =
        Angle.torsionAngle(primary.z().negate(), pairFrame.x().negate(), corrected.z());
    final Angle propeller =
        Angle.torsionAngle(primary.z().negate(), pairFrame.y().negate(), corrected.z());
    final Angle opening =
        Angle.torsionAngle(primary.y().negate(), pairFrame.z().negate(), corrected.y());
    final double interBaseAngle =
        FastMath.sqrt(
            buckle.radians() * buckle.radians() + propeller.radians() * propeller.radians());

    final double correction = Vector3D.angle(primary.z(), corrected.z()) / interBaseAngle;

    return ImmutableBasePairParameters.of(
        shear,
        stretch,
        stagger,
        ImmutableAngle.of(buckle.radians() * correction),
        ImmutableAngle.of(propeller.radians() * correction),
        opening,
        ImmutableAngle.of(interBaseAngle * correction));
  }

  @Value.Parameter(order = 1)
  public abstract double shear();

  @Value.Parameter(order = 2)
  public abstract double stretch();

  @Value.Parameter(order = 3)
  public abstract double stagger();

  @Value.Parameter(order = 4)
  public abstract Angle buckle();

  @Value.Parameter(order = 5)
  public abstract Angle propeller();

  @Value.Parameter(order = 6)
  public abstract Angle opening();

  @Value.Parameter(order = 7)
  public abstract Angle interBaseAngle();
}
