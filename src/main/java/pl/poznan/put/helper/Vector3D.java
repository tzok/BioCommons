package pl.poznan.put.helper;

import org.biojava.bio.structure.Atom;

/**
 * A vector in 3D space.
 * 
 * @author Tomasz Zok (tzok[at]cs.put.poznan.pl)
 */
class Vector3D {
    private double x, y, z;

    private Vector3D() {
    }

    /**
     * Create a mathematical vector from points represented by atoms in 3D
     * space.
     * 
     * @param a1
     *            Atom 1.
     * @param a2
     *            Atom 2.
     */
    public Vector3D(Atom a1, Atom a2) {
        x = a2.getX() - a1.getX();
        y = a2.getY() - a1.getY();
        z = a2.getZ() - a1.getZ();
    }

    /**
     * Calculate cross product of two vectors.
     * 
     * @param v
     *            Input vector.
     * @return Cross product of this object and vector in parameter.
     */
    public Vector3D cross(Vector3D v) {
        Vector3D result = new Vector3D();
        result.x = y * v.z - z * v.y;
        result.y = z * v.x - x * v.z;
        result.z = x * v.y - y * v.x;
        return result;
    }

    /**
     * Calculate dot product of two vectors.
     * 
     * @param v
     *            Input vector.
     * @return Dot product of this object and vector in parameter.
     */
    public double dot(Vector3D v) {
        return x * v.x + y * v.y + z * v.z;
    }

    /**
     * Returns length of this vector in euclidean space.
     * 
     * @return Vector length.
     */
    double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Scale vector by constant factor.
     * 
     * @param factor
     *            Scaling factor.
     * @return Vector with values scaled.
     */
    Vector3D scale(double factor) {
        Vector3D result = new Vector3D();
        result.x = x * factor;
        result.y = y * factor;
        result.z = z * factor;
        return result;
    }
}
