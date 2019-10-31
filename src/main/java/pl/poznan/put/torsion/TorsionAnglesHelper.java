package pl.poznan.put.torsion;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.FastMath;
import pl.poznan.put.circular.Angle;
import pl.poznan.put.circular.enums.ValueType;
import pl.poznan.put.pdb.PdbAtomLine;
import pl.poznan.put.types.Quadruplet;

/**
 * A class to calculate and manage dihedral angles for given BioJava structure.
 *
 * @author Tomasz Zok (tzok[at]cs.put.poznan.pl)
 */
public final class TorsionAnglesHelper {
  private TorsionAnglesHelper() {
    super();
  }

  /**
   * Calculate one dihedral angle value. By default use the atan method.
   *
   * @param atoms A 4-tuple of atoms.
   * @return Value of the tosion angle.
   */
  public static Angle calculateTorsionAngle(final Quadruplet<? extends PdbAtomLine> atoms) {
    return TorsionAnglesHelper.calculateTorsionAngle(atoms.a, atoms.b, atoms.c, atoms.d);
  }

  /**
   * Calculate one dihedral angle value. By default use the atan method.
   *
   * @param a1 Atom 1.
   * @param a2 Atom 2.
   * @param a3 Atom 3.
   * @param a4 Atom 4.
   * @return Value of the torsion angle.
   */
  public static Angle calculateTorsionAngle(
      final PdbAtomLine a1, final PdbAtomLine a2, final PdbAtomLine a3, final PdbAtomLine a4) {
    return TorsionAnglesHelper.calculateTorsionAtan(a1, a2, a3, a4);
  }

  /**
   * Calculate one dihedral angle value for given four atoms.
   *
   * @param a1 Atom 1.
   * @param a2 Atom 2.
   * @param a3 Atom 3.
   * @param a4 Atom 4.
   * @return Dihedral angle between atoms 1-4.
   */
  private static Angle calculateTorsionAtan(
      final PdbAtomLine a1, final PdbAtomLine a2, final PdbAtomLine a3, final PdbAtomLine a4) {
    if ((a1 == null) || (a2 == null) || (a3 == null) || (a4 == null)) {
      return Angle.invalidInstance();
    }

    final Vector3D v1 = new Vector3D(a1.getX(), a1.getY(), a1.getZ());
    final Vector3D v2 = new Vector3D(a2.getX(), a2.getY(), a2.getZ());
    final Vector3D v3 = new Vector3D(a3.getX(), a3.getY(), a3.getZ());
    final Vector3D v4 = new Vector3D(a4.getX(), a4.getY(), a4.getZ());
    return Angle.torsionAngle(v1, v2, v3, v4);
  }

  public static Vector3D atomDistance(final PdbAtomLine a, final PdbAtomLine b) {
    final Vector3D va = new Vector3D(a.getX(), a.getY(), a.getZ());
    final Vector3D vb = new Vector3D(b.getX(), b.getY(), b.getZ());
    return vb.subtract(va);
  }

  /**
   * Calculate one dihedral angle value for given four atoms. Use cos^-1 and a check for
   * pseudovector
   *
   * @param a1 Atom 1.
   * @param a2 Atom 2.
   * @param a3 Atom 3.
   * @param a4 Atom 4.
   * @return Dihedral angle between atoms 1-4.
   */
  public static Angle calculateTorsionAcos(
      final PdbAtomLine a1, final PdbAtomLine a2, final PdbAtomLine a3, final PdbAtomLine a4) {
    if ((a1 == null) || (a2 == null) || (a3 == null) || (a4 == null)) {
      return Angle.invalidInstance();
    }

    final Vector3D d1 = TorsionAnglesHelper.atomDistance(a1, a2);
    final Vector3D d2 = TorsionAnglesHelper.atomDistance(a2, a3);
    final Vector3D d3 = TorsionAnglesHelper.atomDistance(a3, a4);

    final Vector3D u1 = d1.crossProduct(d2);
    final Vector3D u2 = d2.crossProduct(d3);

    double ctor = u1.dotProduct(u2) / FastMath.sqrt(u1.dotProduct(u1) * u2.dotProduct(u2));
    ctor = ctor < -1.0 ? -1.0 : Math.min(ctor, 1.0);
    double torp = FastMath.acos(ctor);
    if (u1.dotProduct(u2.crossProduct(d2)) < 0) {
      torp = -torp;
    }
    return new Angle(torp, ValueType.RADIANS);
  }

  public static double subtractTorsions(final double a1, final double a2) {
    if (Double.isNaN(a1) || Double.isNaN(a2)) {
      return Double.NaN;
    }

    final double full = 2.0 * Math.PI;
    final double a1Mod = (a1 + full) % full;
    final double a2Mod = (a2 + full) % full;
    double diff = Math.abs(a1Mod - a2Mod);
    diff = Math.min(diff, full - diff);
    return diff;
  }
}
