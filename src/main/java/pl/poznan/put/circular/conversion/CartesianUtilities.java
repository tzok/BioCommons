package pl.poznan.put.circular.conversion;

import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.enums.ValueType;

/**
 * A class containg utility functions on the edge between cartesian and trigonometric
 * representations.
 */
public final class CartesianUtilities {
  private CartesianUtilities() {
    super();
  }

  public static void main(final String[] args) {
    final Vector3D coordA = new Vector3D(50.626, 49.730, 50.573); // P
    final Vector3D coordB = new Vector3D(50.161, 49.136, 52.023); // O5'
    final Vector3D coordC = new Vector3D(50.216, 49.948, 53.210); // C5'
    final Vector3D expectedCoordD = new Vector3D(50.968, 49.231, 54.309); // C4'
    final Vector3D actualCoordD =
        CartesianUtilities.nextPlacement(
            coordA,
            coordB,
            coordC,
            1.512413303300389,
            new Angle(110.54, ValueType.DEGREES),
            new Angle(-128.05, ValueType.DEGREES));

    final Vector3D vectorBC = coordC.subtract(coordB);
    final Vector3D vectorCD = expectedCoordD.subtract(coordC).negate();

    System.out.println("Expected: " + expectedCoordD);
    System.out.println("Length:   " + Vector3D.distance(coordC, expectedCoordD));
    System.out.println("Angle:    " + Angle.betweenPoints(coordB, coordC, expectedCoordD));
    System.out.println("Torsion:  " + Angle.torsionAngle(coordA, coordB, coordC, expectedCoordD));
    System.out.println();
    System.out.println("Actual:   " + actualCoordD);
    System.out.println("Length:   " + Vector3D.distance(coordC, actualCoordD));
    System.out.println("Angle:    " + Angle.betweenPoints(coordB, coordC, actualCoordD));
    System.out.println("Torsion:  " + Angle.torsionAngle(coordA, coordB, coordC, actualCoordD));
  }

  /**
   * Compute position of atom D, if you know positions of A, B and C, and the length of bond B-C,
   * and and the angle B-C-D and the torsion angle A-B-C-D.
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
            plane.getNormal().normalize(),
            angleBCD.getRadians(),
            RotationConvention.VECTOR_OPERATOR);
    final Rotation rotationSecond =
        new Rotation(unitBC, torsionABCD.getRadians(), RotationConvention.VECTOR_OPERATOR);

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
