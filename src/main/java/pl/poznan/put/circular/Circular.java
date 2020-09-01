package pl.poznan.put.circular;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;
import pl.poznan.put.circular.enums.ValueType;
import pl.poznan.put.utility.AngleFormat;
import pl.poznan.put.utility.NumberFormatUtils;

import java.io.Serializable;

/**
 * An abstract class representing any circular value. Check {@link Angle} and {@link Axis} to see
 * specific subclasses.
 */
public abstract class Circular implements Comparable<Circular>, Serializable {
  private final double radians;

  Circular(final double value, final ValueType valueType) {
    super();
    radians = valueType.toRadians(value) % MathUtils.TWO_PI;
  }

  /** @return Value in radians in range [-pi; pi). */
  public final double getRadians() {
    return radians;
  }

  /** @return Value in degrees in range [-180; 180). */
  public final double getDegrees() {
    return FastMath.toDegrees(radians);
  }

  /** @return Value in degrees in range [0; 360). */
  public final double getDegrees360() {
    return FastMath.toDegrees(getRadians2PI());
  }

  /** @return Value in radians in range [0; 2pi). */
  public final double getRadians2PI() {
    return (radians < 0.0) ? (radians + MathUtils.TWO_PI) : radians;
  }

  @Override
  public final String toString() {
    if (isValid()) {
      return String.format(
          "%s rad, %s",
          NumberFormatUtils.threeDecimalDigits().format(radians),
          AngleFormat.degreesRoundedToHundredth(radians));
    }
    return "invalid";
  }

  /** @return True if value is not NaN. */
  public final boolean isValid() {
    return !Double.isNaN(radians);
  }

  @Override
  public final int compareTo(final Circular t) {
    return Double.compare(getRadians2PI(), t.getRadians2PI());
  }

  @Override
  public final boolean equals(final Object o) {
    if (this == o) return true;

    if (!(o instanceof Circular)) return false;

    final Circular circular = (Circular) o;
    return Precision.equals(0.0, Angle.subtractByAbsolutes(radians, circular.radians), 1.0e-3);
  }

  @Override
  public final int hashCode() {
    return new HashCodeBuilder(17, 37).append(radians).toHashCode();
  }
}
