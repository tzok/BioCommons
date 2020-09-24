package pl.poznan.put.circular.conversion;

import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import pl.poznan.put.circular.Angle;

/** A set of utility functions on the edge between cartesian and trigonometric representations. */
public final class CartesianUtilities {
  private CartesianUtilities() {
    super();
  }

  /**
   * Computes position of atom D, if you know positions of A, B and C, and the length of bond B-C,
   * and the angle B-C-D and the torsion angle A-B-C-D.
   *
   * @param coordA Coordinates of atom A.
   * @param coordB Coordinates of atom B.
   * @param coordC Coordinates of atom C.
   * @param lengthCD Length of bond C-D.
   * @param angleBCD Angle between atoms B-C-D.
   * @param torsionABCD Torsion angle between atoms A-B-C-D.
   * @return Coordinate of atom D.
   */
  public static Vector3D nextPlacement(
      final Vector3D coordA,
      final Vector3D coordB,
      final Vector3D coordC,
      final double lengthCD,
      final Angle angleBCD,
      final Angle torsionABCD) {
    final Vector3D vectorBC = coordC.subtract(coordB);
    final Vector3D unitBC = vectorBC.normalize();
    final Plane plane = new Plane(coordC, coordB, coordA, 1.0e-3);

    final Rotation rotationFirst =
        new Rotation(
            plane.getNormal().normalize(), angleBCD.radians(), RotationConvention.VECTOR_OPERATOR);
    final Rotation rotationSecond =
        new Rotation(unitBC, torsionABCD.radians(), RotationConvention.VECTOR_OPERATOR);

    final Vector3D coordD0 = CartesianUtilities.coordD0(coordB, coordC, lengthCD);
    final Vector3D coordD1 = rotationFirst.applyTo(coordD0);
    final Vector3D coordD2 = rotationSecond.applyTo(coordD1);

    return coordD2.add(coordC);
  }

  private static Vector3D coordD0(
      final Vector<Euclidean3D> coordB, final Vector<Euclidean3D> coordC, final double lengthCD) {
    return (Vector3D) coordB.subtract(coordC).normalize().scalarMultiply(lengthCD);
  }
}
